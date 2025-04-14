package com.tema_kuznetsov.task_manager.exception.userException.roleException;

import com.tema_kuznetsov.task_manager.model.enums.TaskPriority;
import com.tema_kuznetsov.task_manager.model.enums.UserRole;

public class IncorrectRoleTitleException extends RuntimeException {
    public IncorrectRoleTitleException(String role) {
        super("Invalid role: '" + role + "'. Allowed values: " + String.join(", ", UserRole.VALID_ROLES));
    }
}