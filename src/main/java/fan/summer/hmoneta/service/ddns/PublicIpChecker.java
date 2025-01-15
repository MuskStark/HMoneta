package fan.summer.hmoneta.service.ddns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 类的详细说明
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/7/30
 */
@Component
public class PublicIpChecker {

    private final Logger LOG = LoggerFactory.getLogger(PublicIpChecker.class);

    /**
     * 获取当前设备的公网IP地址。
     * 通过访问https://api.ipify.org API来获取IP地址。
     *
     * @return 当前设备的公网IP地址字符串，如果无法获取则返回null。
     */
    //TODO:存在受代理影响问题
    public String getPublicIp() {
        String ip = null;
        try (HttpClient client = HttpClient.newHttpClient()) {
            LOG.info("-------------开始获取公网IP地址-------------");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.ipify.org"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ip = response.body();
                LOG.info("获取公网IP地址成功,ip:{}", ip);
            } else {
                LOG.info("无法获取IP地址。HTTP状态码: {}" ,response.statusCode());
            }
        } catch (IOException e) {
            LOG.error("发生IO错误: {}", e.getMessage());
        } catch (InterruptedException e) {
            LOG.error("发生中断错误: {}", e.getMessage());
            Thread.currentThread().interrupt(); // 重新设置中断标志
        } catch (Exception e) {
            LOG.error("发生未预期的错误: {}", e.getMessage());
        }
        LOG.info("-------------完成获取公网IP地址-------------");
        return ip;
    }

}
