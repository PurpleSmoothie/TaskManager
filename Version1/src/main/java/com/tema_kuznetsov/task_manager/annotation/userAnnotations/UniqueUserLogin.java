package com.tema_kuznetsov.task_manager.annotation.userAnnotations;

import com.tema_kuznetsov.task_manager.validator.taskValidator.UniqueTaskTitleValidator;
import com.tema_kuznetsov.task_manager.validator.userValidator.UniqueUserLoginValidator;
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
    String message() default "User login already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
