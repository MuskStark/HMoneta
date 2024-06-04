package fan.summer.hmoneta.controller;

import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.database.entity.agent.AgentReport;
import fan.summer.hmoneta.service.AgentService;
import fan.summer.hmoneta.webEntity.agent.SystemInfoEntity;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 为Agent提供专属Api接口
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/12
 */
@RestController
@RequestMapping("/hm/master")
public class AgentController {

    @Resource
    private AgentService agentService;
    
    //TODO:更改接口名称为query-report
    @GetMapping("/get-report")
    public ApiRestResponse<Object> getAgentReport(@RequestParam Long serverId) {
        AgentReport agentReportByServerId = agentService.findAgentReportByServerId(serverId);
        return ApiRestResponse.success(agentReportByServerId);
    }

    //TODO:更改接口名称为receive-report
    @PostMapping("/report")
    public ApiRestResponse<Object> receiveAgentStatus(@RequestBody SystemInfoEntity info) {
        if(ObjUtil.isNotEmpty(info)) {
            agentService.receiveAgentReport(info);
        }
        return ApiRestResponse.success();
    }

}
