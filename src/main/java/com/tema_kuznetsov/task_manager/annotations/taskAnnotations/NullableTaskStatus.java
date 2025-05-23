package com.tema_kuznetsov.task_manager.annotations.taskAnnotations;

import com.tema_kuznetsov.task_manager.validators.taskValidators.NullableStatusValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Аннотация для проверки, что статус задачи является допустимым, или оставлен пустым.
 * Валидатор использует {@link NullableStatusValidator}.
 */
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = NullableStatusValidator.class)
public @interface NullableTaskStatus {
    /**
     * Сообщение, которое будет отображаться при ошибке валидации.
     *
     * @return Сообщение об ошибке.
     */
    String message() default "Недопустимый статус задачи";

    /**
     * Группы, к которым относится аннотация.
     *
     * @return Группы.
     */
    Class<?>[] groups() default {};

    /**
     * Дополнительная информация, которая может быть использована валидатором.
     *
     * @return Дополнительная информация.
     */
    Class<? extends Payload>[] payload() default {};
}