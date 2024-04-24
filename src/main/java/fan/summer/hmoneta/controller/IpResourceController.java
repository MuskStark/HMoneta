package fan.summer.hmoneta.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.WebControllerExceptionEnum;
import fan.summer.hmoneta.common.exception.WebControllerException;
import fan.summer.hmoneta.database.entity.ipPool.IpPool;
import fan.summer.hmoneta.service.IpResourceManagerService;
import fan.summer.hmoneta.service.ServerInfoManagerService;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import fan.summer.hmoneta.webEntity.req.ipPool.IpPoolModifyReq;
import fan.summer.hmoneta.webEntity.resp.ipPool.IpPoolInfoResp;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * HMoneta Ip资源池管理服务API
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/24
 */
@RestController
@RequestMapping("/pool")
public class IpResourceController {

    @Resource
    private IpResourceManagerService ipResourceManagerService;
    @Resource
    private ServerInfoManagerService serverInfoManagerService;

    /**
     * 查询所有IP池的信息
     *
     * @return {@code ApiRestResponse<List<IpPoolInfoResp>>} 包含所有IP池信息的响应对象。
     */
    @GetMapping("/info")
    public ApiRestResponse<List<IpPoolInfoResp>> queryAllInfo(){
        List<IpPool> allIpPool = ipResourceManagerService.findAllIpPool();
        List<IpPoolInfoResp> result = BeanUtil.copyToList(allIpPool, IpPoolInfoResp.class);
        return ApiRestResponse.success(BeanUtil.copyToList(allIpPool, IpPoolInfoResp.class));
    }

    /**
     * 修改IP池信息。该方法处理来自客户端的IP池修改请求，首先验证请求对象及其IP池ID的有效性，
     * 调用服务层方法修改IP池信息，并根据需要同步更新服务器信息。
     *
     * @param req 包含IP池修改信息的请求对象。如果请求对象为空或IP池ID为空（非创建操作），则抛出异常。
     * @return 返回一个表示操作成功的API响应对象。
     * @throws WebControllerException 如果请求对象或请求对象中的IP池ID为空，抛出此异常。
     */
    @RequestMapping("/modify")
    @Transactional
    public ApiRestResponse<ObjUtil> modifyIpPool(@RequestBody IpPoolModifyReq req) {
        if(ObjUtil.isEmpty(req)){
            throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_POOL_REQ_EMPTY);
        }
        if(!req.isCreate()){
            if(ObjUtil.isEmpty(req.getPoolId())){
                throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_POOL_REQ_ID_EMPTY);
            }
        }
        boolean needModifyServer = ipResourceManagerService.modifyIpPool(req);
        // 判断是否需要同步修改服务器信息
        if(needModifyServer){
            serverInfoManagerService.deleteAllByPoolId(req.getPoolId());
        }
        return ApiRestResponse.success();
    }

}
