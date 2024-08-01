package fan.summer.hmoneta.service.ddns;

import fan.summer.hmoneta.database.entity.ddns.DDNSInfo;
import fan.summer.hmoneta.database.repository.ddns.DDNSInfoRepository;
import fan.summer.hmoneta.service.ddns.provider.DDNSProvider;
import fan.summer.hmoneta.service.ddns.provider.Tencent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public DDNSService(PublicIpChecker publicIpChecker, DDNSInfoRepository ddnsInfoRepository){
        this.publicIpChecker = publicIpChecker;
        this.ddnsInfoRepository = ddnsInfoRepository;
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


    public void createDdns(String providerName, String domain, String subDomain) {
        if (providerName == null || providerName.isEmpty()){
            throw new RuntimeException("DDNS服务商不能为空");
        }
        String ip = publicIpChecker.getPublicIp();
        if (ip == null || ip.isEmpty()){
            throw new RuntimeException("获取公网IP失败");
        }
        DDNSInfo ddnsInfo = ddnsInfoRepository.findByProviderName(providerName);
        DDNSProvider provider = null;
        switch (providerName){
        case "tencent":
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

        boolean b = provider.modifyDdns(domain, subDomain, ip);
    }
}
