package com.tema_kuznetsov.task_manager.exception.taskException.statusException;

import com.tema_kuznetsov.task_manager.model.enums.TaskStatus;

public class IncorrectStatusTitleException extends RuntimeException {
    public IncorrectStatusTitleException(String status) {
        super("Invalid status: '" + status + "'. Allowed values: " + String.join(", ", TaskStatus.VALID_STATUSES));
    }
}
