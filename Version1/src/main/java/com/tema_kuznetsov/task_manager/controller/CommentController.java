package com.tema_kuznetsov.task_manager.controller;

import com.tema_kuznetsov.task_manager.dto.commentDto.CommentCreateDto;
import com.tema_kuznetsov.task_manager.dto.commentDto.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.commentDto.CommentUpdateDto;
import com.tema_kuznetsov.task_manager.model.Comment;
import com.tema_kuznetsov.task_manager.service.CommentService;
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
public class CommentController {
    private final CommentService commentService;

    //1. Создание комментария
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    public ResponseEntity<CommentResponseDto> createUser(@Valid @RequestBody CommentCreateDto dto) {
        Comment createdComment = commentService.createComment(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdComment.getId())
                .toUri();
        return ResponseEntity.ok(new CommentResponseDto(createdComment));
    }

    //2. Получение комментария по айди
    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    public ResponseEntity<CommentResponseDto> findCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.findCommentById(id));
    }

    //3. Удаление комментария по айди
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isCommentOwner(#id, authentication)")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long id) {
        commentService.deleteCommentById(id);
        return ResponseEntity.noContent().build();
    }

    //4. Обновление комментария
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isCommentOwner(#id, authentication)")
    public ResponseEntity<CommentResponseDto> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid CommentUpdateDto commentUpdateDto
    ) {
        CommentResponseDto updatedComment = commentService.updateCommentById(id, commentUpdateDto);
        return ResponseEntity.ok(updatedComment);
    }
}
