package com.tema_kuznetsov.task_manager.annotation.taskAnnotations;

import com.tema_kuznetsov.task_manager.validators.taskValidator.TaskPriorityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Target;

import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = TaskPriorityValidator.class)
public @interface ValidTaskPriority {
    String message() default "Invalid task priority";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
