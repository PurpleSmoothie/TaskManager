package com.tema_kuznetsov.task_manager.models.enums;

import java.util.Set;

public class TaskPriority {
    public static final String LOW = "LOW";
    public static final String MEDIUM = "MEDIUM";
    public static final String HIGH = "HIGH";
    public static final String CRITICAL = "CRITICAL";

    public static boolean isValid(String status) {
        return status != null && (
                status.equals(LOW) ||
                        status.equals(MEDIUM) ||
                        status.equals(HIGH) ||
                        status.equals(CRITICAL)
        );
    }

    // Список приоритетов для ошибки
    public static final Set<String> VALID_PRIORITIES = Set.of("LOW", "MEDIUM", "HIGH", "CRITICAL");
}
