package com.tema_kuznetsov.task_manager.annotations.userAnnotations;

import com.tema_kuznetsov.task_manager.validators.userValidators.UniqueUserLoginValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Аннотация для проверки уникальности логина пользователя в системе.
 * Валидатор использует {@link UniqueUserLoginValidator}.
 */
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = UniqueUserLoginValidator.class)
public @interface UniqueUserLogin {
    /**
     * Сообщение, которое будет отображаться при ошибке валидации.
     *
     * @return Сообщение об ошибке.
     */
    String message() default "Пользователь с таким логином уже существует";

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