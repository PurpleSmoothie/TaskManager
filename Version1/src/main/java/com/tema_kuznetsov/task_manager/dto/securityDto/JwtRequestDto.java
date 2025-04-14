package com.tema_kuznetsov.task_manager.dto.securityDto;

import lombok.Data;

@Data
public class JwtRequestDto {
    private String username;
    private String password;
}
