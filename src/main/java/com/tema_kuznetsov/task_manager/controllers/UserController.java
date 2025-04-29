package com.tema_kuznetsov.task_manager.controllers;

import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserUpdateDto;
import com.tema_kuznetsov.task_manager.models.constrains.UserConstrains;
import com.tema_kuznetsov.task_manager.security.CustomUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления пользователями.
 * Предоставляет CRUD-операции для получения, обновления и удаления пользователей.
 * Поддерживает поиск пользователей по различным критериям и управление ролями.
 * Все операции защищены авторизацией через JWT и разграничением доступа по ролям.
 */
@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Управление пользователями")
public class UserController {
    private final UserService userService;

    /**
     * Получение информации о текущем авторизованном пользователе.
     * Доступно всем авторизованным пользователям.
     *
     * @param customUserDetails данные аутентифицированного пользователя
     * @return информация о пользователе с HTTP статусом 200
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить информацию о себе", description = "Доступно всем авторизованным пользователям")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Информация о пользователе получена"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    public ResponseEntity<UserResponseDto> getMe(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(userService.findUserById(customUserDetails.getId()));
    }

    /**
     * Получение пользователя по ID.
     * Доступно для ролей ADMIN и MODERATOR.
     *
     * @param id идентификатор пользователя
     * @return информация о пользователе с HTTP статусом 200
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Получить пользователя по ID", description = "Доступно администраторам и модераторам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    public ResponseEntity<UserResponseDto> findUserById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    /**
     * Поиск пользователя по точному логину.
     * Доступно для ролей ADMIN и MODERATOR.
     *
     * @param login точный логин пользователя
     * @return информация о пользователе с HTTP статусом 200
     */
    @GetMapping("/search/exact")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Поиск пользователя по логину (точное совпадение)",
            description = "Доступно только администраторам и модераторам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат логина)"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
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

    /**
     * Поиск пользователя по email.
     * Доступно для ролей ADMIN и MODERATOR.
     *
     * @param email email пользователя
     * @return информация о пользователе с HTTP статусом 200
     */
    @GetMapping("/search/email")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Поиск пользователя по email", description = "Доступно администраторам и модераторам")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат email")
    })
    public ResponseEntity<UserResponseDto> findUserByEmail(
            @RequestParam
            @NotBlank(message = "Email не должен быть пустым")
            @Email(message = "Некорректный формат email") String email) {
        return ResponseEntity.ok(userService.findUserByEmail(email));
    }

    /**
     * Поиск пользователей по части логина с пагинацией.
     * Доступно для ролей ADMIN и MODERATOR.
     *
     * @param loginPart часть логина для поиска
     * @param pageable параметры пагинации и сортировки
     * @return страница найденных пользователей с HTTP статусом 200
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Поиск пользователя по части логина",
            description = "Доступно администраторам и модераторам. Поддерживает пагинацию.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Неверный формат логина)"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен")
    })
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

    /**
     * Получение всех пользователей с пагинацией.
     * Доступно для ролей ADMIN и MODERATOR.
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница всех пользователей с HTTP статусом 200
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Получить всех пользователей",
            description = "Доступно администраторам и модераторам. Поддерживает пагинацию.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи получены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен")
    })
    public ResponseEntity<Page<UserResponseDto>> findAllUsers(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findAllUsers(pageable));
    }

    /**
     * Обновление информации о пользователе по ID.
     * Доступно для ролей ADMIN и MODERATOR.
     *
     * @param id идентификатор пользователя
     * @param updateDto данные для обновления
     * @return обновленная информация о пользователе с HTTP статусом 200
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Обновить пользователя по ID",
            description = "Доступно администраторам и модераторам. " +
                    "Позволяет изменить логин, email, имя или описание.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Неверные данные или email/логин уже существует"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    public ResponseEntity<UserResponseDto> updateUserById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id,
            @Valid @RequestBody UserUpdateDto updateDto
    ) {
        UserResponseDto updatedUser = userService.updateUserById(id, updateDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Обновление пароля пользователя.
     * Доступно ADMIN или самому пользователю.
     *
     * @param id идентификатор пользователя
     * @param password новый пароль
     * @return информация о пользователе с HTTP статусом 200
     */
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
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

    /**
     * Обновление роли пользователя.
     * Доступно только для ADMIN.
     *
     * @param id идентификатор пользователя
     * @param role новая роль
     * @return информация о пользователе с HTTP статусом 200
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<UserResponseDto> updateUserRoleById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id,
            @Parameter(description = "Роль пользователя", schema = @Schema(allowableValues = {"ADMIN", "USER", "MODERATOR"}))
            @Pattern(regexp = "ADMIN|USER|MODERATOR", message = "Роль должна быть одной из: ADMIN, USER, MODERATOR")
            @RequestParam String role) {
        return ResponseEntity.ok(userService.updateUserRoleById(id, role));
    }

    /**
     * Удаление пользователя по логину.
     * Доступно только для ADMIN.
     *
     * @param login логин пользователя
     * @return HTTP статус 204 (No Content) при успешном удалении
     */
    @DeleteMapping("/by-login/{login}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить пользователя по логину", description = "Доступно только администраторам.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат логина или попытка удалить самого себя"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
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

    /**
     * Удаление пользователя по ID.
     * Доступно только для ADMIN.
     *
     * @param id идентификатор пользователя
     * @return HTTP статус 204 (No Content) при успешном удалении
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<Void> deleteUserById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Поиск пользователей по роли с пагинацией.
     * Доступно для ролей ADMIN и MODERATOR.
     *
     * @param role роль для поиска
     * @param pageable параметры пагинации и сортировки
     * @return страница найденных пользователей с HTTP статусом 200
     */
    @GetMapping("/search/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(summary = "Поиск пользователей по роли",
            description = "Доступно администраторам и модераторам. Поддерживает пагинацию.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователи найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "400", description = "Некорректный формат роли"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    public ResponseEntity<Page<UserResponseDto>> findUsersByRole(
            @Parameter(description = "Роль пользователя", schema = @Schema(allowableValues = {"ADMIN", "USER", "MODERATOR"}))
            @Pattern(regexp = "ADMIN|USER|MODERATOR", message = "Роль должна быть одной из: ADMIN, USER, MODERATOR")
            @RequestParam String role,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findUsersByRole(role, pageable));
    }

    /**
     * Получение комментариев пользователя с пагинацией.
     * Доступно ADMIN, MODERATOR или самому пользователю.
     *
     * @param id идентификатор пользователя
     * @param pageable параметры пагинации и сортировки
     * @return страница комментариев с HTTP статусом 200
     */
    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or #id == authentication.principal.id")
    @Operation(summary = "Получить комментарии пользователя по ID",
            description = "Доступно админам, модераторам или самому пользователю. Поддерживает пагинацию")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарии получены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "JWT токен отсутствует или некорректен")
    })
    public ResponseEntity<Page<CommentResponseDto>> findCommentsByUserId(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.findCommentsByUserId(id, pageable));
    }
}