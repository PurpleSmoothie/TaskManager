package com.tema_kuznetsov.task_manager.validators.taskValidators;

import com.tema_kuznetsov.task_manager.annotations.taskAnnotations.NullableTaskStatus;
import com.tema_kuznetsov.task_manager.models.enums.TaskStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Валидатор для аннотации NullableTaskStatus, проверяет, что статус задачи либо пустой, либо входит в допустимые значения.
 */
public class NullableStatusValidator implements ConstraintValidator<NullableTaskStatus, String> {

    /**
     * Проверяет, что значение статуса задачи либо пустое, либо входит в допустимые значения.
     *
     * @param status Значение статуса задачи.
     * @param context Контекст валидатора.
     * @return true, если статус допустим, иначе false.
     */
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

    /**
     * Строит сообщение об ошибке в случае недопустимого значения статуса.
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