package fan.summer.hmoneta.task.scheduled;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import fan.summer.hmoneta.database.entity.ddns.DDNSRecorderEntity;
import fan.summer.hmoneta.database.entity.ddns.DDNSUpdateRecorderEntity;
import fan.summer.hmoneta.service.ddns.DDNSService;
import fan.summer.hmoneta.service.ddns.PublicIpChecker;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
@Slf4j
@Component
public class DDNSStatusUpdateTask {

    private final DDNSService ddnsService;
    private final PublicIpChecker ipChecker;

    @Autowired
    public DDNSStatusUpdateTask(DDNSService ddnsService, PublicIpChecker ipChecker) {
        this.ddnsService = ddnsService;
        this.ipChecker = ipChecker;
    }

    @Scheduled(cron = "1 */10 * * * ?")
    public void updateDDNSStatus() {
        MDC.put("LOG_ID", System.currentTimeMillis() + RandomUtil.randomString(3));
        log.info("----------------启动DDNS更新任务----------------");
        List<DDNSRecorderEntity> ddnsRecorderEntities = ddnsService.queryAllDDNSRecorder();
        if (!ddnsRecorderEntities.isEmpty()) {
            log.info(">>>开始获取最新公网IP");
            String publicIp = ipChecker.getPublicIp();
            if (publicIp != null) {
                log.info(">>>最新公网ip为：{}", publicIp);
                ddnsRecorderEntities.forEach(iterm -> {
                    boolean status = false;
                    log.info(">>>开始检查{}DNS记录", String.format("%s.%s", iterm.getSubDomain(), iterm.getDomain()));
                    // 当前系统记录的DNS记录
                    DDNSUpdateRecorderEntity ddnsUpdateRecorder = ddnsService.queryDDNSUpdateRecorderByDomain(iterm.getDomain(), iterm.getSubDomain());
                    if (ObjUtil.isNotEmpty(ddnsUpdateRecorder)) {
                        log.info("服务端解析记录为:{}", ddnsUpdateRecorder.getId());
                        log.info("服务端解析记录与实际贡丸IP对比结果:{}", ddnsUpdateRecorder.getIp().equals(publicIp));
                        if (ddnsUpdateRecorder.getIp().equals(publicIp)) status = true;
                        else status = ddnsService.createDdns(iterm.getDomain(), iterm.getSubDomain());
                    } else {
                        log.info("服务器端不存在该{}域名解析记录", iterm.getDomain() + iterm.getSubDomain());
                        status = ddnsService.createDdns(iterm.getDomain(), iterm.getSubDomain());
                    }
                    if (status) log.info(">>>已完成DNS记录变更");
                    else log.error(">>>DNS记录变更失败");
                });
            } else log.error("!!!!!!无法获取公网Ip!!!!!!!");
        } else log.info(">>>无DDNS记录,无需更新");
        log.info("----------------DDNS更新任务结束----------------");
    }
}
