package com.tema_kuznetsov.task_manager.exceptions.userException.performerException;

public class PerformerIdNotFoundException extends RuntimeException {
    public PerformerIdNotFoundException(Long id) {
       super("Performer not found with id: " + id);
    }
}
