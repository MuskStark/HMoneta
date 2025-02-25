package fan.summer.hmoneta.service.acme;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AcmeServiceTest {
    @Resource
    private AcmeService service;


    @Test
    void useDnsChallengeGetCertification() {
        service.useDnsChallengeGetCertification("test7.summer.fan", null);
    }
}