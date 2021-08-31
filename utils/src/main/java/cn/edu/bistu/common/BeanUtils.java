package cn.edu.bistu.common;


import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class BeanUtils {

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
     * @param source 源对象
     * @param exceptionNames 不拷贝属性名单，如果没有不拷贝的属性，那么传入null
     * @return 返回拷贝后的map
     */
    public static Map<String, Object> bean2Map(Object source, String[] exceptionNames) {
        Class<?> sourceClass = source.getClass();

        List<Field> allSourceFields = getAllDeclaredFields(sourceClass);

        Map<String, Object> result = new HashMap<>();

        List<String> list = null;
        if(exceptionNames != null) {
            list = Arrays.asList(exceptionNames);
        }

        try {
            //遍历对象所有属性
            for (Field sourceFiled : allSourceFields) {
                String sourceFiledName = sourceFiled.getName();

                //检查当前属性是否需要排除
                boolean isRemoved = false;
                if(list != null) {
                    isRemoved = list.contains(sourceFiledName);
                }

                //如果当前属性不排除，拷贝到数组中
                if (!isRemoved) {
                    sourceFiled.setAccessible(true);
                    //如果是日期类型，将其转换成指定格式
                    if(sourceFiled.getType().getTypeName().equals("java.util.Date")) {
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
}
