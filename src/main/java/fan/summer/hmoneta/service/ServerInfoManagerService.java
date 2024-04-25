package fan.summer.hmoneta.service;

import fan.summer.hmoneta.database.repository.ServerInfoDetailRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
