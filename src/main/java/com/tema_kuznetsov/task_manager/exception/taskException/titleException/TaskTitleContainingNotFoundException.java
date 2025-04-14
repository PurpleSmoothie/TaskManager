package com.tema_kuznetsov.task_manager.exception.taskException.titleException;

public class TaskTitleContainingNotFoundException extends RuntimeException {
    public TaskTitleContainingNotFoundException(String message) {
        super("Task title containing '" + message + "' is not found");
    }
}
