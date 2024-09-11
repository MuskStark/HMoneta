package fan.summer.hmoneta.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.error.WebControllerExceptionEnum;
import fan.summer.hmoneta.common.exception.WebControllerException;
import fan.summer.hmoneta.database.entity.ipPool.IpPool;
import fan.summer.hmoneta.service.IpResourceManagerService;
import fan.summer.hmoneta.service.ServerInfoManagerService;
import fan.summer.hmoneta.util.IpUtil;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import fan.summer.hmoneta.webEntity.req.ipPool.IpPoolModifyReq;
import fan.summer.hmoneta.webEntity.resp.ipPool.IpPoolInfoResp;
import fan.summer.hmoneta.webEntity.resp.ipPool.IpPoolSelectValueResp;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * HMoneta Ip资源池管理服务API
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/24
 */
@RestController
@RequestMapping("/hm/pool")
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
        return ApiRestResponse.success(BeanUtil.copyToList(allIpPool, IpPoolInfoResp.class));
    }

    @GetMapping("/generate")
    public ApiRestResponse<String> generateIpAddr(@RequestParam @Nonnull Long poolId, @RequestParam boolean isRandom){
        String ip = ipResourceManagerService.issueIpAddrByPoolId(poolId, isRandom);
        return ApiRestResponse.success(ip);
    }

    /**
     * 查询所有可选的IP池值
     *
     * 该接口不需要接收任何参数，通过调用 {@code ipResourceManagerService.findAllIpPoolSelectValue()}
     * 方法从IP资源管理服务中查询所有可用的IP池选择值，并将这些值封装在 {@code ApiRestResponse} 对象中返回。
     *
     * @return {@code ApiRestResponse<List<IpPoolSelectValueResp>>} - 包含所有IP池选择值的列表的API响应。
     */
    @GetMapping("/selectValue")
    public ApiRestResponse<List<IpPoolSelectValueResp>> queryAllSelectValue(){
        List<IpPoolSelectValueResp> allIpPool = ipResourceManagerService.findAllIpPoolSelectValue();
        return ApiRestResponse.success(allIpPool);
    }

    /**
     * 修改IP池信息。该方法处理来自客户端的IP池修改请求，首先验证请求对象及其IP池ID的有效性，
     * 调用服务层方法修改IP池信息，并根据需要同步更新服务器信息。
     *
     * @param req 包含IP池修改信息的请求对象。如果请求对象为空或IP池ID为空（非创建操作），则抛出异常。
     * @return 返回一个表示操作成功的API响应对象。
     * @throws WebControllerException 如果请求对象或请求对象中的IP池ID为空，抛出此异常。
     */
    @PostMapping("/modify")
    @Transactional
    public ApiRestResponse<ObjUtil> modifyIpPool(@RequestBody IpPoolModifyReq req) {
        if(ObjUtil.isEmpty(req)){
            throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_POOL_REQ_EMPTY);
        }
        // 判断Ip地址合法性
        boolean baseNetInfoValid = IpUtil.isValidNetworkAddress(req.getNetworkAddress()) & IpUtil.isValidMask(req.getMask());
        if(baseNetInfoValid){
            if(!ObjUtil.isEmpty(req.getStartAddr())){
                if(!IpUtil.isValidIpAddress(req.getStartAddr())){
                    throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_FORMAT_ERROR);
                }else{
                    if(!IpUtil.isInSameSubnet(req.getStartAddr(),req.getNetworkAddress(),req.getMask())){
                        throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_NOT_IN_SAME_NETWORK);
                    }
                }

            }
            if(!ObjUtil.isEmpty(req.getEndAddr())){
                if(!IpUtil.isValidIpAddress(req.getEndAddr())){
                    throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_FORMAT_ERROR);
                }else{
                    if(!IpUtil.isInSameSubnet(req.getEndAddr(),req.getNetworkAddress(),req.getMask())){
                        throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_NOT_IN_SAME_NETWORK);
                    }
                }

            }

        }else {
            throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_FORMAT_ERROR);
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

    /**
     * 删除IP池
     *
     * @param req 包含要删除的IP池信息的请求体，不能为空且必须包含有效的poolId。
     * @return 返回一个表示操作成功的API响应体，包含操作结果的详细信息。
     */
    @DeleteMapping("/delete/{poolId}")
    @Transactional
    public ApiRestResponse<ObjUtil> deleteIpPool(@PathVariable Long poolId) {
        if(ObjUtil.isEmpty(poolId)){
            throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_POOL_REQ_ID_EMPTY);
        }
        ipResourceManagerService.deleteIpPool(poolId);
        serverInfoManagerService.deleteAllByPoolId(poolId);
        return ApiRestResponse.success();
    }

}
