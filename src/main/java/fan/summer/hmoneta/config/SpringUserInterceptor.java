package fan.summer.hmoneta.config;


import fan.summer.hmoneta.common.interceptor.LogInterceptor;
import fan.summer.hmoneta.common.interceptor.UserInterceptor;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Phoebej
 * @date 2023/5/22
 */
@Component
public class SpringUserInterceptor implements WebMvcConfigurer {

    @Resource
    private LogInterceptor logInterceptor;

    @Resource
    private UserInterceptor userInterceptor;

    /**
     * 注册自定义拦截器，拦截顺序与registry顺序一致
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/hm/register")
                .excludePathPatterns("/hm/master/**")
                .excludePathPatterns("/hm/login")
                .excludePathPatterns("/hm/publicKey");
    }
}
