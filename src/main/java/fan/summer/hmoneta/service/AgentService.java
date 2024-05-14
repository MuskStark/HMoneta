package fan.summer.hmoneta.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fan.summer.hmoneta.database.entity.agent.AgentInfo;
import fan.summer.hmoneta.database.repository.AgentInfoRepository;
import fan.summer.hmoneta.webEntity.agent.AgentStatus;
import fan.summer.hmoneta.webEntity.agent.ConfigEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Set;


/**
 * 提供Agent相关服务
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/11
 */
@Service
public class AgentService {

    private static final Logger LOG = LoggerFactory.getLogger(AgentService.class);

    @Value("${hmoneta.master.report-url}")
    private String AGENT_REPORT_API_URL;
    @Value("${server.port}")
    private int AGENT_REPORT_API_PORT;
    @Value("${hmoneta.agent.port}")
    private int AGENT_PORT;
    private final RestTemplate restTemplate;
    private AgentInfoRepository agentInfoRepository;

    @Autowired
    public AgentService(RestTemplate restTemplate, AgentInfoRepository agentInfoRepository) {
        this.restTemplate = restTemplate;
        this.agentInfoRepository = agentInfoRepository;
    }

    public List<AgentInfo> findAllAgent(){
        return agentInfoRepository.findAll();
    }

    public Set<Long> findAllServerId(){
        return agentInfoRepository.findAllServerId();
    }

    public AgentInfo findAgentByServerId(Long serverId){
        return agentInfoRepository.findByServerId(serverId);
    }
    @Transactional
    public void save(AgentInfo agentInfo){
        agentInfoRepository.save(agentInfo);
    }

    /**
     * 检查指定IP地址的机器上是否安装了Agent客户端。
     * 通过向指定IP的477端口发送HTTP GET请求来检查服务是否可用。
     *
     * @param ip 需要检查的机器的IP地址。
     * @return 如果机器上安装了Agent客户端并服务运行正常，则返回true；否则返回false。
     */
    public boolean isInstallAgentClient(String ip){
        String url = "http://" +ip+":"+AGENT_PORT+"/agent/api/status";
        // 发送GET请求并获取响应
        AgentStatus response = restTemplate.getForObject(url, AgentStatus.class);
        assert response != null;
        return response.isStatus();
    }
    /**
     * 配置分发
     * @param agentInfo agent信息
     * @return boolean 返回配置是否成功
     * @throws JsonProcessingException 当JSON处理出错时抛出
     */
    public boolean issueConfig(AgentInfo agentInfo) throws JsonProcessingException {
        ConfigEntity config = new ConfigEntity();
        String targetUrl = "http://" +agentInfo.getServerIp()+":"+AGENT_PORT+"/agent/api/config";
        config.setAgentId(agentInfo.getAgentId());
        config.setReportUrl(AGENT_REPORT_API_URL + ":" + AGENT_REPORT_API_PORT);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = mapper.writeValueAsString(config);
        HttpEntity<String> entity = new HttpEntity<String>(json, headers);
        // 发送POST请求并获取响应
        ResponseEntity<AgentStatus> response = restTemplate.exchange(targetUrl, HttpMethod.POST, entity, AgentStatus.class);
        return response.getBody().isStatus();

    }

}
