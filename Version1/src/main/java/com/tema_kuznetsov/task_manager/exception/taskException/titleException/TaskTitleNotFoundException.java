package com.tema_kuznetsov.task_manager.exception.taskException.titleException;

public class TaskTitleNotFoundException extends RuntimeException {
    public TaskTitleNotFoundException(String title) {
        super("Task with title '" + title + "' is not found");
    }

}
