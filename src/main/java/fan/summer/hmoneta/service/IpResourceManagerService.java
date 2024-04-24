package fan.summer.hmoneta.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import fan.summer.hmoneta.common.enums.BusinessExceptionEnum;
import fan.summer.hmoneta.common.exception.BusinessException;
import fan.summer.hmoneta.database.entity.ipPool.IpPool;
import fan.summer.hmoneta.database.entity.ipPool.IpPoolUsedDetail;
import fan.summer.hmoneta.database.repository.IpPoolRepository;
import fan.summer.hmoneta.database.repository.IpPoolUsedDetailRepository;
import fan.summer.hmoneta.util.IpUtil;
import fan.summer.hmoneta.util.SnowFlakeUtil;
import fan.summer.hmoneta.webEntity.req.ipPool.IpPoolModifyReq;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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
     * 查找并返回所有IP池的信息。
     *
     * @return List<IpPool> 返回一个IP池信息的列表。
     */
    public List<IpPool> findAllIpPool() {
        return ipPoolRepository.findAll();
    }

    /**
     * 查找并返回所有IP池的名称列表。
     *
     * @return List<String> 所有IP池的名称列表，以字符串列表形式返回。
     */
    public List<String> findAllPoolName() {
        return ipPoolRepository.findAllPoolName();
    }

    /**
     * 修改IP池信息。根据请求的不同，该方法要么创建一个新的IP池，要么更新一个已存在的IP池。
     *
     * @param req 包含IP池修改请求信息的对象。如果请求创建新的IP池，需提供IP池名称和相关配置；
     *            如果请求更新IP池，需提供要更新的IP池ID和更新后的配置信息。
     * @throws BusinessException 如果尝试创建一个已存在的IP池，或者更新一个不存在的IP池，
     *                             或者在更新时改变了IP地址范围导致与已有IP池的范围重叠，会抛出此异常。
     */
    @Transactional
    public void modifyIpPool(IpPoolModifyReq req) {
        IpPool ipPool = null;
        if(req.isCreate()){
            if(!ObjectUtil.isEmpty(findIpPoolByPoolName(req.getPoolName()))){
                throw new BusinessException(BusinessExceptionEnum.IP_POOL_EXISTS_ERROR);
            }
            // 创建Ip池
            ipPool = BeanUtil.copyProperties(req, IpPool.class);
            ipPool.setPoolId(SnowFlakeUtil.getSnowFlakeNextId());
            generateIpPoolResource(ipPool);
            // 检查同网络地址的IpPool起始地址是否重复
            List<IpPool> allByNetworkAddress = ipPoolRepository.findAllByNetworkAddress(req.getNetworkAddress());
            if(!allByNetworkAddress.isEmpty()){
                TreeSet<Integer> allStartIpAddr = new TreeSet<>();
                TreeSet<Integer> allEndIpAddr = new TreeSet<>();
                for (IpPool pool : allByNetworkAddress) {
                    allStartIpAddr.add(IpUtil.ipToInt(pool.getStartAddr()));
                    allEndIpAddr.add(IpUtil.ipToInt(pool.getEndAddr()));
                }
                IpUtil.ipToInt(ipPool.getStartAddr());
                IpUtil.ipToInt(ipPool.getEndAddr());
                boolean check = (IpUtil.ipToInt(ipPool.getStartAddr()) > allEndIpAddr.last() || IpUtil.ipToInt(ipPool.getEndAddr()) < allStartIpAddr.first());
                if(!check){
                    throw new BusinessException(BusinessExceptionEnum.IP_POOL_IP_ADDR_REPEAT_ERROR);
                }
            }
        }else {
            ipPool = ipPoolRepository.findByPoolId(req.getPoolId());
            if(ObjectUtil.isEmpty(ipPool)){
                throw new BusinessException(BusinessExceptionEnum.IP_POOL_NOT_EXISTS_ERROR);
            }
            BeanUtil.copyProperties(req,ipPool);
            generateIpPoolResource(ipPool);
        }
        ipPoolRepository.save(ipPool);
        // TODO:如果是更新，并且发生ip地址变更，则删除IpPoolUsedDetail对应的全部信息
    }

    /**
     * 生成IP池资源。
     * 该方法根据提供的IP池信息（网络地址和子网掩码），计算出可用的IP地址范围，并设置IP池的起始地址、结束地址以及可用IP数量等信息。
     *
     * @param ipPool 包含IP池的网络地址和子网掩码等信息的对象。
     */
    private void generateIpPoolResource(IpPool ipPool) {
        String startIp = null;
        String endIp = null;
        Map<String, String> availableIpRange = IpUtil.getAvailableIpRange(ipPool.getNetworkAddress(), ipPool.getMask());
        if (ObjectUtil.isEmpty(ipPool.getStartAddr())){
            startIp = availableIpRange.get("FirstIP");
            ipPool.setStartAddr(startIp);
        }else {
            startIp = ipPool.getStartAddr();
        }
        if (ObjectUtil.isEmpty(ipPool.getEndAddr())){
            endIp = availableIpRange.get("LastIP");
            ipPool.setEndAddr(endIp);
        }else {
            endIp = ipPool.getEndAddr();
        }
        int availableNum = IpUtil.getAvailableIPNum(startIp,endIp);
        ipPool.setAvailable(availableNum);
        ipPool.setInUse(0);
        ipPool.setRemain(availableNum);
    }



    /*
    IP池使用详情管理
     */

    /**
     * 根据池ID查找IP池使用的详细信息
     *
     * @param poolId 池的唯一标识符
     * @return 返回一个IP池使用详细信息的列表
     */
    public List<IpPoolUsedDetail> findByPoolId(Long poolId){
        return ipPoolUsedDetailRepository.findByPoolId(poolId);
    }

    /**
     * 根据池ID查找已分配的IP地址列表。
     *
     * @param poolId 池ID，用于指定要查询的IP池。
     * @return 返回一个IP地址字符串列表，这些IP地址已被分配自指定的IP池。
     */
    public List<String> findIssuedIpListByPoolId(Long poolId){
        return ipPoolUsedDetailRepository.findIssuedIpByPoolId(poolId);
    }

    /**
     * 根据池ID统计该池中已使用的数量。
     *
     * @param poolId 池的唯一标识符。
     * @return 返回指定池中已使用的数量。
     */
    public int countUsedNumByPoolId(Long poolId){
        return ipPoolUsedDetailRepository.countUsedNumByPoolId(poolId);
    }

    /**
     * 根据池ID删除所有IP池使用详情记录
     *
     * @param poolId 池ID，用于指定要删除的IP池使用详情记录所属的池
     */
    public void deleteAllByPoolId(Long poolId){
        ipPoolUsedDetailRepository.deleteAllByPoolId(poolId);
    }

    /**
     * 删除指定IP池中被使用的IP详情。
     * @param poolId IP池的ID，用于确定要操作的IP池。
     * @param serverIpAddr 需要被删除使用的IP地址，指定要删除的具体IP。
     */
    public void deleteIpUsedDetail(Long poolId,String serverIpAddr){
        ipPoolUsedDetailRepository.deleteByAddrAndPoolId(serverIpAddr,poolId);
    }

    /*
    同步处理
     */




}
