package com.tema_kuznetsov.task_manager.exceptions.userException.ownerException;

public class OwnerIdNotFoundException extends RuntimeException {
    public OwnerIdNotFoundException(Long id) {
        super("Владелец с id " + id + " не найден");
    }
}