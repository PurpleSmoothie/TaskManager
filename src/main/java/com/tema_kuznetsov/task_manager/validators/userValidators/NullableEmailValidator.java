package com.tema_kuznetsov.task_manager.validators.userValidators;

import com.tema_kuznetsov.task_manager.annotations.userAnnotations.NullableEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Валидатор для аннотации NullableEmail, проверяет, что значение является корректным адресом электронной почты.
 * Если значение не пустое, проверяет его на соответствие формату email.
 */
public class NullableEmailValidator implements ConstraintValidator<NullableEmail, String> {

    private final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

    /**
     * Проверяет, что значение является корректным email-адресом или пустым.
     *
     * @param value   Значение email.
     * @param context Контекст валидатора.
     * @return true, если значение пустое или соответствует формату email, иначе false.
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return EMAIL_PATTERN.matcher(value).matches();
    }
}