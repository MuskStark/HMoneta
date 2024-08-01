package fan.summer.hmoneta.service.ddns.provider;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyRecordRequest;
import com.tencentcloudapi.dnspod.v20210323.models.ModifyRecordResponse;

/**
 * 腾讯云DDNS实现
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/7/30
 */
public class Tencent extends DDNSProvider {

    private String accessKeyId;
    private String accessKeySecret;

    public Tencent(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public Tencent(){}

    @Override
    public boolean modifyDdns(String domain, String subDomain, String ip) {
        try {
            Credential cred = new Credential(accessKeyId, accessKeySecret);
            DnspodClient client = new DnspodClient(cred, "");
            ModifyRecordRequest req = new ModifyRecordRequest();
            req.setDomain(domain);
            req.setSubDomain(subDomain);
            req.setRecordType("A");
            req.setRecordLine("默认");
            req.setValue(ip);
            ModifyRecordResponse resp = client.ModifyRecord(req);
            return true;
        }catch (TencentCloudSDKException e){
            return false;
        }
    }
}
