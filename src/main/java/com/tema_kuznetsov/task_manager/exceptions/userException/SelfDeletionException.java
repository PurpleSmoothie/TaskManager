package com.tema_kuznetsov.task_manager.exceptions.userException;

public class SelfDeletionException extends RuntimeException {
    public SelfDeletionException() {
        super("Удаление самого себя невозможно.");
    }
}
