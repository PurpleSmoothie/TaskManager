package com.tema_kuznetsov.task_manager.models.enums;

import java.util.Set;

public class UserRole {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";
    public static final String MODERATOR = "MODERATOR";

    public static boolean isValid(String status) {
        return status != null && (
                status.equals(ADMIN) ||
                        status.equals(USER) ||
                        status.equals(MODERATOR)
        );
    }

    public static final Set<String> VALID_ROLES = Set.of(ADMIN, USER, MODERATOR);
}
