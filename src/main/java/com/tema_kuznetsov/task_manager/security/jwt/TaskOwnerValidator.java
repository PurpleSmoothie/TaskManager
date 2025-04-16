package com.tema_kuznetsov.task_manager.security.jwt;

import com.tema_kuznetsov.task_manager.service.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("taskOwnerValidator")
public class TaskOwnerValidator {

    private final TaskService taskService;

    public TaskOwnerValidator(TaskService taskService) {
        this.taskService = taskService;
    }

    public boolean isTaskOwner(Long taskId, Authentication authentication) {
        // Логика проверки, является ли пользователь владельцем задачи
        String userEmail = authentication.getName();
        return taskService.isOwner(taskId, userEmail);
    }

    public boolean isTaskPerformer(Long taskId, Authentication authentication) {
        // Логика проверки, является ли пользователь владельцем задачи
        String userEmail = authentication.getName();
        return taskService.isPerformer(taskId, userEmail);
    }
}
