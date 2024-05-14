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

    @Scheduled(cron = "0/30 * * * * ?")
    protected void agentListener() {
        List<ServerInfoDetail> allServerInfo = infoManagerService.findAllServerInfo();
        allServerInfo.forEach(serverInfoDetail -> {
            try {
                if (allServerId.contains(serverInfoDetail.getId())) {
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
                        if(!agentByServerId.getIsAlive()) {
                            agentByServerId.setIsAlive(true);
                            needUpdateInfo = true;
                        }
                        service.issueConfig(agentByServerId);
                    }
                    if(needUpdateInfo){
                        service.save(agentByServerId);
                    }

                } else {
                    AgentInfo info = new AgentInfo();
                    info.setAgentId(SnowFlakeUtil.getSnowFlakeNextId());
                    info.setServerId(serverInfoDetail.getId());
                    info.setServerIp(serverInfoDetail.getServerIpAddr());
                    info.setIsAlive(service.isInstallAgentClient(info.getServerIp()));
                    if(info.getIsAlive()){
                        service.issueConfig(info);
                    }
                    service.save(info);
                    allServerId.add(serverInfoDetail.getId());

                }
            }catch (Exception e){
                LOG.error("AgentListener error:{}", Objects.requireNonNull(e.getMessage()));
            }
        });
    }
}