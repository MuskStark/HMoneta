package fan.summer.hmoneta.controller;

import fan.summer.hmoneta.database.entity.acme.AcmeChallengeInfoEntity;
import fan.summer.hmoneta.service.acme.AcmeService;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import fan.summer.hmoneta.webEntity.req.acme.AcmeReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2025/2/24
 */
@RestController
@RequestMapping("/hm/acme")
public class AcmeController {
    private final AcmeService acmeService;

    @Autowired
    public AcmeController(AcmeService acmeService) {
        this.acmeService = acmeService;
    }

    @PostMapping("/apply")
    public ApiRestResponse<Long> applyCertificate(@RequestBody AcmeReq req) {
        // TODO：验证域名格式
        return ApiRestResponse.success(acmeService.applyCertificate(req.getDomain(), req.getDnsProvider()));
    }

    @GetMapping("/apply/status")
    public ApiRestResponse<List<AcmeChallengeInfoEntity>> getCertApplyStatus(@RequestParam String taskId) {
        if (Objects.equals(taskId, "0")) {
            return ApiRestResponse.success(acmeService.queryCertApplyByUserId());
        } else {
            return ApiRestResponse.success(Collections.singletonList(acmeService.queryCertApplyStatus(Long.getLong(taskId))));
        }
    }

    @GetMapping("/logInfo")
    public ApiRestResponse<String> getLogInfo() {
        return ApiRestResponse.success(acmeService.getLastInfo());
    }

    @GetMapping("/getCert")
    public ApiRestResponse<String> getCertificate(@RequestParam String domain) throws IOException {
        byte[] cert = acmeService.getCert(domain);
        return ApiRestResponse.success(Base64.getEncoder().encodeToString(cert));
    }
}
