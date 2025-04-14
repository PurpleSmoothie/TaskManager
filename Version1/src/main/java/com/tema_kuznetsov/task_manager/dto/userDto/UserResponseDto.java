package com.tema_kuznetsov.task_manager.dto.userDto;

import com.tema_kuznetsov.task_manager.model.App_user;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponseDto {
    private String login;
    private String password;
    private String email;
    private Long id;
    private String role;
    private LocalDateTime createdAt;

    public UserResponseDto(App_user user) {
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.id = user.getId();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
    }
}
