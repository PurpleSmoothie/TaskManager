package com.tema_kuznetsov.task_manager.exceptions.userException.emailException;

public class IncorrectEmailFormatException extends RuntimeException {
    public IncorrectEmailFormatException() {
        super("Invalid email format. Please ensure the email follows the standard format (e.g., example@domain.com).");
    }
}
