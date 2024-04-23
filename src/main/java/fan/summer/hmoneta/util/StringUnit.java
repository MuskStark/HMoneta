package fan.summer.hmoneta.util;

import java.util.regex.Pattern;

public class StringUnit {

    /**
     * 驼峰命名转换成蛇形命名
     * @param camelCase 驼峰命名字符串
     * @return 蛇形命名
     */
    public static String camelCaseToSnakeCase(String camelCase){
        return camelCase.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
    }

    /**
     * 蛇形命名转换成驼峰命名
     * @param snakeCase 蛇形命名字符串
     * @return 驼峰命名
     */
    public static String snakeCaseToCamelCase(String snakeCase){
        return Pattern.compile("_([a-z])")
                .matcher(snakeCase)
                .replaceAll(match -> match.group(1).toUpperCase());
    }

}
