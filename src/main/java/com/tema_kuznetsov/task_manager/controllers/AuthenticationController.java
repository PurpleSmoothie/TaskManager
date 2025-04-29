package com.tema_kuznetsov.task_manager.controllers;

import com.tema_kuznetsov.task_manager.util.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.tema_kuznetsov.task_manager.dto.security.JwtRequestDto;
import com.tema_kuznetsov.task_manager.dto.security.JwtResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserCreateDto;
import com.tema_kuznetsov.task_manager.dto.user.UserResponseDto;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.security.jwt.JwtService;
import com.tema_kuznetsov.task_manager.services.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Map;

/**
 * Контроллер для аутентификации и регистрации пользователей.
 * Предоставляет операции для авторизации пользователя (вход в систему),
 * валидации JWT токена и регистрации нового пользователя.
 * Все операции защищены авторизацией через JWT и разграничением доступа по ролям.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Аутентификация и регистрация пользователей")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    /**
     * Авторизация пользователя с получением JWT токена.
     * Проводит аутентификацию пользователя по email и паролю и возвращает JWT токен.
     *
     * @param loginRequest запрос с email и паролем пользователя.
     * @return JWT токен в случае успешной авторизации, ошибка 401 в случае неверного логина или пароля.
     */
    @Operation(summary = "Авторизация пользователя",
            description = "Позволяет войти в систему и получить JWT токен по email и паролю")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Успешная авторизация, возвращается JWT токен"),
            @ApiResponse(responseCode = "401", description = "Неверный логин или пароль")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequestDto loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            UserDetails user = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            String token = jwtService.generateToken(user.getUsername());

            return ResponseEntity.ok(new JwtResponseDto(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", 401,
                            "error", "Unauthorized",
                            "message", "Неверный логин или пароль",
                            "path", "/api/auth/login"
                    ));
        }
    }

    /**
     * Валидация переданного JWT токена.
     * Проверяет, является ли токен валидным и не истёкшим.
     *
     * @param token JWT токен, передаваемый в заголовке авторизации.
     * @param request HttpServletRequest для получения URI запроса.
     * @return 200 OK если токен валиден, 401 UNAUTHORIZED если токен невалиден.
     */
    @GetMapping("/validate")
    @Operation(summary = "Валидация JWT токена", description = "Проверка действительности переданного JWT токена")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Токен валиден"),
            @ApiResponse(responseCode = "401", description = "Невалидный или отсутствующий JWT токен"),
    })
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String token,
                                           HttpServletRequest request) {
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ErrorResponse(
                            HttpStatus.UNAUTHORIZED,
                            "Невалидный или отсутствующий JWT токен",
                            request.getRequestURI()
                    )
            );
        }

        token = token.substring(7);
        String email = jwtService.extractUsername(token);

        if (!jwtService.isTokenValid(token, email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ErrorResponse(
                            HttpStatus.UNAUTHORIZED,
                            "Невалидный или отсутствующий JWT токен",
                            request.getRequestURI()
                    )
            );
        }

        return ResponseEntity.ok(true);
    }

    /**
     * Регистрация нового пользователя с ролью USER.
     * Принимает данные пользователя, создает запись в базе и возвращает созданного пользователя.
     *
     * @param dto данные для регистрации пользователя (email, пароль и др.).
     * @return Ответ с данными созданного пользователя и URI для получения его информации.
     */
    @PostMapping("/register")
    @Operation(summary = "Регистрация нового пользователя", description = "Создание нового пользователя с ролью USER")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные регистрации"),
    })
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        AppUser createdUser = userDetailsService.createUser(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity.created(location).body(new UserResponseDto(createdUser));
    }
}