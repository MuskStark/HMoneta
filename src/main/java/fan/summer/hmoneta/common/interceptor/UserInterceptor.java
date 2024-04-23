package fan.summer.hmoneta.common.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import fan.summer.hmoneta.common.context.LoginUserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * 拦截器：Spring框架特有的，常用于登录校验，权限校验，请求日志打印
 * @author phoebej
 */
@Component
public class UserInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(UserInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info(">>>>>>>>>>> UserInterceptor开始设置本地线程变量 >>>>>>>>>>>");
        //获取header的token参数
        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            LOG.info("获取用户登录token：{}", token);
            JSONObject loginUser = JwtUtil.getJSONObject(token);
            LOG.info("当前登录用户：{}", loginUser);
            UserResp userResp = JSONUtil.toBean(loginUser, UserResp.class);
            LoginUserContext.setMember(userResp);
            LOG.info("<<<<<<<<<<< UserInterceptor本地线程变量设置完毕 <<<<<<<<<<<");
        }else{
            LOG.info("请求中不包含token");
            LOG.info("<<<<<<<<<<< UserInterceptor本地线程变量设置失败 <<<<<<<<<<<");
        }

        return true;

    }

}