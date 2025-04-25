package com.tema_kuznetsov.task_manager.validators.taskValidators;

import com.tema_kuznetsov.task_manager.annotations.taskAnnotations.NullableTaskStatus;
import com.tema_kuznetsov.task_manager.models.enums.TaskStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullableStatusValidator implements ConstraintValidator<NullableTaskStatus, String> {

    @Override
    public boolean isValid(String status, ConstraintValidatorContext context) {
        if (status == null || status.isBlank()) {
            return true;
        }

        if (!TaskStatus.VALID_STATUSES.contains(status)) {
            buildErrorMessage(context,
                    "Недопустимый статус '" + status + "'. Допустимые значения: " +
                            String.join(", ", TaskStatus.VALID_STATUSES));
            return false;
        }

        return true;
    }

    private void buildErrorMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}