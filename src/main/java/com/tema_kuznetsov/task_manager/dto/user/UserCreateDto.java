package com.tema_kuznetsov.task_manager.dto.user;

import com.tema_kuznetsov.task_manager.annotations.userAnnotations.UniqueUserEmail;
import com.tema_kuznetsov.task_manager.annotations.userAnnotations.UniqueUserLogin;
import com.tema_kuznetsov.task_manager.models.constrains.UserConstrains;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
public class UserCreateDto {

    @Schema(description = "Логин пользователя", example = "newuser123", required = true)
    @NotBlank(message = "Логин обязателен")
    @Size(
            min = UserConstrains.MIN_LOGIN_LENGTH,
            max = UserConstrains.MAX_LOGIN_LENGTH,
            message = "Логин должен содержать от " + UserConstrains.MIN_LOGIN_LENGTH +
                    " до " + UserConstrains.MAX_LOGIN_LENGTH + " символов"
    )
    @UniqueUserLogin
    private String login;

    @Schema(description = "Email пользователя", example = "newuser@example.com", required = true)
    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email обязателен")
    @Size(
            min = UserConstrains.MIN_EMAIL_LENGTH,
            max = UserConstrains.MAX_EMAIL_LENGTH,
            message = "Email должен содержать от " + UserConstrains.MIN_EMAIL_LENGTH +
                    " до " + UserConstrains.MAX_EMAIL_LENGTH + " символов"
    )
    @UniqueUserEmail
    private String email;

    @Schema(description = "Пароль пользователя", example = "strongPass123", required = true)
    @NotBlank(message = "Пароль обязателен")
    @Size(
            min = UserConstrains.MIN_PASSWORD_LENGTH,
            max = UserConstrains.MAX_PASSWORD_LENGTH,
            message = "Пароль должен содержать от " + UserConstrains.MIN_PASSWORD_LENGTH +
                    " до " + UserConstrains.MAX_PASSWORD_LENGTH + " символов"
    )
    private String password;
}
