package fan.summer.hmoneta.service.ddns;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class PublicIpChecker {

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
            log.info("-------------开始获取公网IP地址-------------");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ipinfo.io/ip"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ip = response.body();
                log.info("公网Ip为:{}", ip);
            } else log.info("无法获取IP地址。HTTP状态码: {}", response.statusCode());
        } catch (IOException e) {
            log.error("发生IO错误: {}", e.getMessage());
            log.warn(String.valueOf(e.fillInStackTrace()));
        } catch (InterruptedException e) {
            log.error("发生中断错误: {}", e.getMessage());
            Thread.currentThread().interrupt(); // 重新设置中断标志
        } catch (Exception e) {
            log.error("发生未预期的错误: {}", e.getMessage());
        }
        log.info("-------------完成获取公网IP地址-------------");
        return ip;
    }

}
