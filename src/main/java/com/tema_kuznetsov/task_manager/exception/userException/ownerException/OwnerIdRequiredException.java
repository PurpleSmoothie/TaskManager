package com.tema_kuznetsov.task_manager.exception.userException.ownerException;

public class OwnerIdRequiredException extends RuntimeException {
    public OwnerIdRequiredException() {
        super("Owner_id1 is required");
    }
}
