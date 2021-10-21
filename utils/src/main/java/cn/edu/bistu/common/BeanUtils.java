package cn.edu.bistu.common;


import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class BeanUtils {

    /**
     * 该静态方法用于代替Class类的getDeclaredField方法，在目标类及其所有父类中查找一个属性，
     * 方法中委托目标类及其父类的getDeclaredField方法，在各层级查找指定属性，使用while循环
     * 和接口Class.getSuperclass()沿着继承树依次向上遍历所有父类。
     *
     * @param clazz 目标类
     * @param name  属性名字符串
     * @return 返回找到的属性Field对象
     * @throws NoSuchFieldException 若目标类中及其所有父类都找不到该属性，抛出该异常
     */
    public static Field getDeclaredField(Class clazz, String name) throws NoSuchFieldException {
        Field declaredField = null;
        while (clazz != null &&
                !clazz.getName().toLowerCase().equals("java.lang.object")) {//当父类为null的时候说明到达了最上层的父类(Object类).

            try {
                declaredField = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                log.debug(clazz.getTypeName() + " has no such field called " + name);
                //得到父类,然后赋给自己
                clazz = clazz.getSuperclass();
                continue;
            }
            log.debug("field " + name + " was found in class " + clazz.getTypeName());
            return declaredField;
        }
        throw new NoSuchFieldException("field " + name + " not found");
    }

    public static boolean isFieldNull(Object obj, Field field) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(obj) == null;
    }


    public static String formatDate(Object obj, Field field) throws IllegalAccessException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatDate = null;
        field.setAccessible(true);
        Class<?> type = field.getType();
        if (type.getTypeName().equals("java.util.Date")) {
            Date date = (Date) field.get(obj);
            if (date != null) {
                formatDate = dateFormat.format(date);
            }
        }

        return formatDate;
    }

    /**
     * 将属性从source拷贝到map中，只拷贝非空属性。如果bean的表层属性有Date，那么格式化成String类型
     *
     * @param source         源对象
     * @param exceptionNames 不拷贝属性名单，如果没有不拷贝的属性，那么传入null
     * @return 返回拷贝后的map
     */
    public static Map<String, Object> bean2Map(Object source, String[] exceptionNames) {
        Class<?> sourceClass = source.getClass();

        List<Field> allSourceFields = getAllDeclaredFields(sourceClass);

        Map<String, Object> result = new HashMap<>();

        List<String> list = null;
        if (exceptionNames != null) {
            list = Arrays.asList(exceptionNames);
        }

        try {
            //遍历对象所有属性
            for (Field sourceFiled : allSourceFields) {
                String sourceFiledName = sourceFiled.getName();

                //检查当前属性是否需要排除
                boolean isRemoved = false;
                if (list != null) {
                    isRemoved = list.contains(sourceFiledName);
                }

                //如果当前属性不排除，拷贝到数组中
                if (!isRemoved) {
                    sourceFiled.setAccessible(true);
                    //如果是日期类型，将其转换成指定格式
                    if (sourceFiled.getType().getTypeName().equals("java.util.Date")) {
                        String formatDate = formatDate(source, sourceFiled);
                        result.put(sourceFiledName, formatDate);
                    } else {
                        //如果不是日期类型，且属性不为空，将其拷贝到map中
                        Object sourceProperty = sourceFiled.get(source);
                        if (sourceProperty != null) {
                            result.put(sourceFiledName, sourceProperty);
                        }
                    }
                }
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }

        return result;
    }

    /**
     * 将属性从source拷贝到target，对于某个属性，当且仅当它在source中的类型和名称与在
     * target中的都一致，并且值不为null时，才执行拷贝，无论该属性来自source的父类还是source本身。
     *
     * @param source
     * @param target
     */
    public static void copyProperties(Object source, Object target) {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        List<Field> allSourceFileds = getAllDeclaredFields(sourceClass);
        List<Field> allTargetFileds = getAllDeclaredFields(targetClass);

        for (Field sourceFiled : allSourceFileds) {
            sourceFiled.setAccessible(true);

            String sourceFiledName = sourceFiled.getName();
            Class<?> sourceFiledType = sourceFiled.getType();

            for (Field targetFiled : allTargetFileds) {
                String targetFiledName = targetFiled.getName();
                Class<?> targetFiledType = targetFiled.getType();
                if (sourceFiledName.equals(targetFiledName)
                        && sourceFiledType.getTypeName().equals(targetFiledType.getTypeName())) {
                    Object sourceProperty = null;
                    try {
                        sourceProperty = sourceFiled.get(source);
                        if (sourceProperty != null) {
                            targetFiled.setAccessible(true);
                            targetFiled.set(target, sourceProperty);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }

        }

    }

    /**
     * 获取一个类的所有属性，包括父类（Object除外）
     *
     * @param clazz
     * @return
     */
    public static List<Field> getAllDeclaredFields(Class clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null &&
                !clazz.getName().toLowerCase().equals("java.lang.object")) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass(); //得到父类,然后赋给自己
        }
        return fieldList;
    }

    /**
     * 判断字符串是否为空
     * @param str 待判断的字符串
     */
    public static boolean isEmpty(String str) {
        if ("".equals(str) || str == null) {
            return true;
        } else {
            return false;
        }

    }
}
