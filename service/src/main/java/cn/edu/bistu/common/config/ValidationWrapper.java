package cn.edu.bistu.common.config;

import cn.edu.bistu.common.BeanUtils;
import cn.edu.bistu.common.exception.ParameterMissing;
import cn.edu.bistu.common.exception.ParameterRedundent;
import cn.edu.bistu.constants.ResultCodeEnum;
import cn.edu.bistu.model.common.Result;
import cn.edu.bistu.model.entity.ApprovalRecord;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.lang.reflect.Field;
import java.util.*;

@Data
@Slf4j
public class ValidationWrapper {

    String[] requiredPropsName = new String[0];
    String[] optionalPropsName = new String[0];;

    Validator validator;

    public ValidationWrapper(Validator validator) {
        this.validator = validator;
    }


    public List<String> checkMissingParam(Object obj) throws IllegalAccessException {
        List<String> missingParams = new ArrayList<>();
        List<Field> fieldList = BeanUtils.getAllDeclaredFields(obj.getClass());
        for (int i = 0; i < requiredPropsName.length; i++) {
            String requiredName = requiredPropsName[i];
            boolean missing = true;
            for (Field field : fieldList) {
                if (!BeanUtils.isFieldNull(obj, field)) {
                    if (field.getName().equals(requiredName)) {
                        missing = false;
                        break;
                    }
                }
            }
            if (missing) {
                missingParams.add(requiredName);
            }
        }

        return missingParams;
    }


    public List<String> checkRedundantParam(Object obj) throws IllegalAccessException {
        List<Field> allDeclaredFields = BeanUtils.getAllDeclaredFields(obj.getClass());
        List<String> redundantParams = new ArrayList<>();
        for (Field field : allDeclaredFields) {
            String propName = field.getName();


            if (!BeanUtils.isFieldNull(obj, field)) {
                boolean is_required = false;
                boolean is_optional = false;

                for (int i = 0; i < requiredPropsName.length; i++) {
                    String requiredPropName = requiredPropsName[i];
                    if (propName.equals(requiredPropName)) {
                        is_required = true;
                        break;
                    }
                }


                if (!is_required) {

                    for (int i = 0; i < optionalPropsName.length; i++) {
                        String optionalPropName = optionalPropsName[i];
                        if (propName.equals(optionalPropName)) {
                            is_optional = true;
                            break;
                        }
                    }
                }

                if (!(is_optional || is_required)) {
                    redundantParams.add(propName);
                }
            }
        }

        return redundantParams;
    }

    public void checkParamIntegrity(Object object) throws IllegalAccessException {

        List<String> missingParam = checkMissingParam(object);
        if (!missingParam.isEmpty()) {
            throw new ParameterMissing(missingParam);
        }

        List<String> redundantParams = this.checkRedundantParam(object);
        if (!redundantParams.isEmpty()) {
            throw new ParameterRedundent(redundantParams);
            //return Result.build(redundantParams, ResultCodeEnum.FRONT_DATA_MISSING);
        }

    }

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

    public void setPropsNameNull() {
        requiredPropsName = new String[0];
        optionalPropsName = new String[0];
    }

}
