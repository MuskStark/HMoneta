package fan.summer.hmoneta.controller;

import fan.summer.hmoneta.webEntity.agent.SystemInfoEntity;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 为Agent提供专属Api接口
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/12
 */
@Controller
@RequestMapping("/agent/api")
public class AgentController {

    @PostMapping("/report")
    public ApiRestResponse<Object> receiveAgentStatus(@RequestBody SystemInfoEntity info) {
        assert info != null;
        System.out.println(info);
        return ApiRestResponse.success();
    }

}
