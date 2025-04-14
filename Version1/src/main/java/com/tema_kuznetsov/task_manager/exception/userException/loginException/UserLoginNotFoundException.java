package com.tema_kuznetsov.task_manager.exception.userException.loginException;

public class UserLoginNotFoundException extends RuntimeException {
    public UserLoginNotFoundException(String login) {
        super("User not found with login: " + login);
    }
}
