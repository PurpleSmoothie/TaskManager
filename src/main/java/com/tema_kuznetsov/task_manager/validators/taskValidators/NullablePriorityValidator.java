package com.tema_kuznetsov.task_manager.validators.taskValidators;

import com.tema_kuznetsov.task_manager.annotations.taskAnnotations.NullableTaskPriority;
import com.tema_kuznetsov.task_manager.models.enums.TaskPriority;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Валидатор для аннотации NullableTaskPriority, проверяет, что приоритет задачи либо пустой, либо входит в допустимые значения.
 */
public class NullablePriorityValidator implements ConstraintValidator<NullableTaskPriority, String> {

    /**
     * Проверяет, что значение приоритета задачи либо пустое, либо входит в допустимые значения.
     *
     * @param priority Значение приоритета задачи.
     * @param context  Контекст валидатора.
     * @return true, если приоритет допустим, иначе false.
     */
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

    /**
     * Строит сообщение об ошибке в случае недопустимого значения приоритета.
     *
     * @param context Контекст валидатора.
     * @param message Сообщение об ошибке.
     */
    private void buildErrorMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}