package com.tema_kuznetsov.task_manager.annotations.userAnnotations;

import com.tema_kuznetsov.task_manager.validators.userValidators.UniqueUserEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Аннотация для проверки уникальности email пользователя в системе.
 * Валидатор использует {@link UniqueUserEmailValidator}.
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = UniqueUserEmailValidator.class)
public @interface UniqueUserEmail {
    /**
     * Сообщение, которое будет отображаться при ошибке валидации.
     *
     * @return Сообщение об ошибке.
     */
    String message() default "Пользователь с таким email уже существует";

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