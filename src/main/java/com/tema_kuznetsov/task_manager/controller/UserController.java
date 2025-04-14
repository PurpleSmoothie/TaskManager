package com.tema_kuznetsov.task_manager.controller;

import com.tema_kuznetsov.task_manager.dto.commentDto.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.userDto.UserCreateDto;
import com.tema_kuznetsov.task_manager.dto.userDto.UserResponseDto;
import com.tema_kuznetsov.task_manager.dto.userDto.UserUpdateDto;
import com.tema_kuznetsov.task_manager.model.App_user;
import com.tema_kuznetsov.task_manager.service.UserService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 1. Создать пользователя c указанием роли
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        App_user createdUser = userService.createUser(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.getId())
                .toUri();
        return ResponseEntity.ok(new UserResponseDto(createdUser));
    }

    // 2. Получить пользователя по ID
    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<UserResponseDto> findUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    // 3. Поиск по точному названию
    @GetMapping("/search/exact")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<UserResponseDto> findUserByExactLogin(@RequestParam String login) {
        return ResponseEntity.ok(userService.findUserByExactLogin(login));
    }

    // 4. Поиск по имейлу
    @GetMapping("/search/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<UserResponseDto> findUserByEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }

    // 5. Поиск по кусочку названия
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<UserResponseDto>> findUserByLoginContaining(
            @RequestParam String loginPart,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findUserByLoginContaining(loginPart,pageable));
    }

    // 6. Получить всех пользователей
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<UserResponseDto>> findAllTasks(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    // 7. Обновление пользователя по айди
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateDto updateDto
    ) {
        UserResponseDto updatedUser = userService.updateUserById(id, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    // 8. Обновление пароля пользователя по айди
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserResponseDto> updateUserPasswordById(
            @PathVariable Long id,
            @RequestParam String password) {
        return ResponseEntity.ok(userService.updateUserPasswordById(id, password));
    }

    // 9. Обновление роли пользователя по айди
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> updateUserRoleById(
            @PathVariable Long id,
            @RequestParam String role) {
        return ResponseEntity.ok(userService.updateUserRoleById(id, role));
    }

    // 10. Удаление пользователя по имени
    @DeleteMapping("/by-login/{login}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserByLogin(
            @PathVariable String login) {
        userService.deleteUserByLogin(login);
        return ResponseEntity.noContent().build();
    }

    // 11. Удаление пользователя по айди
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    // 12. Поиск пользователей по роли
    @GetMapping("/search/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<UserResponseDto>> findUsersByRole(
            @RequestParam String role,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findUsersByRole(role,pageable));
    }

    // 13. Вывод всех комментов пользователя по айди
    @GetMapping("{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or #id == authentication.principal.id")
    public ResponseEntity<Page<CommentResponseDto>> findCommentsByUserId(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findCommentsByUserId(id, pageable));
    }
}