package fan.summer.hmoneta.service;


import fan.summer.hmoneta.webEntity.agent.AgentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * 提供Agent相关服务
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/11
 */
@Service
public class AgentService {

    private final RestTemplate restTemplate;

    @Autowired
    public AgentService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 检查指定IP地址的机器上是否安装了Agent客户端。
     * 通过向指定IP的477端口发送HTTP GET请求来检查服务是否可用。
     *
     * @param ip 需要检查的机器的IP地址。
     * @return 如果机器上安装了Agent客户端并服务运行正常，则返回true；否则返回false。
     */
    public boolean isInstallAgentClient(String ip){
        String url = "http://" +ip+":477/agent/api/status";
        // 发送GET请求并获取响应
        AgentStatus response = restTemplate.getForObject(url, AgentStatus.class);
        assert response != null;
        return response.isStatus();
    }

}
