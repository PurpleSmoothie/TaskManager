package com.tema_kuznetsov.task_manager.models.enums;

import java.util.Set;

public class TaskStatus {
    public static final String OPEN = "OPEN";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String COMPLETED = "COMPLETED";
    public static final String CANCELLED = "CANCELLED";

    public static boolean isValid(String status) {
        return status != null && (
                status.equals(OPEN) ||
                        status.equals(IN_PROGRESS) ||
                        status.equals(COMPLETED) ||
                        status.equals(CANCELLED)
        );
    }

    // Список cтатусов для ошибки
    public static final Set<String> VALID_STATUSES = Set.of("OPEN", "IN_PROGRESS", "COMPLETED", "CANCELLED");
}
