package fan.summer.hmoneta.service.ddns;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.DDNSProvidersSelectEnum;
import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.ddns.DDNSRecorderEntity;
import fan.summer.hmoneta.database.entity.ddns.DDNSUpdateRecorderEntity;
import fan.summer.hmoneta.database.repository.ddns.DDNSRecorderRepository;
import fan.summer.hmoneta.database.repository.ddns.DDNSUpdateRecorderRepository;
import fan.summer.hmoneta.service.ddns.provider.DDNSProvider;
import fan.summer.hmoneta.service.ddns.provider.ProviderFactory;
import fan.summer.hmoneta.service.ddns.provider.ProviderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 提供DDNS服务
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/7/31
 */
@Service
public class DDNSService {

    private final PublicIpChecker publicIpChecker;
    private final ProviderFactory providerFactory;

    private final DDNSUpdateRecorderRepository ddnsUpdateRecorderRepository;
    private final DDNSRecorderRepository ddnsRecorderRepository;

    private final ProviderService providerService;

    @Autowired
    public DDNSService(PublicIpChecker publicIpChecker,
                       ProviderFactory providerFactory,
                       DDNSUpdateRecorderRepository ddnsUpdateRecorderRepository,
                       DDNSRecorderRepository recorderRepository,
                       ProviderService providerService) {
        this.publicIpChecker = publicIpChecker;
        this.providerFactory = providerFactory;

        this.ddnsUpdateRecorderRepository = ddnsUpdateRecorderRepository;
        this.ddnsRecorderRepository = recorderRepository;

        this.providerService = providerService;
    }

    /*
    DDNSRecorder相关方法
     */
    public List<DDNSRecorderEntity> queryAllDDNSRecorder() {
        return ddnsRecorderRepository.findAll();
    }

    @Transactional
    public void modifyDdnsRecorder(DDNSRecorderEntity entity) {
        if (ObjUtil.isEmpty(entity)) throw new BusinessException(BusinessExceptionEnum.DDNS_RECORDER_EMPTY_ERROR);
        if (!ObjUtil.isEmpty(ddnsRecorderRepository.findBySubDomainAndDomain(entity.getSubDomain(), entity.getDomain())))
            throw new BusinessException(BusinessExceptionEnum.DDNS_RECORDER_EXISTS_ERROR);
        if (ObjUtil.isEmpty(entity.getId())) {
            // 新增
            ddnsRecorderRepository.save(entity);
            createDdns(entity.getDomain(), entity.getSubDomain());
        } else if (ddnsRecorderRepository.findById(entity.getId()).isPresent()) {
            // 修改
            DDNSRecorderEntity oldDdnsRecorderEntity = ddnsRecorderRepository.findById(entity.getId()).get();
            // 移除原UpdateRecorder
            deleteUpdateRecorderInfoByRecorderId(oldDdnsRecorderEntity.getId());
            // 移除供应商解析记录
            deleteDdns(oldDdnsRecorderEntity.getDomain(), oldDdnsRecorderEntity.getSubDomain());
            DDNSRecorderEntity newDdnsRecorderEntity = BeanUtil.copyProperties(oldDdnsRecorderEntity, DDNSRecorderEntity.class);
            newDdnsRecorderEntity.setDomain(entity.getDomain());
            newDdnsRecorderEntity.setSubDomain(entity.getSubDomain());
            newDdnsRecorderEntity.setProviderName(entity.getProviderName());
            ddnsRecorderRepository.save(newDdnsRecorderEntity);
            createDdns(newDdnsRecorderEntity.getDomain(), newDdnsRecorderEntity.getSubDomain());
        }
    }

