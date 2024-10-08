package fan.summer.hmoneta.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.common.enums.error.BusinessExceptionEnum;
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
    @Resource
    private AgentService agentService;

    /**
     * 查找并返回所有服务器信息的详细列表。
     *
     * @return List<ServerInfoDetail> - 服务器信息详细列表。该列表包含了所有服务器的详细信息。
     */
    public List<ServerInfoDetail> findAllServerInfo() {
        return serverInfoDetailRepository.findAll();

    }

    /**
     * 根据服务器名称查找服务器信息详情。
     *
     * @param serverName 服务器的名称，用于查询特定服务器的信息。
     * @return 返回匹配给定服务器名称的服务器信息详情对象。如果没有找到匹配的服务器信息，则返回null。
     */
    public ServerInfoDetail findServerInfoByServerName(String serverName) {
        return serverInfoDetailRepository.findByServerName(serverName);
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
        if (req.isCreate()) {
            if (ObjUtil.isNotEmpty(db)) {
                throw new BusinessException(BusinessExceptionEnum.SM_SERVER_EXISTS_ERROR);
            }
            db = new ServerInfoDetail();
        } else {
            if (ObjUtil.isEmpty(db)) {
                throw new BusinessException(BusinessExceptionEnum.SM_SERVER_NOT_EXISTS_ERROR);
            }
        }
        BeanUtil.copyProperties(req, db);
        db.setIsAlive(false);
        serverInfoDetailRepository.save(db);
    }

    /**
     * 保存所有服务器信息到数据库。
     *
     * @param serverInfoDetailList 服务器信息详情列表，不可为空。包含待保存的服务器的详细信息。
     *                             利用Spring Data JPA的saveAll方法批量保存至数据库。
     */
    @Transactional
    public void saveAllServerInfo(List<ServerInfoDetail> serverInfoDetailList) {
        serverInfoDetailRepository.saveAll(serverInfoDetailList);
    }

    /**
     * 删除指定服务器信息
     *
     * @param serverName 服务器名称
     * @throws BusinessException 如果服务器不存在，抛出此异常
     */
    @Transactional
    public void deleteServerInfo(String serverName) {
        ServerInfoDetail db = serverInfoDetailRepository.findByServerName(serverName);
        if (ObjUtil.isEmpty(db)) {
            throw new BusinessException(BusinessExceptionEnum.SM_SERVER_NOT_EXISTS_ERROR);
        }
        serverInfoDetailRepository.deleteByServerName(serverName);
    }

    /**
     * 根据池ID删除所有关联的服务器信息。
     *
     * @param poolId 池ID，用于标识要删除的服务器信息所属的池。
     */
    @Transactional
    public void deleteAllByPoolId(Long poolId) {
        if (!serverInfoDetailRepository.findAllByPoolId(poolId).isEmpty()) {
            serverInfoDetailRepository.deleteAllByPoolId(poolId);
        }
    }

}
