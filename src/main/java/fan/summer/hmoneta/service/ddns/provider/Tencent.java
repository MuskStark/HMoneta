package fan.summer.hmoneta.service.ddns.provider;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.*;

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
            String oldIp = null;
            Credential cred = new Credential(accessKeyId, accessKeySecret);
            DnspodClient client = new DnspodClient(cred, "");
            // 检查DDNS服务商域名现在ip地址是否与当前ip一致
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            DescribeRecordListRequest dnsReq = new DescribeRecordListRequest();
            dnsReq.setDomain(domain);
            DescribeRecordListResponse dnsResp = client.DescribeRecordList(dnsReq);
            for (RecordListItem record : dnsResp.getRecordList()) {
                if (record.getName().equals(subDomain) && record.getType().equals("A")) {
                    oldIp = record.getValue();
                }
            }
            assert oldIp != null;
            // 如果不一致则修改
            if (!oldIp.equals(ip)) {
                ModifyRecordRequest req = new ModifyRecordRequest();
                req.setDomain(domain);
                req.setSubDomain(subDomain);
                req.setRecordType("A");
                req.setRecordLine("默认");
                req.setValue(ip);
                ModifyRecordResponse resp = client.ModifyRecord(req);
            }
            return true;
        }catch (TencentCloudSDKException e){
            return false;
        }
    }
}
