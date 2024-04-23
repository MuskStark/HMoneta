package fan.summer.hmoneta.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现单点登录的工具类
 * @author Phoebej
 * @date 2023/5/18
 */
public class JwtUtil<T> {

    private final static Logger LOG = LoggerFactory.getLogger(JwtUtil.class);

    private static final String key = "povTvh!U7e9aJCwLUmp^qIVRCrgOAa=a";

    private T data;

    /**
     * 生成token
     * @param data 待需要生成token的对象
     * @return token
     * @param <T> 待需要生成token的对象的类型
     */
    public static <T> String createTokenForObject(T data){
        DateTime now = DateTime.now();
        // 设置token失效时间
        DateTime expires = now.offsetNew(DateField.HOUR, 24);
        byte[] byteKey = key.getBytes();
        Map<String, Object> payload = new HashMap<>();
        // 设置token时间校验
        payload.put(JWTPayload.ISSUED_AT,now);
        payload.put(JWTPayload.EXPIRES_AT, expires);
        payload.put(JWTPayload.NOT_BEFORE, now);
        // 将传入的对象属性及值写入payload(利用的Java反射技术)
        Field[] fields = data.getClass().getDeclaredFields();
        try{
            for(Field field : fields){
                field.setAccessible(true);
                // 以防传入对象中包含token属性，且token属性为null所引起的脏数据
                if(!"token".equals(field.getName())) {
                    payload.put(field.getName(), field.get(data));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        String token = JWTUtil.createToken(payload, byteKey);
        LOG.info("Jwt工具 payload：{}", payload.toString());
        LOG.info("Jwt工具 token：{}", token);
        return token;
    }

    /**
     * 根据Map中的数据生成对应的Token
     * @param payload 需生成token的数据
     * @return token
     */
    public static String createTokenByMap(Map<String,Object> payload){
        DateTime now = DateTime.now();
        // 设置token失效时间
        DateTime expires = now.offsetNew(DateField.HOUR, 24);
        byte[] byteKey = key.getBytes();
        // 设置token时间校验
        payload.put(JWTPayload.ISSUED_AT,now);
        payload.put(JWTPayload.EXPIRES_AT, expires);
        payload.put(JWTPayload.NOT_BEFORE, now);
        String token = JWTUtil.createToken(payload, byteKey);
        LOG.info("Jwt工具 payload：{}", payload.toString());
        LOG.info("Jwt工具 token：{}", token);
        return token;
    }

    /**
     * 校验token合法性
     * @param token 待校验token
     * @return 是否合法
     */
    public static boolean validate(String token){
        try {
            JWT jwt = JWTUtil.parseToken(token).setKey(key.getBytes());
            boolean validate = jwt.validate(0);
            LOG.info("Jwt工具 token校验结果：{}", validate);
            return validate;
        } catch (Exception e) {
            LOG.error("Jwt工具 token校验异常:", e);
            return false;
        }
    }

    /**
     * 获取token中原始数据
     * @param token token
     * @return 原始Json数据
     */
    public static JSONObject getJSONObject(String token){
        JWT jwt = JWTUtil.parseToken(token).setKey(key.getBytes());
        JSONObject payloads = jwt.getPayloads();
        payloads.remove(JWTPayload.ISSUED_AT);
        payloads.remove(JWTPayload.EXPIRES_AT);
        payloads.remove(JWTPayload.NOT_BEFORE);
        LOG.info("Jwt工具 token原始值：{}", payloads);
        return payloads;
    }

}
