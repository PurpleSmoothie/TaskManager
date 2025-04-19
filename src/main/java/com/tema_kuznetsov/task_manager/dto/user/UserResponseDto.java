package com.tema_kuznetsov.task_manager.dto.user;

import com.tema_kuznetsov.task_manager.model.AppUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDto {

    @Schema(description = "Уникальный логин пользователя", example = "tema_kuz")
    private String login;

    @Schema(description = "Email пользователя", example = "tema@example.com")
    private String email;

    @Schema(description = "ID пользователя", example = "1")
    private Long id;

    @Schema(description = "Роль пользователя", example = "USER")
    private String role;

    @Schema(description = "Дата регистрации пользователя", example = "2024-04-17T10:15:30")
    private LocalDateTime createdAt;

    public UserResponseDto(AppUser user) {
        this.login = user.getLogin();
        this.email = user.getEmail();
        this.id = user.getId();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}