package com.tema_kuznetsov.task_manager.exception.taskException.titleException;

public class TaskTitleAlreadyExistsException extends RuntimeException {
    public TaskTitleAlreadyExistsException(String title) {
        super("Task with title '" + title + "' already exists");
}
}
