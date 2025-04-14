package com.tema_kuznetsov.task_manager.validator.taskValidator;

import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.ValidTaskPriority;
import com.tema_kuznetsov.task_manager.model.enums.TaskPriority;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TaskPriorityValidator implements ConstraintValidator<ValidTaskPriority, String> {

    @Override
    public boolean isValid(String priority, ConstraintValidatorContext context) {
        if (priority == null || priority.isBlank()) {
            return true; // Пропускаем, это проверяет @NotBlank
        }

        if (!TaskPriority.VALID_PRIORITIES.contains(priority)) {
            buildErrorMessage(context,
                    "Invalid task priority '" + priority + "'. Allowed values: " +
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
