package com.tema_kuznetsov.task_manager.annotations.userAnnotations;

import com.tema_kuznetsov.task_manager.validators.userValidators.UniqueUserLoginValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = UniqueUserLoginValidator.class)
public @interface UniqueUserLogin {
    String message() default "Пользователь с таким логином уже существует";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
