package com.tema_kuznetsov.task_manager.validators.taskValidator;

import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.ValidTaskStatus;
import com.tema_kuznetsov.task_manager.models.enums.TaskStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaskStatusValidator implements ConstraintValidator<ValidTaskStatus, String> {

    @Override
    public boolean isValid(String status, ConstraintValidatorContext context) {
        if (status == null || status.isBlank()) {
            return true; // Пропускаем, это проверяет @NotBlank
        }

        if (!TaskStatus.VALID_STATUSES.contains(status)) {
            buildErrorMessage(context,
                    "Invalid task status '" + status + "'. Allowed values: " +
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