package fan.summer.hmoneta.service.ddns;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void modifyDdnsRecorder(DDNSRecorderEntity entity) {
        if (ObjUtil.isEmpty(entity)) throw new BusinessException(BusinessExceptionEnum.DDNS_RECORDER_EMPTY_ERROR);
        DDNSRecorderEntity bySubDomainAndDomain = ddnsRecorderRepository.findBySubDomainAndDomain(entity.getSubDomain(), entity.getDomain());
        if (ObjUtil.isEmpty(bySubDomainAndDomain)) {
            ddnsRecorderRepository.save(entity);
            createDdns(entity.getDomain(), entity.getSubDomain());
        } else throw new BusinessException(BusinessExceptionEnum.DDNS_RECORDER_EXISTS_ERROR);
    }

    public boolean createDdns(String domain, String subDomain) {
        DDNSRecorderEntity recorder = ddnsRecorderRepository.findBySubDomainAndDomain(subDomain, domain);
        if (ObjUtil.isEmpty(recorder)) throw new RuntimeException(subDomain + "." + domain + "域名无DDNS记录");
        String ip = publicIpChecker.getPublicIp();
        if (ip == null || ip.isEmpty()) throw new RuntimeException("获取公网IP失败");
        DDNSProvider provider = providerFactory.generatorProvider(DDNSProvidersSelectEnum.valueOf(recorder.getProviderName()));
        // TODO:将更新状态记录至数据库
        boolean status = provider.DDNSOperation(domain, subDomain, ip);
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

    public List<DDNSRecorderEntity> queryAllDDNSRecorder() {
        return ddnsRecorderRepository.findAll();
    }

    public List<DDNSUpdateRecorderEntity> queryAllDDNSUpdateRecorder() {
        return ddnsUpdateRecorderRepository.findAll();
    }

    public List<DDNSUpdateRecorderEntity> queryAllDDNSUpdateRecorderByProviderName(String providerName) {
        return ddnsUpdateRecorderRepository.findAllByProviderName(providerName);
    }

    public DDNSUpdateRecorderEntity queryDDNSUpdateRecorderByDomain(String domain, String subDomain) {
        return ddnsUpdateRecorderRepository.findByDomainAndSubDomain(domain, subDomain);
    }

}
