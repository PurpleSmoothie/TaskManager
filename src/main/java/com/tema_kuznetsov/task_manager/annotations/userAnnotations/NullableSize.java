package com.tema_kuznetsov.task_manager.annotations.userAnnotations;

import com.tema_kuznetsov.task_manager.validators.userValidators.NullableSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Аннотация для проверки, что строка имеет допустимую длину, если она предоставлена.
 * Валидатор использует {@link NullableSizeValidator}.
 */
@Documented
@Constraint(validatedBy = NullableSizeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NullableSize {
    /**
     * Сообщение, которое будет отображаться при ошибке валидации.
     *
     * @return Сообщение об ошибке.
     */
    String message() default "Поле должно содержать от {min} до {max} символов";

    /**
     * Минимальная длина строки.
     *
     * @return Минимальная длина.
     */
    int min() default 0;

    /**
     * Максимальная длина строки.
     *
     * @return Максимальная длина.
     */
    int max() default Integer.MAX_VALUE;

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