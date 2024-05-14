package fan.summer.hmoneta.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AgentServiceTest {

    @Resource
    AgentService agentService;

    @Test
    void isInstallAgentClient() throws IOException, InterruptedException {
        boolean localhost = agentService.isInstallAgentClient("localhost");
        System.out.println(localhost);
    }

    @Test
    void issueConfig() throws JsonProcessingException {
//        boolean b = agentService.issueConfig("localhost");
//        System.out.println(b);
    }
}