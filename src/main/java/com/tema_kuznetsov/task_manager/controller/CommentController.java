package com.tema_kuznetsov.task_manager.controller;

import com.tema_kuznetsov.task_manager.dto.comment.CommentCreateDto;
import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.comment.CommentUpdateDto;
import com.tema_kuznetsov.task_manager.model.Comment;
import com.tema_kuznetsov.task_manager.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Управление комментариями")
public class CommentController {
    private final CommentService commentService;

    // 1. Создание комментария
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(summary = "Создать комментарий", description = "Доступно для ролей: ADMIN, USER, MODERATOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса")
    })
    public ResponseEntity<CommentResponseDto> createUser(@Valid @RequestBody CommentCreateDto dto) {
        Comment createdComment = commentService.createComment(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdComment.getId())
                .toUri();
        return ResponseEntity.ok(new CommentResponseDto(createdComment));
    }

    // 2. Получение комментария по ID
    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(summary = "Получить комментарий по ID", description = "Доступно для ролей ADMIN, USER, MODERATOR")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий найден"),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден")
    })
    public ResponseEntity<CommentResponseDto> findCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.findCommentById(id));
    }

    // 3. Удаление комментария по ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @commentOwnerValidator.isCommentOwner(#id, authentication)")
    @Operation(summary = "Удалить комментарий по ID", description = "Доступно владельцу комментария или ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Комментарий удален"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен или комментарий не найден")
    })
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long id) {
        commentService.deleteCommentById(id);
        return ResponseEntity.noContent().build();
    }

    // 4. Обновление комментария
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @commentOwnerValidator.isCommentOwner(#id, authentication)")
    @Operation(summary = "Обновить комментарий", description = "Доступно владельцу комментария или ADMIN")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарий обновлен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен или комментарий не найден")
    })
    public ResponseEntity<CommentResponseDto> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid CommentUpdateDto commentUpdateDto
    ) {
        CommentResponseDto updatedComment = commentService.updateCommentById(id, commentUpdateDto);
        return ResponseEntity.ok(updatedComment);
    }
}