package com.tema_kuznetsov.task_manager.util;

import com.tema_kuznetsov.task_manager.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Утилитарный класс для получения информации о текущем пользователе.
 */
public class SecurityUtils {

    /**
     * Получает идентификатор текущего пользователя из контекста безопасности.
     *
     * @return идентификатор текущего пользователя
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }
}