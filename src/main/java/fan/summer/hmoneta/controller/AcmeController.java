package fan.summer.hmoneta.controller;

import fan.summer.hmoneta.service.acme.AcmeService;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

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

    @GetMapping("/test")
    public ApiRestResponse<Long> test() {
        return ApiRestResponse.success(acmeService.applyCertificate("test7.summer.fan", "TencentCloud"));
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
