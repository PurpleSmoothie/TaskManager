package com.tema_kuznetsov.task_manager.exceptions.userException.emailException;

public class UserEmailAlreadyExistsException extends RuntimeException {
    public UserEmailAlreadyExistsException(String email) {
        super("Пользователь с email '" + email + "' уже существует");
    }
}