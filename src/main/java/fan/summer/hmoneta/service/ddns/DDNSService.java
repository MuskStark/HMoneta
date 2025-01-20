package fan.summer.hmoneta.service.ddns;

import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.DDNSProvidersSelectEnum;
import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.ddns.DDNSInfo;
import fan.summer.hmoneta.database.entity.ddns.DDNSRecorderEntity;
import fan.summer.hmoneta.database.entity.ddns.DDNSUpdateRecorderEntity;
import fan.summer.hmoneta.database.repository.ddns.DDNSInfoRepository;
import fan.summer.hmoneta.database.repository.ddns.DDNSRecorderRepository;
import fan.summer.hmoneta.database.repository.ddns.DDNSUpdateRecorderRepository;
import fan.summer.hmoneta.service.ddns.provider.DDNSProvider;
import fan.summer.hmoneta.service.ddns.provider.Tencent;
import fan.summer.hmoneta.webEntity.resp.ddns.ProviderInfoResp;
import fan.summer.hmoneta.webEntity.resp.ddns.ProviderSelectorInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final DDNSInfoRepository ddnsInfoRepository;
    private final DDNSUpdateRecorderRepository ddnsUpdateRecorderRepository;
    private final DDNSRecorderRepository ddnsRecorderRepository;

    @Autowired
    public DDNSService(PublicIpChecker publicIpChecker,
                       DDNSInfoRepository ddnsInfoRepository,
                       DDNSUpdateRecorderRepository ddnsUpdateRecorderRepository,
                       DDNSRecorderRepository recorderRepository){
        this.publicIpChecker = publicIpChecker;
        this.ddnsInfoRepository = ddnsInfoRepository;
        this.ddnsUpdateRecorderRepository = ddnsUpdateRecorderRepository;
        this.ddnsRecorderRepository = recorderRepository;
    }

    public List<ProviderInfoResp> queryAllDDNSProvider() {
        List<DDNSInfo> allProviders = ddnsInfoRepository.findAll();
        if (allProviders.isEmpty()){
            return null;
        }else{
            List<ProviderInfoResp> providerInfos = new ArrayList<>();
            for (DDNSInfo ddnsInfo : allProviders){
                ProviderInfoResp providerInfoResp = new ProviderInfoResp();
                providerInfoResp.setProviderName(ddnsInfo.getProviderName());
                providerInfoResp.setAccessKeyId(ddnsInfo.getAccessKeyId());
                providerInfos.add(providerInfoResp);
            }
            return providerInfos;
        }
    }

    public List<ProviderSelectorInfo> getProviderSelectorInfo() {
        List<ProviderSelectorInfo> selectors = new ArrayList<>();
        DDNSProvidersSelectEnum[] values = DDNSProvidersSelectEnum.values();
        for (DDNSProvidersSelectEnum value : values) {
            selectors.add(new ProviderSelectorInfo(value.getName(), value.getLabel()));
        }
        return selectors;
    }

    public void modifyDdnsProvider(DDNSInfo ddnsInfo) {
        if (ddnsInfo == null){
            throw new RuntimeException("DDNS供应商信息不能为空");
        }
        DDNSInfo byProviderName = ddnsInfoRepository.findByProviderName(ddnsInfo.getProviderName());
        if (byProviderName != null){
            ddnsInfo.setDdnsId(byProviderName.getDdnsId());
        }
        ddnsInfoRepository.save(ddnsInfo);
    }

    public void modifyDdnsRecorder(DDNSRecorderEntity entity){
        if(ObjUtil.isEmpty(entity)){
            throw new BusinessException(BusinessExceptionEnum.DDNS_RECORDER_EMPTY_ERROR);
        }
        DDNSRecorderEntity bySubDomainAndDomain = ddnsRecorderRepository.findBySubDomainAndDomain(entity.getSubDomain(), entity.getDomain());
        if (ObjUtil.isEmpty(bySubDomainAndDomain)) {
            ddnsRecorderRepository.save(entity);
            createDdns(entity.getDomain(), entity.getSubDomain());
        }else {
            throw new BusinessException(BusinessExceptionEnum.DDNS_RECORDER_EXISTS_ERROR);
        }
    }


    public boolean createDdns(String domain, String subDomain) {
        DDNSRecorderEntity recorder = ddnsRecorderRepository.findBySubDomainAndDomain(subDomain, domain);
        if (ObjUtil.isEmpty(recorder)){
            throw new RuntimeException(subDomain+"."+domain+"域名无DDNS记录");
        }
        String ip = publicIpChecker.getPublicIp();
        if (ip == null || ip.isEmpty()){
            throw new RuntimeException("获取公网IP失败");
        }
        DDNSInfo ddnsInfo = ddnsInfoRepository.findByProviderName(recorder.getProviderName());
        DDNSProvider provider;
        switch (DDNSProvidersSelectEnum.valueOf(recorder.getProviderName())){
            case TencentCloud:
                provider = new Tencent(ddnsInfo.getAccessKeyId(),ddnsInfo.getAccessKeySecret());
            break;
//        case "aliyun":
//            provider = new Aliyun();
//            break;
//        case "dnspod":
//            provider = new Dnspod();
//            break;
        default:
            throw new RuntimeException("不支持的DDNS服务商");
        }
        // TODO:将更新状态记录至数据库
        boolean status = provider.DDNSOperation(domain, subDomain, ip);
        if(status){
            DDNSUpdateRecorderEntity byDomain = ddnsUpdateRecorderRepository.findByDomainAndSubDomain(domain, subDomain);
            if(byDomain == null){
                DDNSUpdateRecorderEntity recorderEntity = new DDNSUpdateRecorderEntity();
                recorderEntity.setRecorderId(recorder.getId());
                recorderEntity.setDomain(domain);
                recorderEntity.setSubDomain(subDomain);
                recorderEntity.setProviderName(recorderEntity.getProviderName());
                recorderEntity.setIp(ip);
                recorderEntity.setStatus(true);
                ddnsUpdateRecorderRepository.save(recorderEntity);
            }else {
                if(!byDomain.getIp().equals(ip)){
                    byDomain.setIp(ip);
                    if(!byDomain.getProviderName().equals(recorder.getProviderName())){
                        byDomain.setProviderName(recorder.getProviderName());
                    }
                    byDomain.setStatus(true);
                    ddnsUpdateRecorderRepository.save(byDomain);
                }else {
                    if(!byDomain.getStatus()){
                        byDomain.setStatus(true);
                        ddnsUpdateRecorderRepository.save(byDomain);
                    }
                }
            }

        }
        return status;
    }

    public List<DDNSUpdateRecorderEntity> queryAllDDNSUpdateRecorder() {
        return ddnsUpdateRecorderRepository.findAll();
    }
    public List<DDNSUpdateRecorderEntity> queryAllDDNSUpdateRecorderByProviderName(String providerName){
        return ddnsUpdateRecorderRepository.findAllByProviderName(providerName);
    }
}
