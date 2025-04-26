package com.tema_kuznetsov.task_manager.annotations.userAnnotations;

import com.tema_kuznetsov.task_manager.validators.userValidators.NullableSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NullableSizeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NullableSize {
    String message() default "Поле должно содержать от {min} до {max} символов";

    int min() default 0;
    int max() default Integer.MAX_VALUE;

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
