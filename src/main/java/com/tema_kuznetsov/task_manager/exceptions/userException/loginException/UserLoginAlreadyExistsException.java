package com.tema_kuznetsov.task_manager.exceptions.userException.loginException;

public class UserLoginAlreadyExistsException extends RuntimeException {
    public UserLoginAlreadyExistsException(String login) {
        super("User with title '" + login + "' already exists");
    }
}
