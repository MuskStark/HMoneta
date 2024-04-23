package fan.summer.hmoneta.service;

import fan.summer.hmoneta.database.entity.ipPool.IpPool;
import fan.summer.hmoneta.database.repository.IpPoolRepository;
import fan.summer.hmoneta.database.repository.IpPoolUsedDetailRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 根据池名查找IP池。
     *
     * @param poolName IP池的名称。
     * @return 返回与指定池名匹配的IP池对象，如果没有找到，则返回null。
     */
    public IpPool findIpPoolByPoolName(String poolName) {
        return ipPoolRepository.findByPoolName(poolName);
    }

    /**
     * 根据网络地址查找所有的IP池。
     *
     * @param networkAddress 网络地址，用于查询关联的IP池。
     * @return 返回一个包含所有匹配网络地址的IP池的列表。
     */
    public List<IpPool> findAllByNetworkAddress(String networkAddress){
        return ipPoolRepository.findAllByNetworkAddress(networkAddress);
    }

    /**
     * 查找并返回所有IP池的名称列表。
     *
     * @return List<String> 所有IP池的名称列表，以字符串列表形式返回。
     */
    public List<String> findAllPoolName() {
        return ipPoolRepository.findAllPoolName();
    }




}
