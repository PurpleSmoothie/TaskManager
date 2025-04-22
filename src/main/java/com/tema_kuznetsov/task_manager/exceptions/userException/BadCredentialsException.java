package com.tema_kuznetsov.task_manager.exceptions.userException;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException() {
        super("Invalid email or password");
    }
}
