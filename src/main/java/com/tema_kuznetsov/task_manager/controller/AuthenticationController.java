package com.tema_kuznetsov.task_manager.controller;

import com.tema_kuznetsov.task_manager.dto.security.JwtRequestDto;
import com.tema_kuznetsov.task_manager.dto.security.JwtResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserCreateDto;
import com.tema_kuznetsov.task_manager.dto.user.UserResponseDto;
import com.tema_kuznetsov.task_manager.exception.userException.BadCredentialsException;
import com.tema_kuznetsov.task_manager.model.AppUser;
import com.tema_kuznetsov.task_manager.security.jwt.JwtService;
import com.tema_kuznetsov.task_manager.service.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Аутентификация и регистрация пользователей")
public class AuthenticationController {

    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Вход пользователя", description = "Позволяет пользователю войти и получить JWT токен")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "JWT токен успешно получен"),
            @ApiResponse(responseCode = "400", description = "Неверные учетные данные"),
    })
    public ResponseEntity<JwtResponseDto> login(@RequestBody JwtRequestDto requestDto) {
        String email = requestDto.getEmail();
        String password = requestDto.getPassword();

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException();
        }

        String token = jwtService.generateToken(email);
        return ResponseEntity.ok(new JwtResponseDto(token));
    }

    @GetMapping("/validate")
    @Operation(summary = "Валидация JWT токена", description = "Проверка действительности переданного JWT токена")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токен валиден"),
            @ApiResponse(responseCode = "400", description = "Неверный формат токена")
    })
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtService.extractUsername(token);
        boolean isValid = jwtService.isTokenValid(token, email);

        return ResponseEntity.ok(isValid);
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", description = "Создание нового пользователя с указанием роли")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные регистрации"),
    })
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        AppUser createdUser = userDetailsService.createUser(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity.ok(new UserResponseDto(createdUser));
    }
}