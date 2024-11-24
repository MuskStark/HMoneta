package fan.summer.hmoneta.task.scheduled;

import cn.hutool.core.util.ObjUtil;
import fan.summer.hmoneta.database.entity.agent.AgentInfo;
import fan.summer.hmoneta.database.entity.agent.AgentReport;
import fan.summer.hmoneta.database.entity.serverInfo.ServerInfoDetail;
import fan.summer.hmoneta.service.AgentService;
import fan.summer.hmoneta.service.ServerInfoManagerService;
import fan.summer.hmoneta.util.SnowFlakeUtil;
import fan.summer.hmoneta.webEntity.agent.ConfigEntity;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
    @Value("${hmoneta.agent.report-delay}")
    private Long reportDelay;
    @Value("${hmoneta.agent.port}")
    private Integer agentPort;

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
        if(!allServerInfo.isEmpty()) {
            allServerInfo.forEach(serverInfoDetail -> {
                LOG.info(">>>>>待扫描服务器名称{}", serverInfoDetail.getServerName());
                try {
                    boolean isContain = allServerId.contains(serverInfoDetail.getId());
                    LOG.info(">>>>>是否已记录该服务器Agent信息：{}", isContain);
                    // 已记录该服务器Agent信息
                    if (isContain) {
                        AgentInfo agentInfo = service.findAgentByServerId(serverInfoDetail.getId());
                        // Agent验活
                        Map<String, Boolean> agentStatus = checkAgentStatus(agentInfo.getServerIp());
                        LOG.info(">>>>>目标服务器Agent是否存活：{}", agentStatus.get("isAlive"));
                        if (agentStatus.get("isAlive")) {
                            // 检查报告是否严重超时
                            AgentReport report = service.findAgentReportByServerId(serverInfoDetail.getId());
                            LOG.info(">>>>>系统报告是否超期：{}", report.getTimeStamp() - System.currentTimeMillis() > reportDelay);
                            if (report.getTimeStamp() - System.currentTimeMillis() > reportDelay) {
                                agentInfo.setIssueConfig(service.issueConfig(agentInfo));
                            }
                            if (!agentStatus.get("configStatus")) {
                                agentInfo.setIssueConfig(service.issueConfig(agentInfo));
                            }
                        } else {
                            // 检测Agent地址信息是否与服务器信息一直
                            LOG.info(">>>>>Agent地址信息是否正确：{}", agentInfo.getServerIp().equals(serverInfoDetail.getServerIpAddr()));
                            if (!agentInfo.getServerIp().equals(serverInfoDetail.getServerIpAddr())) {
                                agentInfo.setServerIp(serverInfoDetail.getServerIpAddr());
                                agentInfo.setIssueConfig(service.issueConfig(agentInfo));
                            }

                        }
                        service.save(agentInfo);
                    } else {
                        LOG.info(">>>>>Agent信息不存在，开始创建Agent信息");
                        AgentInfo info = new AgentInfo();
                        info.setAgentId(SnowFlakeUtil.getSnowFlakeNextId());
                        info.setServerId(serverInfoDetail.getId());
                        info.setServerIp(serverInfoDetail.getServerIpAddr());
                        info.setAlive(service.isInstallAgentClient(info.getServerIp()));
                        if (info.getAlive()) {
                            service.issueConfig(info);
                            info.setIssueConfig(true);
                        } else {
                            info.setIssueConfig(false);
                        }
                        service.save(info);
                        allServerId.add(serverInfoDetail.getId());
                    }
                } catch (Exception e) {
                    LOG.error(">>>>>AgentListener error:{}", Objects.requireNonNull(e.getMessage()));
                }

            });
        }else {
            LOG.info(">>>>>未查询到任何服务器信息，停止Agent扫描!");

        }
        LOG.info("----------------完成Agent扫描任务----------------");
    }

    private Map<String, Boolean> checkAgentStatus(String serverUri){
        // 请求头，设置Content-Type为application/json
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String reportUrl = "http://" + serverUri +":" + agentPort + "/agent/api/is_alive";
        String reportConfigUri = "http://" + serverUri +":" + agentPort + "/agent/api/report-config";
        ResponseEntity<String> forEntity = restTemplate.getForEntity(reportUrl, String.class);
        ResponseEntity<ConfigEntity> config = restTemplate.getForEntity(reportConfigUri, ConfigEntity.class);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isAlive",forEntity.getStatusCode().is2xxSuccessful());
        result.put("configStatus", !ObjUtil.isEmpty(config.getBody()));
        return result;
    }
}
