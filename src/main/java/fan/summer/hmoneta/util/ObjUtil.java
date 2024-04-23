package fan.summer.hmoneta.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 提供对象处理工具
 */
public class ObjUtil {

    /**
     * 将对象中属性与值转成map
     *
     * @param object 待转换的对象
     * @return 转换结果
     */
    public static Map<String, Object> objectAttributeValueMap(Object object) {
        Map<String, Object> attributeValueMap = new HashMap<>();
        // 获取对象的所有属性
        Field[] fields = object.getClass().getDeclaredFields();

        // 遍历属性
        for (Field field : fields) {
            // 设置可访问私有属性
            field.setAccessible(true);

            try {
                // 获取属性名
                String attributeName = field.getName();
                // 获取属性值
                Object value = field.get(object);

                // 检查属性值是否非空
                if (value != null) {
                    if (!attributeName.equals("userId")) {
                        attributeValueMap.put(attributeName, value);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return attributeValueMap;
    }

    /**
     * 将对象中属性与值转成map(对象中包含Date)
     *
     * @param object 待转换的对象
     * @param format 时间格式
     * @return 转换结果
     */
    public static Map<String, Object> objectAttributeValueMap(Object object, String format) {
        Map<String, Object> attributeValueMap = new HashMap<>();
        // 获取对象的所有属性
        Field[] fields = object.getClass().getDeclaredFields();

        // 遍历属性
        for (Field field : fields) {
            // 设置可访问私有属性
            field.setAccessible(true);

            try {
                // 获取属性名
                String attributeName = field.getName();
                // 获取属性类型
                Class<?> type = field.getType();
                // 获取属性值
                Object value = field.get(object);

                // 检查属性值是否非空
                if (value != null) {
                    if (!attributeName.equals("userId")) {
                        if (type == Date.class) {
                            String reFormat = DateUtil.format((Date) value, format);
                            attributeValueMap.put(attributeName, reFormat);
                        } else {
                            attributeValueMap.put(attributeName, value);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return attributeValueMap;
    }

    /**
     * 实现类属性更新
     *
     * @param oldObj 旧数据类
     * @param newObj 新数据类
     * @param target 返回类型
     * @param <T>
     * @return 更新结果
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     */
    public static <T> T updateValue(Object oldObj, Object newObj, Class<T> target) {
        T result = null;
        try {
            // 实例化target对象
            result = target.getDeclaredConstructor().newInstance();
            // 获取result的所有属性名
            List<String> resultFieldNameList = generateFieldNameList(result.getClass().getDeclaredFields());
            // 获取newObj的所有属性名
            List<String> newFieldNameList = generateFieldNameList(newObj.getClass().getDeclaredFields());
            // 获取oldObj的所有field
            Field[] olds = oldObj.getClass().getDeclaredFields();
            // 更新逻辑
            for (Field oldfield : olds) {
                // 获取newObj的同名属性
                if (newFieldNameList.contains(oldfield.getName()) && resultFieldNameList.contains(oldfield.getName())) {
                    // 获取newObj中与oldfield的同名属性
                    Field newField = newObj.getClass().getDeclaredField(oldfield.getName());
                    // 获取result中与oldfield的同名属性
                    Field resultField = result.getClass().getDeclaredField(oldfield.getName());
                    // 设置所有属性均可访问
                    newField.setAccessible(true);
                    oldfield.setAccessible(true);
                    resultField.setAccessible(true);
                    // 未更新保留原属性，更新则使用新属性
                    boolean oldfieldIsNotNUll = ObjectUtil.isNotNull(oldfield.get(oldObj));
                    boolean newFieldIsNotNUll = ObjectUtil.isNotNull(newField.get(newObj));
                    boolean typeIsEqual = oldfield.getType() == newField.getType();
                    if (oldfieldIsNotNUll && newFieldIsNotNUll) {
                        if (oldfield.get(oldObj).equals(newField.get(newObj)) && typeIsEqual) {
                            resultField.set(result, oldfield.get(oldObj));
                        } else if (!oldfield.get(oldObj).equals(newField.get(newObj)) && typeIsEqual) {
                            resultField.set(result, newField.get(newObj));
                        }
                    }else if(oldfieldIsNotNUll && !newFieldIsNotNUll){
                        if(typeIsEqual) {
                            resultField.set(result, oldfield.get(oldObj));
                        }
                    }else if(!oldfieldIsNotNUll && newFieldIsNotNUll){
                        if(typeIsEqual) {
                            resultField.set(result, newField.get(newObj));
                        }
                    }

                }
            }
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 NoSuchFieldException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    private static List<String> generateFieldNameList(Field[] fields) {
        List<String> result = new ArrayList<String>();
        for (Field field : fields) {
            result.add(field.getName());
        }
        return result;
    }
}
