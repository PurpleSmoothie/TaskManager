package com.tema_kuznetsov.task_manager.exceptions.userException.roleException;

public class SelfRoleChangeException extends RuntimeException {
    public SelfRoleChangeException() {
        super("Невозможно изменить собственную роль");
    }
}
