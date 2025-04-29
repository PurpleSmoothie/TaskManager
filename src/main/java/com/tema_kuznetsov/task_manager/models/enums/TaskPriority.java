package com.tema_kuznetsov.task_manager.models.enums;

import java.util.Set;

/**
 * Класс, содержащий возможные приоритеты задач.
 * Предоставляет константы для различных уровней приоритетов и метод для проверки их допустимости.
 */
public class TaskPriority {

    /**
     * Низкий приоритет задачи.
     */
    public static final String LOW = "LOW";

    /**
     * Средний приоритет задачи.
     */
    public static final String MEDIUM = "MEDIUM";

    /**
     * Высокий приоритет задачи.
     */
    public static final String HIGH = "HIGH";

    /**
     * Критический приоритет задачи.
     */
    public static final String CRITICAL = "CRITICAL";

    /**
     * Проверяет, является ли переданный статус допустимым приоритетом задачи.
     *
     * @param status Статус задачи для проверки.
     * @return true, если статус является допустимым приоритетом, иначе false.
     */
    public static boolean isValid(String status) {
        return status != null && (
                status.equals(LOW) ||
                        status.equals(MEDIUM) ||
                        status.equals(HIGH) ||
                        status.equals(CRITICAL)
        );
    }

    /**
     * Множество допустимых значений приоритетов задач.
     */
    public static final Set<String> VALID_PRIORITIES = Set.of(LOW, MEDIUM, HIGH, CRITICAL);
}
