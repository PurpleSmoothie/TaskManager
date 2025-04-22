package com.tema_kuznetsov.task_manager.exceptions.taskException.titleException;

public class TaskTitleNotFoundException extends RuntimeException {
    public TaskTitleNotFoundException(String title) {
        super("Task with title '" + title + "' is not found");
    }

}
