package fan.summer.hmoneta.controller;

import fan.summer.hmoneta.database.entity.ddns.DDNSInfo;
import fan.summer.hmoneta.service.ddns.DDNSService;
import fan.summer.hmoneta.util.EncryptionUtil;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import fan.summer.hmoneta.webEntity.resp.ddns.ProviderInfoResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    private final Map<String, String> rsaKey = EncryptionUtil.generateKeyPair();

    @Autowired
    public DDNSController(DDNSService ddnsService) throws Exception {
        try {
            this.ddnsService = ddnsService;
        }catch (Exception e){
            LOG.error("初始化失败生成RSA密钥对失败:{}",e.getMessage());
        }
    }

    @GetMapping("/publicKey")
    public ApiRestResponse<String> getPublicKey() {
        return ApiRestResponse.success(rsaKey.get("publicKey"));
    }

    @GetMapping("/provider/list")
    public ApiRestResponse<List<String>> getProviders() {
        return ApiRestResponse.success(ddnsService.queryAllDDNSProviderNames());
    }

    @GetMapping("/provider/query")
    public ApiRestResponse<List<ProviderInfoResp>> queryAllDDNSProvider() {
        return ApiRestResponse.success(ddnsService.queryAllDDNSProvider());
    }

    @PostMapping("/provider/add")
    public ApiRestResponse<Object> addDDNSProvider(String providerName, String accessKeyId, String accessKeySecret) throws Exception {
        String decryptAccessKeySecret = Arrays.toString(EncryptionUtil.decrypt(rsaKey.get("privateKey"), accessKeySecret));
        DDNSInfo ddnsInfo = new DDNSInfo();
        ddnsInfo.setProviderName(providerName);
        ddnsInfo.setAccessKeyId(accessKeyId);
        ddnsInfo.setAccessKeySecret(decryptAccessKeySecret);
        ddnsService.modifyDdnsProvider(ddnsInfo);
        return ApiRestResponse.success();


    }
}
