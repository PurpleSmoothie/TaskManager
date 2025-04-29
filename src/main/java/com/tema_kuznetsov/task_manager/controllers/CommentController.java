package com.tema_kuznetsov.task_manager.controllers;

import com.tema_kuznetsov.task_manager.dto.comment.CommentCreateDto;
import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.comment.CommentUpdateDto;
import com.tema_kuznetsov.task_manager.services.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Контроллер для управления комментариями.
 * Предоставляет CRUD-операции для создания, получения, обновления и удаления комментариев.
 * Все операции защищены авторизацией через JWT и разграничением доступа по ролям.
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Управление комментариями")
public class CommentController {

    private final CommentService commentService;

    /**
     * Создание нового комментария.
     * Доступно для ролей: ADMIN, USER, MODERATOR.
     *
     * @param dto данные для создания комментария.
     * @return созданный комментарий с HTTP статусом 201.
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(summary = "Создать комментарий", description = "Доступно для ролей: ADMIN, USER, MODERATOR")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Комментарий успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    public ResponseEntity<CommentResponseDto> createComment(@Valid @RequestBody CommentCreateDto dto) {
        CommentResponseDto createdComment = commentService.createComment(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdComment.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdComment);
    }

    /**
     * Получение комментария по ID.
     * Доступно для ролей ADMIN, USER, MODERATOR.
     *
     * @param id идентификатор комментария.
     * @return найденный комментарий с HTTP статусом 200.
     */
    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(summary = "Получить комментарий по ID", description = "Доступно для ролей ADMIN, USER, MODERATOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий найден"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    public ResponseEntity<CommentResponseDto> findCommentById(
            @PathVariable @Min(value = 1, message = "ID должен быть положительным") Long id) {
        return ResponseEntity.ok(commentService.findCommentById(id));
    }

    /**
     * Удаление комментария по ID.
     * Доступно владельцу комментария или ADMIN.
     *
     * @param id идентификатор комментария для удаления.
     * @return HTTP статус 204 (No Content) при успешном удалении.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @commentOwnerValidator.isCommentOwner(#id, authentication)")
    @Operation(summary = "Удалить комментарий по ID", description = "Доступно владельцу комментария или ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Комментарий удален"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "400", description = "Неверный ID")
    })
    public ResponseEntity<Void> deleteCommentById(
            @PathVariable @Min(value = 1, message = "ID должен быть положительным") Long id) {
        commentService.deleteCommentById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновление комментария по ID.
     * Доступно владельцу комментария или ADMIN.
     *
     * @param id идентификатор комментария.
     * @param commentUpdateDto данные для обновления комментария.
     * @return обновленный комментарий с HTTP статусом 200.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @commentOwnerValidator.isCommentOwner(#id, authentication)")
    @Operation(summary = "Обновить комментарий", description = "Доступно владельцу комментария или ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий обновлен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "400", description = "Неверный ID или тело запроса")
    })
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable @Min(value = 1, message = "ID должен быть положительным") Long id,
            @RequestBody @Valid CommentUpdateDto commentUpdateDto
    ) {
        CommentResponseDto updatedComment = commentService.updateCommentById(id, commentUpdateDto);
        return ResponseEntity.ok(updatedComment);
    }
}