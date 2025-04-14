package com.tema_kuznetsov.task_manager.validator.taskValidator;

import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.UniqueTaskTitle;
import com.tema_kuznetsov.task_manager.repository.TaskRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class UniqueTaskTitleValidator implements ConstraintValidator<UniqueTaskTitle, String> {
    @Autowired
    private TaskRepository taskRepository;

    @Override
    public boolean isValid(String title, ConstraintValidatorContext context) {
        if (title == null || title.isBlank()) {
            return true; // Пропускаем, это проверяет @NotBlank
        }
        // Проверка уникальности только для непустых значений
        return !taskRepository.existsByTitle(title);
    }
}