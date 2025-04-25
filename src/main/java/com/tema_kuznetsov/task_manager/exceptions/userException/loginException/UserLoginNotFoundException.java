package com.tema_kuznetsov.task_manager.exceptions.userException.loginException;

public class UserLoginNotFoundException extends RuntimeException {
    public UserLoginNotFoundException(String login) {
        super("Пользователь с логином '" + login + "' не найден");
    }
}