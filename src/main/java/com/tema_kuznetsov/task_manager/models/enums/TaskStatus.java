package com.tema_kuznetsov.task_manager.models.enums;

import java.util.Set;

/**
 * Класс, содержащий возможные статусы задач.
 * Предоставляет константы для различных статусов задач и метод для проверки их допустимости.
 */
public class TaskStatus {

    /**
     * Открытая задача.
     */
    public static final String OPEN = "OPEN";

    /**
     * Задача в процессе выполнения.
     */
    public static final String IN_PROGRESS = "IN_PROGRESS";

    /**
     * Завершённая задача.
     */
    public static final String COMPLETED = "COMPLETED";

    /**
     * Отменённая задача.
     */
    public static final String CANCELLED = "CANCELLED";

    /**
     * Проверяет, является ли переданный статус допустимым статусом задачи.
     *
     * @param status Статус задачи для проверки.
     * @return true, если статус является допустимым, иначе false.
     */
    public static boolean isValid(String status) {
        return status != null && (
                status.equals(OPEN) ||
                        status.equals(IN_PROGRESS) ||
                        status.equals(COMPLETED) ||
                        status.equals(CANCELLED)
        );
    }

    /**
     * Множество допустимых значений статусов задач.
     */
    public static final Set<String> VALID_STATUSES = Set.of(OPEN, IN_PROGRESS, COMPLETED, CANCELLED);
}