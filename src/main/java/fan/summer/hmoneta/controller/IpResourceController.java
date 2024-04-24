package fan.summer.hmoneta.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.WebControllerExceptionEnum;
import fan.summer.hmoneta.common.exception.WebControllerException;
import fan.summer.hmoneta.database.entity.ipPool.IpPool;
import fan.summer.hmoneta.service.IpResourceManagerService;
import fan.summer.hmoneta.webEntity.common.ApiRestResponse;
import fan.summer.hmoneta.webEntity.req.ipPool.IpPoolModifyReq;
import fan.summer.hmoneta.webEntity.resp.ipPool.IpPoolInfoResp;
import jakarta.annotation.Resource;
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
     * IP池信息操作。
     *
     * @param req 包含IP池修改信息的请求对象。如果请求对象为空或IP池ID为空（非创建操作），则抛出异常。
     * @return 返回一个表示操作成功的API响应对象。
     */
    @RequestMapping("/modify")
    public ApiRestResponse<ObjUtil> modifyIpPool(@RequestBody IpPoolModifyReq req) {
        if(ObjUtil.isEmpty(req)){
            throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_POOL_REQ_EMPTY);
        }
        if(!req.isCreate()){
            if(ObjUtil.isEmpty(req.getPoolId())){
                throw new WebControllerException(WebControllerExceptionEnum.WEB_IP_POOL_REQ_ID_EMPTY);
            }
        }
        ipResourceManagerService.modifyIpPool(req);
        return ApiRestResponse.success();
    }

}
