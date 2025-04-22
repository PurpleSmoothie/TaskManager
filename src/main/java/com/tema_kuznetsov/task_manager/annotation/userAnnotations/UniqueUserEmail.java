package com.tema_kuznetsov.task_manager.annotation.userAnnotations;

import com.tema_kuznetsov.task_manager.validators.userValidator.UniqueUserEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = UniqueUserEmailValidator.class)
public @interface UniqueUserEmail {
    String message() default "User with this email already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
