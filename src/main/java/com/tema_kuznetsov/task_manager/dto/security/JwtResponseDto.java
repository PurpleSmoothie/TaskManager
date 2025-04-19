package com.tema_kuznetsov.task_manager.dto.security;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDto {

    @Schema(description = "JWT токен для доступа к API", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String token;
}