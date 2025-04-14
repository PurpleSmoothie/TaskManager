package com.tema_kuznetsov.task_manager.exception.userException.ownerException;

public class OwnerIdNotFoundException extends RuntimeException {
    public OwnerIdNotFoundException(Long id) {
        super("Owner not found with id: " + id);
    }
}
