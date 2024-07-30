package fan.summer.hmoneta.service.ddns;

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
public class PublicIpChecker {

    /**
     * 获取当前设备的公网IP地址。
     * 通过访问https://api.ipify.org API来获取IP地址。
     *
     * @return 当前设备的公网IP地址字符串，如果无法获取则返回null。
     */
    public String getPublicIp() {
        String ip = null;
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.ipify.org"))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ip = response.body();
                System.out.println("您的公网IP地址是: " + ip);
            } else {
                System.out.println("无法获取IP地址。HTTP状态码: " + response.statusCode());
            }
        } catch (IOException e) {
            System.out.println("发生IO错误: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("操作被中断: " + e.getMessage());
            Thread.currentThread().interrupt(); // 重新设置中断标志
        } catch (Exception e) {
            System.out.println("发生未预期的错误: " + e.getMessage());
        }
        return ip;
    }

}
