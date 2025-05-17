package fan.summer.hmoneta.config;

import fan.summer.hmoneta.common.context.LoginUserContext;
import fan.summer.hmoneta.webEntity.resp.user.UserResp;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.initialize();

        // 自定义 TaskDecorator
        executor.setTaskDecorator(new ContextCopyingDecorator());
        return executor;
    }

    static class ContextCopyingDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            // 获取主线程中的 ThreadLocal 数据
            UserResp userResp = LoginUserContext.getMember();
            return () -> {
                try {
                    // 在异步线程中重新设置 ThreadLocal 数据
                    LoginUserContext.setMember(userResp);
                    runnable.run();
                } finally {
                    // 清理 ThreadLocal 数据，防止内存泄漏
                    LoginUserContext.setMember(null);
                }
            };
        }
    }
}