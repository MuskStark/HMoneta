package fan.summer.hmoneta.service.ddns.provider.providers;

import cn.hutool.core.util.ObjUtil;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.dnspod.v20210323.DnspodClient;
import com.tencentcloudapi.dnspod.v20210323.models.*;
import fan.summer.hmoneta.service.ddns.provider.DDNSProvider;
import fan.summer.hmoneta.util.DynamicStreamProcessingUtil;
import lombok.Setter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Tencent() {
    }

    private DnspodClient getCredential() {
        Credential cred = new Credential(accessKeyId, accessKeySecret);
        return new DnspodClient(cred, "");
    }

    @Override
    public Map<String, Object> dnsCheck(String domain, String subDomain) {
        try {
            logInfo("-----------------开始检查DNS信息-----------------");
            logInfo("域名：" + domain);
            logInfo("子域名：" + subDomain);
            Map<String, Object> result = new HashMap<>();
            DnspodClient client = getCredential();
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("dnspod.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            DescribeRecordListRequest dnsReq = new DescribeRecordListRequest();
            dnsReq.setDomain(domain);
            DescribeRecordListResponse dnsResp = client.DescribeRecordList(dnsReq);
            Stream<RecordListItem> recordListItemStream = DynamicStreamProcessingUtil.processWithConditionalParallel(Arrays.asList(dnsResp.getRecordList()));
            Map<String, List<RecordListItem>> collect = recordListItemStream.collect(Collectors.groupingBy(RecordListItem::getName));
            if (collect.containsKey(subDomain)) {
                List<RecordListItem> recordListItems = collect.get(subDomain);
                for (RecordListItem record : recordListItems)
                    if (record.getType().equals("A"))
                        result = Map.of("oldIp", record.getValue(), "recordId", record.getRecordId());
            }
            logInfo("DNS信息：" + result);
            logInfo("-----------------完成DNS信息检查-----------------");
            return result;
        } catch (TencentCloudSDKException e) {
            logError("检查DNS信息失败", e);
            return null;
        }
    }

    @Override
    public boolean modifyDdns(String domain, String subDomain, String ip) {
        try {
            logInfo("-----------------开始修改DNS信息-----------------");
            logInfo("域名：" + domain);
            logInfo("子域名：" + subDomain);
            logInfo("目标Ip：" + ip);
            DnspodClient client = getCredential();
            Map<String, Object> dnsCheckResult = dnsCheck(domain, subDomain);
            if (ObjUtil.isEmpty(dnsCheckResult)) {
                logInfo("不存在DNS信息，开始创建DNS信息");
                CreateRecordRequest createReq = new CreateRecordRequest();
                createReq.setDomain(domain);
                createReq.setSubDomain(subDomain);
                createReq.setRecordType("A");
                createReq.setRecordLine("默认");
                createReq.setValue(ip);
                CreateRecordResponse resp = client.CreateRecord(createReq);
            } else {
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
        } catch (TencentCloudSDKException e) {
            logError("修改DNS信息失败", e);
            return false;
        }
    }

    @Override
    public void deleteDdns(String domain, String subDomain) {
        boolean status = false;
        try {
            logInfo("-----------------开始移除DNS信息-----------------");
            logInfo("域名：" + domain);
            logInfo("子域名：" + subDomain);
            Map<String, Object> dnsCheck = dnsCheck(domain, subDomain);
            if (ObjUtil.isEmpty(dnsCheck)) logError("未检查到供应商存在要删除的DNS记录", null);
            else {
                DnspodClient client = getCredential();
                DeleteRecordRequest req = new DeleteRecordRequest();
                req.setDomain(domain);
                req.setRecordId((Long) dnsCheck.get("recordId"));
                client.DeleteRecord(req);
                status = true;
            }
        } catch (TencentCloudSDKException e) {
            logError("删除DNS记录失败", e);
        } finally {
            logInfo("-----------------移除DNS信息结束-----------------");
        }
    }

}
