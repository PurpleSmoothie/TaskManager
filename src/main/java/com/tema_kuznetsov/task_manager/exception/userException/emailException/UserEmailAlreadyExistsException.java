package com.tema_kuznetsov.task_manager.exception.userException.emailException;

public class UserEmailAlreadyExistsException extends RuntimeException {
    public UserEmailAlreadyExistsException(String email) {
        super("User with email '" + email + "' already exists");
    }
}
