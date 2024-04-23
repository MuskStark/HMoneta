package fan.summer.hmoneta.common.context;

import fan.summer.homelab.apiEntity.resp.user.UserResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本地线程类，用来存储本地线程变量信息
 * @author phoebej
 */
public class LoginUserContext {
    private static final Logger LOG = LoggerFactory.getLogger(LoginUserContext.class);

    /**
     * 线程本地变量
     */
    private static ThreadLocal<UserResp> member = new ThreadLocal<>();

    /**
     * 获取存在线程本地变量中的对象
     * @return 存在线程本地变量中的对象
     */
    public static UserResp getMember() {
        return member.get();
    }

    /**
     * 将对象存储仅线程本地变量中
     */
    public static void setMember(UserResp userResp) {
        LoginUserContext.member.set(userResp);
    }

    public static Long getId() {
        try {
            return member.get().getUserId();
        } catch (Exception e) {
            LOG.error("获取登录会员信息异常", e);
            throw e;
        }
    }

}