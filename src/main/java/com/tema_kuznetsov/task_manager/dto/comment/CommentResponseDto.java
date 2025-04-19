package com.tema_kuznetsov.task_manager.dto.comment;

import com.tema_kuznetsov.task_manager.model.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDto {

    @Schema(description = "ID комментария", example = "100")
    private Long id;

    @Schema(description = "Текст комментария", example = "Отличная задача, я взял её в работу.")
    private String text;

    @Schema(description = "Дата создания комментария", example = "2024-04-18T08:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "ID автора комментария", example = "1")
    private Long authorId;

    @Schema(description = "ID задачи, к которой относится комментарий", example = "42")
    private Long taskId;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.createdAt = comment.getCreatedAt();
        this.authorId = comment.getAuthor().getId();
        this.taskId = comment.getTask().getId();
    }
}
