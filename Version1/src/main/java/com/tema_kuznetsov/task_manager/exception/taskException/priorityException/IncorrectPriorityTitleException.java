package com.tema_kuznetsov.task_manager.exception.taskException.priorityException;

import com.tema_kuznetsov.task_manager.model.enums.TaskPriority;

public class IncorrectPriorityTitleException extends RuntimeException {
    public IncorrectPriorityTitleException(String priority) {
        super("Invalid priority: '" + priority + "'. Allowed values: " + String.join(", ", TaskPriority.VALID_PRIORITIES));
    }
}