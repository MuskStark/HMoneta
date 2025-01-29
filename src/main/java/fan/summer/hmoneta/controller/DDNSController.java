package fan.summer.hmoneta.controller;

import cn.hutool.core.bean.BeanUtil;
import fan.summer.hmoneta.common.enums.DDNSProvidersSelectEnum;
import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
import fan.summer.hmoneta.database.entity.ddns.DDNSProviderEntity;
import fan.summer.hmoneta.database.entity.ddns.DDNSRecorderEntity;
import fan.summer.hmoneta.database.entity.ddns.DDNSUpdateRecorderEntity;
import fan.summer.hmoneta.service.ddns.DDNSService;
import fan.summer.hmoneta.service.ddns.provider.ProviderService;
import fan.summer.hmoneta.util.EncryptionUtil;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import fan.summer.hmoneta.webEntity.req.ddns.DDNSProviderInfoReq;
import fan.summer.hmoneta.webEntity.req.ddns.DDNSRecorderReq;
import fan.summer.hmoneta.webEntity.resp.ddns.DDNSUpdateRecorderResp;
import fan.summer.hmoneta.webEntity.resp.ddns.ProviderInfoResp;
import fan.summer.hmoneta.webEntity.resp.ddns.ProviderSelectorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/8/6
 */
@RestController
@RequestMapping("/hm/ddns")
public class DDNSController {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private DDNSService ddnsService;
    private final ProviderService providerService;
    private final Map<String, String> rsaKey = EncryptionUtil.generateKeyPair();

    @Autowired
    public DDNSController(DDNSService ddnsService, ProviderService providerService) throws Exception {
        try {
            this.ddnsService = ddnsService;
            this.providerService = providerService;
        } catch (Exception e) {
            LOG.error("初始化失败生成RSA密钥对失败:{}", e.getMessage());
        }
    }

    @GetMapping("/publicKey")
    public ApiRestResponse<String> getPublicKey() {
        return ApiRestResponse.success(rsaKey.get("publicKey"));
    }

    @GetMapping("/provider/selector")
    public ApiRestResponse<List<ProviderSelectorInfo>> getProviders() {
        return ApiRestResponse.success(providerService.getProviderSelectorInfo());
    }

    @GetMapping("/provider/query")
    public ApiRestResponse<List<ProviderInfoResp>> queryAllDDNSProvider() {
        List<ProviderInfoResp> providerInfoResps = providerService.queryAllDDNSProvider();
        // TODO: 空值判断
        if (providerInfoResps == null || providerInfoResps.isEmpty())
            return ApiRestResponse.error(BusinessExceptionEnum.DDNS_PROVIDER_LIST_EMPTY);
        else {
            for (ProviderInfoResp providerInfoResp : providerInfoResps)
                providerInfoResp.setLabel(DDNSProvidersSelectEnum.valueOf(providerInfoResp.getProviderName()).getLabel());
            return ApiRestResponse.success(providerInfoResps);
        }
    }

    @PostMapping("/provider/add")
    public ApiRestResponse<Object> addDDNSProvider(@RequestBody DDNSProviderInfoReq providerInfoReq) throws Exception {
        DDNSProviderEntity ddnsProviderEntity = new DDNSProviderEntity();
        ddnsProviderEntity.setProviderName(providerInfoReq.getProviderName());
        ddnsProviderEntity.setAccessKeyId(providerInfoReq.getAccessKeyId());
        String accessKey = new String(EncryptionUtil.decrypt(rsaKey.get("privateKey"), providerInfoReq.getAccessKeySecret()), StandardCharsets.UTF_8);
        ddnsProviderEntity.setAccessKeySecret(accessKey);
        providerService.modifyDdnsProvider(ddnsProviderEntity);
        return ApiRestResponse.success();
    }

    @PostMapping("/record/modify")
    public ApiRestResponse<Object> modifyDDNSProvider(@RequestBody DDNSRecorderReq req) {
        DDNSRecorderEntity ddnsRecorderEntity = BeanUtil.copyProperties(req, DDNSRecorderEntity.class);
        ddnsService.modifyDdnsRecorder(ddnsRecorderEntity);
        return ApiRestResponse.success();
    }

    @GetMapping("/record")
    public ApiRestResponse<List<DDNSUpdateRecorderResp>> queryRecordInfoByProviderName(@RequestParam("providerName") String providerName) {
        List<DDNSUpdateRecorderEntity> ddnsUpdateRecorderEntities = ddnsService.queryAllDDNSUpdateRecorderByProviderName(providerName);
        return ApiRestResponse.success(BeanUtil.copyToList(ddnsUpdateRecorderEntities, DDNSUpdateRecorderResp.class));
    }
}
