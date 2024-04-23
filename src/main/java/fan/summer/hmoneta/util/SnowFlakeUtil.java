package fan.summer.hmoneta.util;

import cn.hutool.core.util.IdUtil;

/**
 * @author Phoebej
 * @date 2023/5/14
 */
public class SnowFlakeUtil {
    private static final long dataCenterId = 1;
    private static final long workerId = 1;

    public static long getSnowFlakeNextId(){
        return IdUtil.getSnowflake(workerId,dataCenterId).nextId();
    }
    public static String getSnowFlakeNextIdStr(){
        return IdUtil.getSnowflake(workerId,dataCenterId).nextIdStr();
    }
}
