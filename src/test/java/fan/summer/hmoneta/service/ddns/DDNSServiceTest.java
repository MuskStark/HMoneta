package fan.summer.hmoneta.service.ddns;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DDNSServiceTest {

    @Resource
    private DDNSService ddnsService;

    @Test
    protected void testService(){
        ddnsService.createDdns("summer.fan", "test");
    }

}