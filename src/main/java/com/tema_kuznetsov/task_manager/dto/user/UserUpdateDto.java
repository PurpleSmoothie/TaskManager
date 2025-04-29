package com.tema_kuznetsov.task_manager.dto.user;

import com.tema_kuznetsov.task_manager.annotations.userAnnotations.NullableEmail;
import com.tema_kuznetsov.task_manager.annotations.userAnnotations.NullableSize;
import com.tema_kuznetsov.task_manager.annotations.userAnnotations.UniqueUserEmail;
import com.tema_kuznetsov.task_manager.annotations.userAnnotations.UniqueUserLogin;
import com.tema_kuznetsov.task_manager.models.constrains.UserConstrains;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для обновления данных пользователя.
 * Содержит информацию о новом логине, email и пароле, которые могут быть обновлены частично.
 */
@Getter
@Setter
public class UserUpdateDto {

    /**
     * Новый логин пользователя (опционально).
     * Должен содержать от минимальной до максимальной длины и быть уникальным.
     *
     * @see UserConstrains#MIN_LOGIN_LENGTH
     * @see UserConstrains#MAX_LOGIN_LENGTH
     */
    @Schema(description = "Новый логин пользователя (опционально)", example = "user123")
    @NullableSize(
            min = UserConstrains.MIN_LOGIN_LENGTH,
            max = UserConstrains.MAX_LOGIN_LENGTH,
            message = "Логин должен содержать от " + UserConstrains.MIN_LOGIN_LENGTH + " до "
                    + UserConstrains.MAX_LOGIN_LENGTH + " символов"
    )
    @UniqueUserLogin
    private String login;

    /**
     * Новый email пользователя (опционально).
     * Должен быть уникальным и иметь корректный формат.
     *
     * @see UserConstrains#MIN_EMAIL_LENGTH
     * @see UserConstrains#MAX_EMAIL_LENGTH
     */
    @Schema(description = "Новый email пользователя (опционально)", example = "user@example.com")
    @NullableEmail
    @NullableSize(
            min = UserConstrains.MIN_EMAIL_LENGTH,
            max = UserConstrains.MAX_EMAIL_LENGTH,
            message = "Email должен содержать от " + UserConstrains.MIN_EMAIL_LENGTH + " до "
                    + UserConstrains.MAX_EMAIL_LENGTH + " символов"
    )
    @UniqueUserEmail
    private String email;

    /**
     * Новый пароль пользователя (опционально).
     * Должен содержать от минимальной до максимальной длины.
     *
     * @see UserConstrains#MIN_PASSWORD_LENGTH
     * @see UserConstrains#MAX_PASSWORD_LENGTH
     */
    @Schema(description = "Новый пароль пользователя (опционально)", example = "newStrongPassword123")
    @NullableSize(
            min = UserConstrains.MIN_PASSWORD_LENGTH,
            max = UserConstrains.MAX_PASSWORD_LENGTH,
            message = "Пароль должен содержать от " + UserConstrains.MIN_PASSWORD_LENGTH + " до "
                    + UserConstrains.MAX_PASSWORD_LENGTH + " символов"
    )
    private String password;
}