package com.tema_kuznetsov.task_manager.dto.user;

import com.tema_kuznetsov.task_manager.models.AppUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * DTO для ответа при получении информации о пользователе.
 * Содержит данные о логине, email, роли и дате регистрации пользователя.
 */
@Getter
@Setter
public class UserResponseDto {

    /**
     * Уникальный логин пользователя.
     */
    @Schema(description = "Уникальный логин пользователя", example = "tema_kuz")
    private String login;

    /**
     * Email пользователя.
     */
    @Schema(description = "Email пользователя", example = "tema@example.com")
    private String email;

    /**
     * ID пользователя.
     */
    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    /**
     * Роль пользователя.
     */
    @Schema(description = "Роль пользователя", example = "USER")
    private String role;

    /**
     * Дата регистрации пользователя.
     */
    @Schema(description = "Дата регистрации пользователя", example = "2024-04-17T10:15:30")
    private LocalDateTime createdAt;

    /**
     * Конструктор для создания DTO на основе пользователя.
     *
     * @param user объект пользователя
     */
    public UserResponseDto(AppUser user) {
        this.login = user.getLogin();
        this.email = user.getEmail();
        this.id = user.getId();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}