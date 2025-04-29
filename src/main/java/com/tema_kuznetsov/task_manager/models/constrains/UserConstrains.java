package com.tema_kuznetsov.task_manager.models.constrains;

import lombok.Getter;

/**
 * Класс, содержащий ограничения для пользователей.
 * Включает максимальные и минимальные длины для логина, пароля и email.
 * Этот класс не предназначен для создания экземпляров.
 */
@Getter
public final class UserConstrains {

    /**
     * Максимальная длина логина пользователя.
     */
    public static final int MAX_LOGIN_LENGTH = 30;

    /**
     * Максимальная длина пароля пользователя.
     */
    public static final int MAX_PASSWORD_LENGTH = 100;

    /**
     * Максимальная длина email пользователя.
     */
    public static final int MAX_EMAIL_LENGTH = 100;

    /**
     * Минимальная длина логина пользователя.
     */
    public static final int MIN_LOGIN_LENGTH = 4;

    /**
     * Минимальная длина пароля пользователя.
     */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Минимальная длина email пользователя.
     */
    public static final int MIN_EMAIL_LENGTH = 5;

    /**
     * Приватный конструктор для предотвращения создания экземпляров класса.
     */
    private UserConstrains() {}
}
