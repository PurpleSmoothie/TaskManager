package com.tema_kuznetsov.task_manager.validators.taskValidators;

import com.tema_kuznetsov.task_manager.annotations.taskAnnotations.NullableTaskPriority;
import com.tema_kuznetsov.task_manager.models.enums.TaskPriority;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NullablePriorityValidator implements ConstraintValidator<NullableTaskPriority, String> {

    @Override
    public boolean isValid(String priority, ConstraintValidatorContext context) {
        if (priority == null || priority.isBlank()) {
            return true;
        }

        if (!TaskPriority.VALID_PRIORITIES.contains(priority)) {
            buildErrorMessage(context,
                    "Недопустимый приоритет '" + priority + "'. Допустимые значения: " +
                            String.join(", ", TaskPriority.VALID_PRIORITIES));
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
