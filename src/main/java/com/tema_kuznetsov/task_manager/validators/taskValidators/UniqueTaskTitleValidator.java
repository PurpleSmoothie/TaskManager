package com.tema_kuznetsov.task_manager.validators.taskValidators;

import com.tema_kuznetsov.task_manager.annotations.taskAnnotations.UniqueTaskTitle;
import com.tema_kuznetsov.task_manager.repositories.TaskRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Валидатор для аннотации UniqueTaskTitle, проверяет, что заголовок задачи уникален в системе.
 */
public class UniqueTaskTitleValidator implements ConstraintValidator<UniqueTaskTitle, String> {
    @Autowired
    private TaskRepository taskRepository;

    /**
     * Проверяет, что заголовок задачи уникален в системе.
     *
     * @param title   Заголовок задачи.
     * @param context Контекст валидатора.
     * @return true, если заголовок уникален, иначе false.
     */
    @Override
    public boolean isValid(String title, ConstraintValidatorContext context) {
        if (title == null || title.isBlank()) {
            return true;
        }
        return !taskRepository.existsByTitle(title);
    }
}