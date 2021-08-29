package cn.edu.bistu.common.config;

import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.exception.ParameterMissingException;
import cn.edu.bistu.common.exception.ParameterRedundentException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 主要用于验证前端参数是否冗余或缺失，空属性视为不存在
 * 属性缺失：如果一个属性为空且属性名在requiredPropsName列表中，那么该属性缺失
 * 属性多余：如果一个属性非空且属性名既不在requiredPropsName列表中，也不在optionalPropsName列表中,那么该属性多余
 */
@Data
@Slf4j
public class ValidationWrapper {

    /**
     * 指定前端必须传递的参数，不允许缺失
     */
    String[] requiredPropsName = new String[0];

    /**
     * 指定前端可选的参数，允许缺失
     */
    String[] optionalPropsName = new String[0];

    /**
     * javax.validation.Validator接口对象，用于其他的验证
     */
    Validator validator;

    /**
     * @param validator 从spring容器中传入一个validator。
     */
    public ValidationWrapper(Validator validator) {
        this.validator = validator;
    }

    /**
     * 检测指定对象是否缺失属性
     *
     * @param obj 待检测的对象
     * @return 缺失的属性名称列表
     * @throws IllegalAccessException
     */
    private List<String> checkMissingParam(Object obj) throws IllegalAccessException {
        //缺失属性列表
        List<String> missingParams = new ArrayList<>();

        //获取对象的所有属性
        List<Field> fieldList = BeanUtils.getAllDeclaredFields(obj.getClass());

        //遍历必需属性
        for (int i = 0; i < requiredPropsName.length; i++) {
            String requiredName = requiredPropsName[i];
            boolean missing = true;
            //检查指定对象是否含有某必需属性
            for (Field field : fieldList) {
                if (!BeanUtils.isFieldNull(obj, field)) {
                    if (field.getName().equals(requiredName)) {
                        missing = false;
                        break;
                    }
                }
            }
            //如果属性缺失，添加到缺失属性列表
            if (missing) {
                missingParams.add(requiredName);
            }
        }

        return missingParams;
    }


    /**
     * 检测指定对象是否有冗余属性
     *
     * @param obj 待检测的对象
     * @return 冗余的属性名称列表
     * @throws IllegalAccessException
     */
    private List<String> checkRedundantParam(Object obj) throws IllegalAccessException {
        //存放缺失属性名称
        List<String> redundantParams = new ArrayList<>();

        //获取对象所有属性
        List<Field> allDeclaredFields = BeanUtils.getAllDeclaredFields(obj.getClass());

        //遍历所有属性
        for (Field field : allDeclaredFields) {
            String propName = field.getName();

            if (!BeanUtils.isFieldNull(obj, field)) {
                boolean is_required = false;
                boolean is_optional = false;

                //检查该属性是否必需
                for (int i = 0; i < requiredPropsName.length; i++) {
                    String requiredPropName = requiredPropsName[i];
                    if (propName.equals(requiredPropName)) {
                        is_required = true;
                        break;
                    }
                }

                //检查该属性是否可选，若属性必须，则不必检查是否可选
                if (!is_required) {

                    for (int i = 0; i < optionalPropsName.length; i++) {
                        String optionalPropName = optionalPropsName[i];
                        if (propName.equals(optionalPropName)) {
                            is_optional = true;
                            break;
                        }
                    }
                }

                //若属性既不是必须的，也不是可选的，那么它是多余的
                if (!(is_optional || is_required)) {
                    redundantParams.add(propName);
                }
            }
        }

        return redundantParams;
    }

    /**
     * 检查对象的属性完整性，以异常形式抛出多余属性或缺失属性，若不抛出异常，说明对象属性完整。
     *
     * @param object 待检测的对象
     * @throws ParameterMissingException   若缺失属性，则抛出ParameterMissing异常
     * @throws ParameterRedundentException 若缺失属性，则抛出ParameterMissing异常
     * @throws IllegalAccessException
     */
    public void checkParamIntegrity(Object object) throws IllegalAccessException {

        //获取缺失属性列表
        List<String> missingParam = checkMissingParam(object);
        if (!missingParam.isEmpty()) {
            throw new ParameterMissingException(missingParam);
        }

        //获取多余属性列表
        List<String> redundantParams = this.checkRedundantParam(object);
        if (!redundantParams.isEmpty()) {
            throw new ParameterRedundentException(redundantParams);
        }

        //清空必需列表和可选列表，不要阻碍下次使用该对象。
        setPropsNameNull();
    }

    /**
     * 在每次调用checkParamIntegrity方法后自动调用该方法清空两个列表，防止阻碍下次使用该对象。
     */
    public void setPropsNameNull() {
        requiredPropsName = new String[0];
        optionalPropsName = new String[0];
    }

    /**
     * 暂时用不到的方法
     * @param object
     * @param <T>
     * @return
     */
    public <T> Set<ConstraintViolation<T>> validate(T object) {

        if (requiredPropsName == null) {
            return validator.validate(object);
        }

        Set<ConstraintViolation<T>> result = new HashSet<>();
        for (String name : requiredPropsName) {
            Set<ConstraintViolation<T>> ConstraintViolation = validator.validateProperty(object, name);
            if (!ConstraintViolation.isEmpty()) {
                result.addAll(ConstraintViolation);
            }
        }

        return result;
    }

}
