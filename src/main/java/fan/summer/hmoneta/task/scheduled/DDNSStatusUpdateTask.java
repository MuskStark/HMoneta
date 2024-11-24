package fan.summer.hmoneta.task.scheduled;

import fan.summer.hmoneta.database.entity.ddns.DDNSUpdateRecorderEntity;
import fan.summer.hmoneta.service.ddns.DDNSService;
import fan.summer.hmoneta.service.ddns.PublicIpChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DDNS更新定时任务
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/11/22
 */
@Component
public class DDNSStatusUpdateTask {
    private static final Logger LOG = LoggerFactory.getLogger(DDNSStatusUpdateTask.class);
    private final DDNSService ddnsService;
    private final PublicIpChecker ipChecker;

    @Autowired
    public DDNSStatusUpdateTask(DDNSService ddnsService, PublicIpChecker ipChecker) {
        this.ddnsService = ddnsService;
        this.ipChecker = ipChecker;
    }

    @Scheduled(cron = "1 */10 * * * ?")
    public void updateDDNSStatus() {
        LOG.info("----------------启动DDNS更新任务----------------");
        List<DDNSUpdateRecorderEntity> ddnsUpdateRecorderEntities = ddnsService.queryAllDDNSUpdateRecorder();
        if(!ddnsUpdateRecorderEntities.isEmpty()) {
            LOG.info(">>>开始获取最新公网IP");
            String publicIp = ipChecker.getPublicIp();
            if (publicIp != null) {
                LOG.info(">>>最新公网ip为：{}", publicIp);
                ddnsUpdateRecorderEntities.forEach(iterm -> accept(iterm, publicIp));
            } else {
                LOG.error("!!!!!!无法获取公网Ip!!!!!!!");
            }
        }else {
            LOG.info(">>>无DDNS记录,无需更新");
        }
        LOG.info("----------------DDNS更新任务结束----------------");
    }

    private void accept(DDNSUpdateRecorderEntity iterm, String publicIp) {
        LOG.info(">>>开始检查{}DNS记录", String.format("%s.%s", iterm.getSubDomain(), iterm.getDomain()));
            if (!iterm.getIp().equals(publicIp)) {
                LOG.info(">>>IP已变更，开始更新最新DNS记录");
                boolean status = ddnsService.createDdns(iterm.getProviderName(), iterm.getDomain(), iterm.getSubDomain());
                if(status){
                    LOG.info(">>>已完成DNS记录变更");
                }else {
                    LOG.error(">>>DNS记录变更失败");
                }
            }else {
                LOG.info(">>>IP未变更，无需更新");
            }
    }
}
