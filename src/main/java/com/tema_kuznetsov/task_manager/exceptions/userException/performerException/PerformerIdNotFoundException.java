package com.tema_kuznetsov.task_manager.exceptions.userException.performerException;

public class PerformerIdNotFoundException extends RuntimeException {
    public PerformerIdNotFoundException(Long id) {
        super("Исполнитель с id " + id + " не найден");
    }
}