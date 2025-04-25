package com.tema_kuznetsov.task_manager.controllers;

import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserUpdateDto;
import com.tema_kuznetsov.task_manager.models.constrains.UserConstrains;
import com.tema_kuznetsov.task_manager.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Управление пользователями")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Получить пользователя по ID", description = "Доступно администраторам и модераторам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<UserResponseDto> findUserById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @Operation(summary = "Поиск пользователя по логину (точное совпадение)", description = "Доступно только администраторам и модераторам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат логина)"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    @GetMapping("/search/exact")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<UserResponseDto> findUserByExactLogin(
            @RequestParam
            @NotBlank(message = "Логин обязателен")
            @Size(
                    min = UserConstrains.MIN_LOGIN_LENGTH,
                    max = UserConstrains.MAX_LOGIN_LENGTH,
                    message = "Логин должен содержать от " + UserConstrains.MIN_LOGIN_LENGTH +
                            " до " + UserConstrains.MAX_LOGIN_LENGTH + " символов"
            )
            String login) {
        return ResponseEntity.ok(userService.findUserByExactLogin(login));
    }

    @Operation(summary = "Поиск пользователя по email", description = "Доступно администраторам и модераторам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат email")
    })

    @GetMapping("/search/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<UserResponseDto> findUserByEmail(
            @RequestParam
            @NotBlank(message = "Email не должен быть пустым")
            @Email (message = "Некорректный формат email")String email) {
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }

    @Operation(summary = "Поиск пользователя по части логина", description = "Доступно администраторам и модераторам. Поддерживает пагинацию.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат логина)"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен")
    })
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<UserResponseDto>> findUserByLoginContaining(
            @NotBlank(message = "Логин обязателен")
            @Size(
                    max = UserConstrains.MAX_LOGIN_LENGTH,
                    message = "Логин должен содержать до "+ UserConstrains.MAX_LOGIN_LENGTH + " символов"
            )
            @RequestParam String loginPart,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findUserByLoginContaining(loginPart, pageable));
    }

    @Operation(summary = "Получить всех пользователей", description = "Доступно администраторам и модераторам. " +
            "Поддерживает пагинацию.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи получены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<UserResponseDto>> findAllUsers(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    @Operation(summary = "Обновить пользователя по ID", description = "Доступно администраторам и модераторам. " +
            "Позволяет изменить логин, email, имя или описание.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные или email/логин уже существует"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<UserResponseDto> updateUserById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id,
            @Valid @RequestBody UserUpdateDto updateDto
    ) {
        UserResponseDto updatedUser = userService.updateUserById(id, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Обновить пароль пользователя по ID",
            description = "Доступно администраторам или самому пользователю. Позволяет изменить только пароль.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат пароля"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDto> updateUserPasswordById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id,
            @NotBlank(message = "Пароль обязателен")
            @Size(
                    min = UserConstrains.MIN_PASSWORD_LENGTH,
                    max = UserConstrains.MAX_PASSWORD_LENGTH,
                    message = "Пароль должен содержать от " + UserConstrains.MIN_PASSWORD_LENGTH +
                            " до " + UserConstrains.MAX_PASSWORD_LENGTH + " символов"
            )
            @RequestParam String password) {
        return ResponseEntity.ok(userService.updateUserPasswordById(id, password));
    }

    @Operation(summary = "Обновить роль пользователя по ID", description = "Доступно только администраторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Роль обновлена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат идентификатора " +
                    "или попытка изменить роль самому себе"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUserRoleById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id,
            @Parameter(description = "Роль пользователя", schema = @Schema(allowableValues = {"ADMIN", "USER", "MODERATOR"}))
            @Pattern(regexp = "ADMIN|USER|MODERATOR", message = "Роль должна быть одной из: ADMIN, USER, MODERATOR")
            @RequestParam String role) {
        return ResponseEntity.ok(userService.updateUserRoleById(id, role));
    }

    @Operation(summary = "Удалить пользователя по логину", description = "Доступно только администраторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат логина или попытка удалить самого себя"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    @DeleteMapping("/by-login/{login}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserByLogin(
            @PathVariable
            @Size(
            min = UserConstrains.MIN_LOGIN_LENGTH,
            max = UserConstrains.MAX_LOGIN_LENGTH,
            message = "Логин должен содержать от " + UserConstrains.MIN_LOGIN_LENGTH +
                    " до " + UserConstrains.MAX_LOGIN_LENGTH + " символов"
            ) String login) {
        userService.deleteUserByLogin(login);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить пользователя по ID", description = "Доступно только администраторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат идентификатора " +
                    "или попытка удалить самого себя"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск пользователей по роли", description = "Доступно администраторам и модераторам. " +
            "Поддерживает пагинацию.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат роли"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    @GetMapping("/search/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<UserResponseDto>> findUsersByRole(
            @Parameter(description = "Роль пользователя", schema = @Schema(allowableValues = {"ADMIN", "USER", "MODERATOR"}))
            @Pattern(regexp = "ADMIN|USER|MODERATOR", message = "Роль должна быть одной из: ADMIN, USER, MODERATOR")
            @RequestParam String role,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findUsersByRole(role, pageable));
    }

    @Operation(summary = "Получить комментарии пользователя по ID",
            description = "Доступно админам, модераторам или самому пользователю." +
            " Поддерживает пагинацию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарии получены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "JWT токен отсутствует или некорректен")
    })
    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or #id == authentication.principal.id")
    public ResponseEntity<Page<CommentResponseDto>> findCommentsByUserId(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findCommentsByUserId(id, pageable));
    }


}