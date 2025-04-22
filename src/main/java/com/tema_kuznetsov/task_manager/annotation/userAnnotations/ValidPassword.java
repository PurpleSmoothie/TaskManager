package com.tema_kuznetsov.task_manager.annotation.userAnnotations;

import com.tema_kuznetsov.task_manager.validators.userValidator.UserPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UserPasswordValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Password must contain at least one letter";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
