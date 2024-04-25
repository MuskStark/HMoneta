package fan.summer.hmoneta.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.WebControllerExceptionEnum;
import fan.summer.hmoneta.common.exception.WebControllerException;
import fan.summer.hmoneta.database.entity.ipPool.IpPool;
import fan.summer.hmoneta.service.IpResourceManagerService;
import fan.summer.hmoneta.service.ServerInfoManagerService;
import fan.summer.hmoneta.util.IpUtil;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import fan.summer.hmoneta.webEntity.req.serverInfo.ServerInfoDetailReq;
import fan.summer.hmoneta.webEntity.resp.serverInfo.ServerInfoDetailResp;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 查询所有服务器信息
     *
     * 该接口不需要接收任何参数，通过调用 {@code serverInfoManagerService.findAllServerInfo()} 方法，
     * 获取所有服务器基本信息，并将其转换为 {@code ServerInfoDetailResp} 类型的列表后，封装到 {@code ApiRestResponse} 中返回。
     *
     * @return {@code ApiRestResponse<List<ServerInfoDetailResp>>} 包含所有服务器详细信息的响应对象。
     */
    @GetMapping("/info")
    public ApiRestResponse<List<ServerInfoDetailResp>> queryAllInfo(){
        return ApiRestResponse.success(BeanUtil.copyToList(serverInfoManagerService.findAllServerInfo(),ServerInfoDetailResp.class));
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
}
