package com.tema_kuznetsov.task_manager.exceptions.userException;

public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException(Long id) {
        super("User not found with id: " + id);
    }
}
