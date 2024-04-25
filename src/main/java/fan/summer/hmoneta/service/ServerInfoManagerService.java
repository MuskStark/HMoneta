package fan.summer.hmoneta.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.serverInfo.ServerInfoDetail;
import fan.summer.hmoneta.database.repository.ServerInfoDetailRepository;
import fan.summer.hmoneta.webEntity.req.serverInfo.ServerInfoDetailReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理HMoneta系统中服务器资源
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/23
 */
@Service
public class ServerInfoManagerService {

    @Resource
    private ServerInfoDetailRepository serverInfoDetailRepository;

    /**
     * 查找并返回所有服务器信息的详细列表。
     *
     * @return List<ServerInfoDetail> - 服务器信息详细列表。该列表包含了所有服务器的详细信息。
     */
    public List<ServerInfoDetail> findAllServerInfo() {
        return serverInfoDetailRepository.findAll();
    }

    /**
     * 修改服务器信息。
     *
     * @param req 包含服务器信息详情的请求对象，其中必须包含服务器名称。
     * @throws BusinessException 如果尝试更新的服务器名称已存在于数据库中，则抛出此异常。
     */
    @Transactional
    public void modifyServerInfo(ServerInfoDetailReq req) {
        ServerInfoDetail db = serverInfoDetailRepository.findByServerName(req.getServerName());
        if(ObjUtil.isNotEmpty(db)){
            throw new BusinessException(BusinessExceptionEnum.SM_SERVER_EXISTS_ERROR);
        }
        ServerInfoDetail serverInfoDetail = BeanUtil.copyProperties(req, ServerInfoDetail.class);
        serverInfoDetail.setIsAlive(false);
        serverInfoDetailRepository.save(serverInfoDetail);
    }

    /**
     * 根据池ID删除所有关联的服务器信息。
     *
     * @param poolId 池ID，用于标识要删除的服务器信息所属的池。
     */
    @Transactional
    public void deleteAllByPoolId(Long poolId) {
        if(!serverInfoDetailRepository.findAllByPoolId(poolId).isEmpty()) {
            serverInfoDetailRepository.deleteAllByPoolId(poolId);
        }
    }

}
