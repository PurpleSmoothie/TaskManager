package com.tema_kuznetsov.task_manager.models.enums;

import java.util.Set;

/**
 * Класс, содержащий возможные роли пользователей в системе.
 * Предоставляет константы для различных ролей и метод для проверки их допустимости.
 */
public class UserRole {

    /**
     * Роль администратора.
     */
    public static final String ADMIN = "ADMIN";

    /**
     * Роль обычного пользователя.
     */
    public static final String USER = "USER";

    /**
     * Роль модератора.
     */
    public static final String MODERATOR = "MODERATOR";

    /**
     * Проверяет, является ли переданный статус допустимой ролью пользователя.
     *
     * @param status Роль пользователя для проверки.
     * @return true, если роль является допустимой, иначе false.
     */
    public static boolean isValid(String status) {
        return status != null && (
                status.equals(ADMIN) ||
                        status.equals(USER) ||
                        status.equals(MODERATOR)
        );
    }

    /**
     * Множество допустимых значений ролей пользователей.
     */
    public static final Set<String> VALID_ROLES = Set.of(ADMIN, USER, MODERATOR);
}