package com.tema_kuznetsov.task_manager.exceptions.userException;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("Password must contain at least one letter and be between 8 and 100 characters");
    }
}
