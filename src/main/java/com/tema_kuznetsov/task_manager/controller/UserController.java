package com.tema_kuznetsov.task_manager.controller;

import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserUpdateDto;
import com.tema_kuznetsov.task_manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @Operation(summary = "Поиск пользователя по логину (точное совпадение)", description = "Доступно только администраторам и модераторам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/search/exact")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<UserResponseDto> findUserByExactLogin(@RequestParam String login) {
        return ResponseEntity.ok(userService.findUserByExactLogin(login));
    }

    @Operation(summary = "Поиск пользователя по email", description = "Доступно администраторам и модераторам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/search/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<UserResponseDto> findUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }

    @Operation(summary = "Поиск пользователя по части логина", description = "Доступно администраторам и модераторам. Поддерживает пагинацию.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<UserResponseDto>> findUserByLoginContaining(
            @RequestParam String loginPart,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findUserByLoginContaining(loginPart, pageable));
    }

    @Operation(summary = "Получить всех пользователей", description = "Доступно администраторам и модераторам. Поддерживает пагинацию.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи получены"),
            @ApiResponse(responseCode = "404", description = "Пользователи не найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<UserResponseDto>> findAllTasks(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    @Operation(summary = "Обновить пользователя по ID", description = "Доступно админам или самому пользователю. Обновляются данные профиля.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDto updateDto
    ) {
        UserResponseDto updatedUser = userService.updateUserById(id, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Обновить пароль пользователя по ID", description = "Доступно админам или самому пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пароль обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDto> updateUserPasswordById(
            @PathVariable Long id,
            @RequestParam String password) {
        return ResponseEntity.ok(userService.updateUserPasswordById(id, password));
    }

    @Operation(summary = "Обновить роль пользователя по ID", description = "Доступно только администраторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Роль обновлена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUserRoleById(
            @PathVariable Long id,
            @RequestParam String role) {
        return ResponseEntity.ok(userService.updateUserRoleById(id, role));
    }

    @Operation(summary = "Удалить пользователя по логину", description = "Доступно только администраторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @DeleteMapping("/by-login/{login}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserByLogin(@PathVariable String login) {
        userService.deleteUserByLogin(login);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить пользователя по ID", description = "Доступно только администраторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Поиск пользователей по роли", description = "Доступно администраторам и модераторам. Поддерживает пагинацию.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен или пользователь не найден")
    })
    @GetMapping("/search/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<UserResponseDto>> findUsersByRole(
            @RequestParam String role,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findUsersByRole(role, pageable));
    }

    @Operation(summary = "Получить комментарии пользователя по ID", description = "Доступно админам, модераторам или самому пользователю. Поддерживает пагинацию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарии получены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен или комментарии для данного пользователя не найдены")
    })
    @GetMapping("{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or #id == authentication.principal.id")
    public ResponseEntity<Page<CommentResponseDto>> findCommentsByUserId(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findCommentsByUserId(id, pageable));
    }
}