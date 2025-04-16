package com.tema_kuznetsov.task_manager.security.auth;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}