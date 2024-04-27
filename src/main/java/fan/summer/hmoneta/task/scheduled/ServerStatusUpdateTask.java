package fan.summer.hmoneta.task.scheduled;

import fan.summer.hmoneta.database.entity.ipPool.IpPoolUsedDetail;
import fan.summer.hmoneta.database.entity.serverInfo.ServerInfoDetail;
import fan.summer.hmoneta.service.IpResourceManagerService;
import fan.summer.hmoneta.service.ServerInfoManagerService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务器状态定时更新任务
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/4/26
 */
@Component
public class ServerStatusUpdateTask {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStatusUpdateTask.class);
    private static final int TIMEOUT_SECONDS = 1;

    @Resource
    private ServerInfoManagerService serversManagerService;
    @Resource
    private IpResourceManagerService ipResourceManagerService;

    @Scheduled(cron = "0 0/5 * * * ?")
    @Transactional
    protected void updateServerStatus() {
        LOG.info(">>>>>>>开始服务器状态服务>>>>>>>>>");
        List<ServerInfoDetail> serverInfoDetails = serversManagerService.findAllServerInfo();
        if(serverInfoDetails.isEmpty()){
            LOG.warn("服务器列表为空，请检查服务器列表信息或添加服务器信息");
            LOG.info(">>>>>>>服务器状态服务结束>>>>>>>>>");
            return;
        }
        List<IpPoolUsedDetail> resultList = new ArrayList<>();
        for (ServerInfoDetail serverInfoDetail : serverInfoDetails) {
            IpPoolUsedDetail detail = new IpPoolUsedDetail();
            try (SocketChannel channel = SocketChannel.open()) {
                channel.configureBlocking(false);
                channel.connect(new InetSocketAddress(serverInfoDetail.getServerIpAddr(), Integer.parseInt(serverInfoDetail.getServerPort())));
                long startTime = System.currentTimeMillis();
                while (!channel.finishConnect()) {
                    long elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= TIMEOUT_SECONDS * 1000) {
                        LOG.warn("{}服务器无响应，等待超时", serverInfoDetail.getServerIpAddr());
                        break;
                    }
                }
                LOG.info("设置{}服务器状态", serverInfoDetail.getServerIpAddr());
                serverInfoDetail.setIsAlive(channel.isConnected());
                detail.setPoolId(serverInfoDetail.getPoolId());
                detail.setServerName(serverInfoDetail.getServerName());
                detail.setIssuedIp(serverInfoDetail.getServerIpAddr());
                detail.setIsUsed(channel.isConnected());
                resultList.add(detail);

            } catch (IOException e) {
                LOG.error("{}链接异常", serverInfoDetail.getServerIpAddr());
                detail.setPoolId(serverInfoDetail.getPoolId());
                detail.setServerName(serverInfoDetail.getServerName());
                detail.setIssuedIp(serverInfoDetail.getServerIpAddr());
                detail.setIsUsed(false);
                resultList.add(detail);
                serverInfoDetail.setIsAlive(false);
            }
        }
        try {
            serversManagerService.saveAllServerInfo(serverInfoDetails);
            ipResourceManagerService.saveAllIpPoolUsedDetail(resultList);
            LOG.info("完成服务器状态更新累计更新{}台", serverInfoDetails.size());
        }catch (Exception e){
            LOG.error("数据库写入错误");
        }
        LOG.info(">>>>>>>服务器状态服务结束>>>>>>>>>");
    }

}
