package com.tema_kuznetsov.task_manager.annotations.taskAnnotations;

import com.tema_kuznetsov.task_manager.validators.taskValidators.NullablePriorityValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = NullablePriorityValidator.class)
public @interface NullableTaskPriority {
    String message() default "Недопустимый приоритет задачи";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
