package fan.summer.hmoneta.task.scheduled;

import fan.summer.hmoneta.database.entity.agent.AgentInfo;
import fan.summer.hmoneta.database.entity.serverInfo.ServerInfoDetail;
import fan.summer.hmoneta.service.AgentService;
import fan.summer.hmoneta.service.ServerInfoManagerService;
import fan.summer.hmoneta.util.SnowFlakeUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/13
 */
@Component
public class AgentScanTask {


    private static final Logger LOG = LoggerFactory.getLogger(AgentScanTask.class);
    private final AgentService service;
    private final ServerInfoManagerService infoManagerService;
    private Set<Long> allServerId;

    @Autowired
    public AgentScanTask(AgentService service, ServerInfoManagerService infoManagerService) {
        this.service = service;
        this.infoManagerService = infoManagerService;
    }

    @PostConstruct
    public void init() {
        this.allServerId = service.findAllServerId();
    }

    /**
     * 定时任务，每隔30秒执行一次，用于监听和更新代理信息。
     * 通过查询所有服务器信息，比对当前已知的代理信息，进行信息的更新或下发配置。
     */
    @Scheduled(cron = "0/30 * * * * ?")
    protected void agentListener() {
        LOG.info("----------------启动Agent扫描任务----------------");
        List<ServerInfoDetail> allServerInfo = infoManagerService.findAllServerInfo();
        allServerInfo.forEach(serverInfoDetail -> {
            LOG.info("待扫描服务器名称{}", serverInfoDetail.getServerName());
            try {
                boolean isContain = allServerId.contains(serverInfoDetail.getId());
                LOG.info("是否已记录该服务器Agent信息：{}",isContain);
                if (isContain) {
                    LOG.info("开始Agent信息更新");
                    boolean needUpdateInfo = false;
                    AgentInfo agentByServerId = service.findAgentByServerId(serverInfoDetail.getId());
                    // 检查服务器地址是否变化
                    if (!agentByServerId.getServerIp().equals(serverInfoDetail.getServerIpAddr())) {
                        agentByServerId.setServerIp(serverInfoDetail.getServerIpAddr());
                        needUpdateInfo = true;
                    }
                    boolean installAgentClient = service.isInstallAgentClient(agentByServerId.getServerIp());
                    // 判断是否下发配置信息
                    if (installAgentClient) {
                        if(!agentByServerId.getAlive()) {
                            agentByServerId.setAlive(true);
                            needUpdateInfo = true;
                        }
                        if(!agentByServerId.getIssueConfig()) {
                            boolean success = service.issueConfig(agentByServerId);
                            if(success){
                                agentByServerId.setIssueConfig(true);
                                needUpdateInfo = true;
                            }
                        }
                    }
                    if(needUpdateInfo){
                        service.save(agentByServerId);
                    }
                    LOG.info("Agent信息更新完成");

                } else {
                    LOG.info("Agent信息不存在，开始创建Agent信息");
                    AgentInfo info = new AgentInfo();
                    info.setAgentId(SnowFlakeUtil.getSnowFlakeNextId());
                    info.setServerId(serverInfoDetail.getId());
                    info.setServerIp(serverInfoDetail.getServerIpAddr());
                    info.setAlive(service.isInstallAgentClient(info.getServerIp()));
                    if(info.getAlive()){
                        service.issueConfig(info);
                        info.setIssueConfig(true);
                    }else {
                        info.setIssueConfig(false);
                    }
                    service.save(info);
                    allServerId.add(serverInfoDetail.getId());

                }
                LOG.info("----------------完成Agent扫描任务----------------");
            }catch (Exception e){
                LOG.error("AgentListener error:{}", Objects.requireNonNull(e.getMessage()));
                LOG.info("----------------完成Agent扫描任务----------------");
            }
        });
    }
}
