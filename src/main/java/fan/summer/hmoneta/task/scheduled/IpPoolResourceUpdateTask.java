package fan.summer.hmoneta.task.scheduled;

import cn.hutool.core.util.RandomUtil;
import fan.summer.hmoneta.database.entity.ipPool.IpPool;
import fan.summer.hmoneta.service.IpResourceManagerService;
import fan.summer.hmoneta.service.ServerInfoManagerService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * IpPool资源使用情况更新任务
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/26
 */
@Component
public class IpPoolResourceUpdateTask {

    private static final Logger LOG = LoggerFactory.getLogger(IpPoolResourceUpdateTask.class);

    @Resource
    private IpResourceManagerService ipResourceManagerService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void updateIpPoolResource() {
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        LOG.info(">>>>>>>>>>>>>>>开始更新IpPool资源使用情况");
        List<IpPool> ipPools = ipResourceManagerService.findAllIpPool();
        for (IpPool ipPool : ipPools) {
            int i = ipResourceManagerService.countUsedNumByPoolId(ipPool.getPoolId());
            ipPool.setInUse(i);
            ipPool.setRemain(ipPool.getAvailable() - i);
        }
        ipResourceManagerService.saveAllIpPool(ipPools);
        LOG.info("完成{}个Ip池更新",ipPools.size());
        LOG.info(">>>>>>>>>>>>>>>完成IpPool资源使用情况更新");
    }


    }
