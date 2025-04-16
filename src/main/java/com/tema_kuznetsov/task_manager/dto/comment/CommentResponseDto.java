package com.tema_kuznetsov.task_manager.dto.comment;

import com.tema_kuznetsov.task_manager.model.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDto {
    private Long id;
    private String text;
    private LocalDateTime createdAt;
    private Long authorId; // Только ID автора, а не весь объект
    private Long taskId; // Только ID задачи

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.createdAt = comment.getCreatedAt();
        this.authorId = comment.getAuthor().getId();
        this.taskId = comment.getTask().getId();
    }
}