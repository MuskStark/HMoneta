package fan.summer.hmoneta.service;

import fan.summer.hmoneta.database.entity.ipPool.IpPool;
import fan.summer.hmoneta.database.repository.IpPoolRepository;
import fan.summer.hmoneta.database.repository.IpPoolUsedDetailRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 管理HMoneta系统中IP资源
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/23
 */
@Service
public class IpResourceManagerService {

    @Resource
    private IpPoolRepository ipPoolRepository;
    @Resource
    private IpPoolUsedDetailRepository ipPoolUsedDetailRepository;

    /*
    IP池管理
     */

    /**
     * 根据IP池ID查找对应的IP池信息。
     *
     * @param poolId IP池的唯一标识符。
     * @return 返回与指定ID匹配的IP池对象，如果没有找到则返回null。
     */
    public IpPool findIpPoolByPoolId(Long poolId) {
        return ipPoolRepository.findByPoolId(poolId);
    }


}
