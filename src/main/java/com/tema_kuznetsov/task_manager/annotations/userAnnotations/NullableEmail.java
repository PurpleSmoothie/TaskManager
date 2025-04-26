package com.tema_kuznetsov.task_manager.annotations.userAnnotations;

import com.tema_kuznetsov.task_manager.validators.userValidators.NullableEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NullableEmailValidator.class)
@Documented
public @interface NullableEmail {
    String message() default "Некорректный формат email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
