package com.tema_kuznetsov.task_manager.validators.taskValidators;

import com.tema_kuznetsov.task_manager.services.TaskService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Валидатор, который проверяет, является ли пользователь владельцем задачи или исполнителем.
 * Используется для проверки прав доступа пользователя к задаче.
 */
@Component("taskOwnerValidator")
public class TaskOwnerValidator {

    private final TaskService taskService;

    /**
     * Конструктор для инициализации TaskOwnerValidator с сервисом задач.
     *
     * @param taskService Сервис для работы с задачами.
     */
    public TaskOwnerValidator(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Проверяет, является ли пользователь владельцем задачи.
     *
     * @param taskId        ID задачи, для которой выполняется проверка.
     * @param authentication Аутентификация текущего пользователя.
     * @return true, если пользователь является владельцем задачи, иначе false.
     */
    public boolean isTaskOwner(Long taskId, Authentication authentication) {
        String userEmail = authentication.getName();
        return taskService.isOwner(taskId, userEmail);
    }

    /**
     * Проверяет, является ли пользователь исполнителем задачи.
     *
     * @param taskId        ID задачи, для которой выполняется проверка.
     * @param authentication Аутентификация текущего пользователя.
     * @return true, если пользователь является исполнителем задачи, иначе false.
     */
    public boolean isTaskPerformer(Long taskId, Authentication authentication) {
        String userEmail = authentication.getName();
        return taskService.isPerformer(taskId, userEmail);
    }
}