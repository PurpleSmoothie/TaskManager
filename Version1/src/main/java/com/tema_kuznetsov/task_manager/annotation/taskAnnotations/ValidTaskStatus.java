package com.tema_kuznetsov.task_manager.annotation.taskAnnotations;

import com.tema_kuznetsov.task_manager.validator.taskValidator.TaskStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Target;

import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = TaskStatusValidator.class)
public @interface ValidTaskStatus {
    String message() default "Invalid task status";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
