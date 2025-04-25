package com.tema_kuznetsov.task_manager.validators.userValidators;

import com.tema_kuznetsov.task_manager.annotations.userAnnotations.NullableEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NullableEmailValidator implements ConstraintValidator<NullableEmail, String> {

    private final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return EMAIL_PATTERN.matcher(value).matches();
    }
}