package com.tema_kuznetsov.task_manager.annotation.taskAnnotations;

import com.tema_kuznetsov.task_manager.validators.taskValidator.UniqueTaskTitleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Target;

import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = UniqueTaskTitleValidator.class)
public @interface UniqueTaskTitle {
    String message() default "Task title already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
