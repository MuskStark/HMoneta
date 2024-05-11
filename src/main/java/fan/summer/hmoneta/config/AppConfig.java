package fan.summer.hmoneta.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;

/**
 * HMoneta自定义Bean
 *
 * @author phoebej
 * @version 1.00
 * @Date 2024/5/11
 */
@Configuration
public class AppConfig {

    @Bean
    public HttpClient httpClient() {
        // 创建并配置HttpClient实例
        return HttpClient
                .newBuilder()
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
