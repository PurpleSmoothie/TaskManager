package com.tema_kuznetsov.task_manager.exceptions.taskException.priorityException;

import com.tema_kuznetsov.task_manager.models.enums.TaskPriority;

public class IncorrectPriorityTitleException extends RuntimeException {
    public IncorrectPriorityTitleException(String priority) {
        super("Invalid priority: '" + priority + "'. Allowed values: " + String.join(", ", TaskPriority.VALID_PRIORITIES));
    }
}