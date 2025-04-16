package com.tema_kuznetsov.task_manager.dto.user;

import com.tema_kuznetsov.task_manager.model.AppUser;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDto {
    private String login;
    private String email;
    private Long id;
    private String role;
    private LocalDateTime createdAt;

    public UserResponseDto(AppUser user) {
        this.login = user.getLogin();
        this.email = user.getEmail();
        this.id = user.getId();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}
