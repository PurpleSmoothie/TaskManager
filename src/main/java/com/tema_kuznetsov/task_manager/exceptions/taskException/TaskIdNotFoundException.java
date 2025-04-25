package com.tema_kuznetsov.task_manager.exceptions.taskException;

public class TaskIdNotFoundException extends RuntimeException {
    public TaskIdNotFoundException(Long id) {
        super("Задача с id " + id + " не найдена");
    }
}