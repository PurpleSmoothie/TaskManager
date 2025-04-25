package com.tema_kuznetsov.task_manager.exceptions.userException.emailException;

public class UserEmailNotFoundException extends RuntimeException {
    public UserEmailNotFoundException(String email) {
        super("Пользователь с email '" + email + "' не найден");
    }
}