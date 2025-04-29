package com.tema_kuznetsov.task_manager.dto.comment;

import com.tema_kuznetsov.task_manager.models.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO для ответа с комментариями.
 * Включает детали комментария, такие как текст, автор и время создания.
 */
@Getter
@Setter
public class CommentResponseDto {

    /**
     * ID комментария.
     */
    @Schema(description = "ID комментария", example = "100")
    private Long id;

    /**
     * Текст комментария.
     */
    @Schema(description = "Текст комментария", example = "Отличная задача, я взял её в работу.")
    private String text;

    /**
     * Дата создания комментария.
     * В формате ISO 8601.
     */
    @Schema(description = "Дата создания комментария", example = "2024-04-18T08:30:00")
    private LocalDateTime createdAt;

    /**
     * ID автора комментария.
     */
    @Schema(description = "ID автора комментария", example = "1")
    private Long authorId;

    /**
     * ID задачи, к которой относится комментарий.
     */
    @Schema(description = "ID задачи, к которой относится комментарий", example = "42")
    private Long taskId;

    /**
     * Конструктор, преобразующий сущность Comment в CommentResponseDto.
     *
     * @param comment Объект Comment для преобразования.
     */
    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.createdAt = comment.getCreatedAt();
        this.authorId = comment.getAuthor().getId();
        this.taskId = comment.getTask().getId();
    }
}