    @Transactional
    public void deleteRecorder(Long recorderId) {
        if (ObjUtil.isEmpty(recorderId)) throw new BusinessException(BusinessExceptionEnum.DDNS_RECORDER_EMPTY_ERROR);
        Optional<DDNSRecorderEntity> byId = ddnsRecorderRepository.findById(recorderId);
        if (byId.isEmpty())
            throw new BusinessException(BusinessExceptionEnum.DDNS_RECORDER_EMPTY_ERROR);
        DDNSRecorderEntity ddnsRecorderEntity = byId.get();
        deleteDdns(ddnsRecorderEntity.getDomain(), ddnsRecorderEntity.getSubDomain());
        ddnsRecorderRepository.deleteById(recorderId);
        ddnsUpdateRecorderRepository.deleteByRecorderId(recorderId);
    }

    /*
    DDNSUpdateRecorder相关服务
     */

    public List<DDNSUpdateRecorderEntity> queryAllDDNSUpdateRecorder() {
        return ddnsUpdateRecorderRepository.findAll();
    }

    public List<DDNSUpdateRecorderEntity> queryAllDDNSUpdateRecorderByProviderName(String providerName) {
        return ddnsUpdateRecorderRepository.findAllByProviderName(providerName);
    }

    public DDNSUpdateRecorderEntity queryDDNSUpdateRecorderByDomain(String domain, String subDomain) {
        return ddnsUpdateRecorderRepository.findByDomainAndSubDomain(domain, subDomain);
    }

    private void deleteUpdateRecorderInfoByRecorderId(Long recorderId) throws BusinessException {
        if (ddnsUpdateRecorderRepository.findByRecorderId(recorderId).isPresent())
            ddnsUpdateRecorderRepository.deleteByRecorderId(recorderId);
        else throw new BusinessException(BusinessExceptionEnum.DDNS_RECORDER_UPDATE_NULL_RECORDER_ID_ERROR);
    }

    /*
    其他方法
     */

    public boolean createDdns(String domain, String subDomain) {
        DDNSRecorderEntity recorder = ddnsRecorderRepository.findBySubDomainAndDomain(subDomain, domain);
        if (ObjUtil.isEmpty(recorder)) throw new RuntimeException(subDomain + "." + domain + "域名无DDNS记录");
        String ip = publicIpChecker.getPublicIp();
        if (ip == null || ip.isEmpty()) throw new RuntimeException("获取公网IP失败");
        DDNSProvider provider = providerFactory.generatorProvider(DDNSProvidersSelectEnum.valueOf(recorder.getProviderName()));
        boolean status = provider.modifyDdns(domain, subDomain, ip);
        if (status) {
            DDNSUpdateRecorderEntity byDomain = ddnsUpdateRecorderRepository.findByDomainAndSubDomain(domain, subDomain);
            if (byDomain == null) {
                DDNSUpdateRecorderEntity recorderEntity = new DDNSUpdateRecorderEntity();
                recorderEntity.setRecorderId(recorder.getId());
                recorderEntity.setDomain(domain);
                recorderEntity.setSubDomain(subDomain);
                recorderEntity.setProviderName(recorder.getProviderName());
                recorderEntity.setIp(ip);
                recorderEntity.setStatus(true);
                ddnsUpdateRecorderRepository.save(recorderEntity);
            } else if (!byDomain.getIp().equals(ip)) {
                byDomain.setIp(ip);
                if (!byDomain.getProviderName().equals(recorder.getProviderName()))
                    byDomain.setProviderName(recorder.getProviderName());
                byDomain.setStatus(true);
                ddnsUpdateRecorderRepository.save(byDomain);
            } else if (!byDomain.getStatus()) {
                byDomain.setStatus(true);
                ddnsUpdateRecorderRepository.save(byDomain);
            }

        }
        return status;
    }

    public void deleteDdns(String domain, String subDomain) {
        DDNSRecorderEntity recorder = ddnsRecorderRepository.findBySubDomainAndDomain(subDomain, domain);
        if (ObjUtil.isEmpty(recorder)) throw new RuntimeException(subDomain + "." + domain + "域名无DDNS记录");
        DDNSProvider provider = providerFactory.generatorProvider(DDNSProvidersSelectEnum.valueOf(recorder.getProviderName()));
        provider.deleteDdns(domain, subDomain);
    }


}
