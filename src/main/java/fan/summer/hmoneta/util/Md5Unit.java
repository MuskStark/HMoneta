package fan.summer.hmoneta.util;

import cn.hutool.crypto.digest.DigestUtil;

import java.util.Random;

/**
 * md5工具类
 * @author phoebej
 */
public class Md5Unit {
    /**
     * 对要加密的字符串进行Md5摘要加密
     * @param source 要加密的原文
     * @param salt 参与加密的盐值
     * @return 加密后的MD5摘要
     */
    public static String md5Digest(String source, Integer salt){
        // 对传入的source进行加盐混淆
        char[] tmp = source.toCharArray();
        for(int i=0; i<tmp.length; i++){
            tmp[i] = (char) (tmp[i] + salt);
        }
        String target = new String(tmp);
        // 生成MD5
        String md5 = DigestUtil.md5Hex(target);
        return md5;

    }
    /**
     * 用户盐值生成函数
     * @return 盐值
     */
    public static int saltGenerator() {
        Random rand = new Random();
        int[] tmp = {12, 50, 79, 23, 54, 51, 38, 56, 36, 58};
        int hearder = tmp[rand.nextInt(tmp.length)];
        return hearder + rand.nextInt(1000);
    }
}
