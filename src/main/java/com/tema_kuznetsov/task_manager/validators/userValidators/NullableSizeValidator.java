package com.tema_kuznetsov.task_manager.validators.userValidators;

import com.tema_kuznetsov.task_manager.annotations.userAnnotations.NullableSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Валидатор для аннотации NullableSize, проверяет, что длина строки находится в пределах допустимого диапазона.
 * Если значение пустое, считается, что оно валидно.
 */
public class NullableSizeValidator implements ConstraintValidator<NullableSize, String> {

    private int min;
    private int max;

    /**
     * Инициализация валидатора с минимальной и максимальной длиной.
     *
     * @param constraintAnnotation Аннотация с минимальной и максимальной длиной.
     */
    @Override
    public void initialize(NullableSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    /**
     * Проверяет, что длина строки в пределах от min до max. Пустая строка считается валидной.
     *
     * @param value   Значение для проверки.
     * @param context Контекст валидатора.
     * @return true, если длина строки валидна, иначе false.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        int length = value.length();
        return length >= min && length <= max;
    }
}