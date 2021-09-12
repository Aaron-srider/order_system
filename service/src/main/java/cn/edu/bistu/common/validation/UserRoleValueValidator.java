package cn.edu.bistu.common.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserRoleValueValidator implements ConstraintValidator<UserRoleValue, String> {
    private String[] roleCases;

    public void initialize(UserRoleValue constraint) {
        roleCases = constraint.roleCases();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        for (String roleCase : roleCases) {
            if(roleCase.equals(value)) {
                return  true;
            }
        }
        return false;
    }

}
