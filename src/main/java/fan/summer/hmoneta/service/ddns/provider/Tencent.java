package fan.summer.hmoneta.service.ddns.provider;

import cn.hutool.core.util.ObjUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.*;
import fan.summer.hmoneta.task.scheduled.AgentScanTask;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * 腾讯云DDNS实现
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/7/30
 */
@Setter
public class Tencent extends DDNSProvider {


    private String accessKeyId;
    private String accessKeySecret;

    public Tencent(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
    }

    public Tencent(){}

    private DnspodClient getCredential() {
        Credential cred = new Credential(accessKeyId, accessKeySecret);
        return new DnspodClient(cred, "");
    }

    @Override
    protected Map<String,Object> dnsCheck(String domain, String subDomain){
        try {
            logInfo("-----------------开始检查DNS信息-----------------");
            logInfo("域名：" + domain);
            logInfo("子域名：" + subDomain);
            Map<String ,Object> result = null;
            DnspodClient client = getCredential();
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            DescribeRecordListRequest dnsReq = new DescribeRecordListRequest();
            dnsReq.setDomain(domain);
            DescribeRecordListResponse dnsResp = client.DescribeRecordList(dnsReq);
            for (RecordListItem record : dnsResp.getRecordList()) {
                if (record.getName().equals(subDomain) && record.getType().equals("A")) {
                    String oldIp = record.getValue();
                    Long recordId = record.getRecordId();
                    result = Map.of("oldIp", oldIp, "recordId", recordId);
                }
            }
            logInfo("DNS信息：" + result.toString());
            logInfo("-----------------完成DNS信息检查-----------------");
            return result;
        }catch (TencentCloudSDKException e){
            logError("检查DNS信息失败", e);
            return null;
        }
    }
    @Override
    protected boolean modifyDdns(Map<String, Object> dnsCheckResult, String domain, String subDomain, String ip) {
        try {
            logInfo("-----------------开始修改DNS信息-----------------");
            logInfo("域名：" + domain);
            logInfo("子域名：" + subDomain);
            logInfo("目标Ip：" + ip);
            DnspodClient client = getCredential();
            if (ObjUtil.isEmpty(dnsCheckResult)) {
                logInfo("不存在DNS信息，开始创建DNS信息");
                CreateRecordRequest createReq = new CreateRecordRequest();
                createReq.setDomain(domain);
                createReq.setSubDomain(subDomain);
                createReq.setRecordType("A");
                createReq.setRecordLine("默认");
                createReq.setValue(ip);
                CreateRecordResponse resp = client.CreateRecord(createReq);
            }else {
                logInfo("存在DNS信息，开始修改DNS信息");
                String oldIp = dnsCheckResult.get("oldIp").toString();
                Long recordId = Long.parseLong(dnsCheckResult.get("recordId").toString());
                // 如果不一致则修改
                if (!oldIp.equals(ip)) {
                    ModifyRecordRequest req = new ModifyRecordRequest();
                    req.setRecordId(recordId);
                    req.setDomain(domain);
                    req.setSubDomain(subDomain);
                    req.setRecordType("A");
                    req.setRecordLine("默认");
                    req.setValue(ip);
                    ModifyRecordResponse resp = client.ModifyRecord(req);
                }
            }
            logInfo("-----------------完成DNS信息修改-----------------");
            return true;
        }catch (TencentCloudSDKException e){
            logError("修改DNS信息失败", e);
            return false;
        }
    }
}
