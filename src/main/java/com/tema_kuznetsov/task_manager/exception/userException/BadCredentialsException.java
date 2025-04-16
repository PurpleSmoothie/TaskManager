package com.tema_kuznetsov.task_manager.exception.userException;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException() {
        super("Invalid email or password");
    }
}
