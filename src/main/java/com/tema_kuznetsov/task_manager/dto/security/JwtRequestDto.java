package com.tema_kuznetsov.task_manager.dto.security;

import lombok.Data;

@Data
public class JwtRequestDto {
    private String email;
    private String password;
}
