package com.tema_kuznetsov.task_manager.validators.userValidators;

import com.tema_kuznetsov.task_manager.annotations.userAnnotations.NullableSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableSizeValidator implements ConstraintValidator<NullableSize, String> {

    private int min;
    private int max;

    @Override
    public void initialize(NullableSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        int length = value.length();
        return length >= min && length <= max;
    }
}