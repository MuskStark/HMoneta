package fan.summer.hmoneta.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.WebControllerExceptionEnum;
import fan.summer.hmoneta.common.exception.WebControllerException;
import fan.summer.hmoneta.database.entity.agent.AgentInfo;
import fan.summer.hmoneta.database.entity.ipPool.IpPool;
import fan.summer.hmoneta.database.entity.serverInfo.ServerInfoDetail;
import fan.summer.hmoneta.service.AgentService;
import fan.summer.hmoneta.service.IpResourceManagerService;
import fan.summer.hmoneta.service.ServerInfoManagerService;
import fan.summer.hmoneta.util.IpUtil;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import fan.summer.hmoneta.webEntity.req.serverInfo.ServerInfoDetailReq;
import fan.summer.hmoneta.webEntity.resp.serverInfo.ServerInfoDetailResp;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


/**
 * HMoneta服务器信息管理控制器
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/25
 */
@RestController
@RequestMapping("/server")
public class ServerInfoManagerController {

    @Resource
    private ServerInfoManagerService serverInfoManagerService;
    @Resource
    private IpResourceManagerService ipResourceManagerService;
    @Resource
    private AgentService agentService;


    /**
     * 查询所有服务器信息的API接口。
     *
     * 该接口通过调用 {@code serverInfoManagerService.findAllServerInfo()} 方法获取所有服务器基本信息，
     * 然后转换为前端可接收的响应格式。如果服务器信息存在，则将每条信息转换为 {@code ServerInfoDetailResp} 对象，
     * 同时通过调用 {@code agentService.findAgentByServerId} 方法补充每个服务器的代理信息，
     * 包括代理ID、在线状态、是否配置了问题以及是否接收到了报告。最后将转换后的信息列表封装在 {@code ApiRestResponse} 中返回。
     * 如果没有服务器信息，则返回一个空的成功响应。
     *
     * @return {@code ApiRestResponse<List<ServerInfoDetailResp>>} 包含服务器信息列表的响应对象，
     *         如果没有服务器信息，则列表为空。
     */
    @GetMapping("/info")
    public ApiRestResponse<List<ServerInfoDetailResp>> queryAllInfo(){
        List<ServerInfoDetail> serverInfos = serverInfoManagerService.findAllServerInfo();
        if (!serverInfos.isEmpty()) {
            ArrayList<ServerInfoDetailResp> respList = new ArrayList<>();
            for (ServerInfoDetail serverInfo : serverInfos) {
                ServerInfoDetailResp resp = new ServerInfoDetailResp();
                BeanUtil.copyProperties(serverInfo, resp);
                AgentInfo serverAgent = agentService.findAgentByServerId(serverInfo.getId());
                if (ObjUtil.isNotNull(serverAgent)) {
                    resp.setAgentId(serverAgent.getAgentId());
                    resp.setIsOnline(serverAgent.getAlive());
                    resp.setIsIssueConfig(serverAgent.getIssueConfig());
                    resp.setIsReceivedReport(serverAgent.getReceivedReport());
                }
                respList.add(resp);
            }
            return ApiRestResponse.success(respList);
        } else {
            return ApiRestResponse.success();
        }
    }

    /**
     * 修改服务器信息
     *
     * @param req 包含服务器详细信息的请求对象，不允许为空。
     *            req包含字段：服务器MAC地址，服务器IP地址，IP池ID。
     * @return 返回一个API响应对象，如果操作成功，则返回一个成功的状态。
     * @throws WebControllerException 如果请求对象为空，MAC地址格式错误，
     *                                  IP地址格式错误，或服务器IP地址不在指定的IP池范围内，
     *                                  则抛出异常。
     */
    @PostMapping("/modify")
    public ApiRestResponse<Object> modifyServerInfo(@RequestBody ServerInfoDetailReq req){
        if(ObjUtil.isEmpty(req)){
            throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_POOL_REQ_EMPTY);
        }
        // 验证服务器信息合法性
        if(!IpUtil.isValidMACAddress(req.getServerMacAddr())){
            throw new WebControllerException(WebControllerExceptionEnum.WEB_SM_REQ_MAC_FORMAT_ERROR);
        }
        if(!IpUtil.isValidIpAddress(req.getServerIpAddr())){
            throw new WebControllerException(WebControllerExceptionEnum.WEB_SM_REQ_IP_FORMAT_ERROR);
        }
        IpPool targetIpPool = ipResourceManagerService.findIpPoolByPoolId(req.getPoolId());
        if(!IpUtil.isInSameSubnet(req.getServerIpAddr(),targetIpPool.getNetworkAddress(),targetIpPool.getMask())){
            throw new WebControllerException(WebControllerExceptionEnum.WEB_SM_REQ_IP_IS_NOT_IN_POOL);
        }
        serverInfoManagerService.modifyServerInfo(req);
        return ApiRestResponse.success();
    }

    /**
     * 通过服务器名称删除服务器信息。
     *
     * @param serverName 服务器名称，不可为空。
     * @return 返回一个表示操作成功的ApiRestResponse对象，其中包含可能的通用响应数据。
     */
    @DeleteMapping("/delete")
    @Transactional
    public ApiRestResponse<Object> deleteServerInfo(@RequestParam @Nonnull String serverName){
        ServerInfoDetail serverInfoDb = serverInfoManagerService.findServerInfoByServerName(serverName);
        if (ObjUtil.isEmpty(serverInfoDb)){
            throw new WebControllerException(WebControllerExceptionEnum.WEB_SM_REQ_SERVER_NOT_EXIST);
        }
        serverInfoManagerService.deleteServerInfo(serverName);
        ipResourceManagerService.deleteIpUsedDetail(serverInfoDb.getPoolId(),serverInfoDb.getServerIpAddr());
        return ApiRestResponse.success();
    }
}
