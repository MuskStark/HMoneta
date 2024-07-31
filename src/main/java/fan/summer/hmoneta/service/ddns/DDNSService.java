package fan.summer.hmoneta.service.ddns;

import fan.summer.hmoneta.service.ddns.provider.DDNSProvider;
import fan.summer.hmoneta.service.ddns.provider.Tencent;
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

    public void createDdns(String providerName) {

        DDNSProvider provider = null;
        switch (providerName){
        case "tencent":
            provider = new Tencent();
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
        provider.modifyDdns();
    }
}
