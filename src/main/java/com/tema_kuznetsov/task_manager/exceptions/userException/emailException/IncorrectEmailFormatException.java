package com.tema_kuznetsov.task_manager.exceptions.userException.emailException;

public class IncorrectEmailFormatException extends RuntimeException {
    public IncorrectEmailFormatException() {
        super("Некорректный формат email. Убедитесь, что email соответствует стандартному формату (например, example@domain.com).");
    }
}