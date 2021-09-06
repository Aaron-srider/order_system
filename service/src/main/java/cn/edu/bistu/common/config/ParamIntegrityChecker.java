package cn.edu.bistu.common.config;

import cn.edu.bistu.common.exception.FrontDataMissingException;
import cn.edu.bistu.constants.ResultCodeEnum;
import lombok.Data;

import java.util.*;

/**
 * 使用注意：每次完整性检查前必须先设置必填参数列表和可选参数列表，即先调用setRequiredPropsName()和
 * setOptionalPropsName()方法，再调用checkMapParamIntegrity()方法检测map参数的
 * 完整性，如果不设置，则视为不指定即不做必填参数检测或可选参数检测，上述为一次完整性检查过程。
 */
@Data
public class ParamIntegrityChecker {
    /**
     * 指定前端必须传递的参数，不允许缺失
     */
    String[] requiredPropsName = new String[0];

    /**
     * 指定前端可选的参数，允许缺失
     */
    Map<String, Object> optionalPropsName = new HashMap<>();

    /**
     * 检查参数的完整性，对必填参数和可选参数，处理方式略有不同：
     *          必填参数：
     *              检测参数map中是否包含指定的参数，若不包含，通过异常抛出缺失的参数名
 *              可选参数：
     *              检测参数map中是否包含指定的参数，若不包含，在map中添加可选参数默认值的键值对
     * @param map 前端传来的参数
     */
    public void checkMapParamIntegrity(Map<String, Object> map)  {

        //遍历必要参数，逐个检测map中是否包含指定属性（value为null或者key不存在为不包含）
        List<String> missingPropsName = new ArrayList<>();
        for (String propName : requiredPropsName) {
            boolean contains = contains(map, propName);
            if (!contains) {
                missingPropsName.add(propName);
            }
        }

        //抛出丢失的参数
        if (missingPropsName.size() > 0) {
            throw new FrontDataMissingException(missingPropsName, ResultCodeEnum.FRONT_DATA_MISSING);
        }

        //遍历选填参数，如果没有传，那么填写默认值，如果传了，那么不改变前端传递的数据
        Set<String> keySet = optionalPropsName.keySet();
        for (String propName : keySet) {
            boolean contains = map.containsKey(propName);
            if (!contains) {
                map.put(propName, optionalPropsName.get(propName));
            }
        }

        //清空必需列表和可选列表，不要阻碍下次使用该对象。
        clearChecker();
    }

    /**
     * 在每次调用checkParamIntegrity方法后自动调用该方法清空两个列表，防止阻碍下次使用该对象。
     */
    public void clearChecker() {
        requiredPropsName = new String[0];
        optionalPropsName = new HashMap<>();
    }

    /**
     * 判断指定参数在可选参数map中是否存在，判断基准如下:
     *      若map中不包含指定的键，或者指定键对应的值是null，那么判定为不包含指定的参数；
     *      若map中包含键且键对应的值非空，那么判定为包含。
     * @param map 前端传来的参数map
     * @param propName 待判定是否存在的参数名
     * @return  若存在返回true，否则返回false
     */
    public boolean contains(Map<String, Object> map, String propName) {
        boolean containsKey = map.containsKey(propName);
        return containsKey && map.get(propName) != null;
    }

}
