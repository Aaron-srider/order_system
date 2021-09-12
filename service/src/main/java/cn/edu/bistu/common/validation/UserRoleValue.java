package cn.edu.bistu.common.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserRoleValueValidator.class)
public @interface UserRoleValue {
    String message() default "";
    String[] roleCases();
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}