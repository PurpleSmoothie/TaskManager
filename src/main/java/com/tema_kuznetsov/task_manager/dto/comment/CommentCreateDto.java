package com.tema_kuznetsov.task_manager.dto.comment;

import com.tema_kuznetsov.task_manager.models.constrains.CommentConstrains;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для создания комментария.
 * Содержит информацию о тексте комментария и привязанной задаче.
 */
@Getter
@Setter
public class CommentCreateDto {

    /**
     * Текст комментария.
     * Должен быть строкой, не пустой и не превышать максимальную длину.
     *
     * @see CommentConstrains#MAX_TEXT_LENGTH
     */
    @Schema(
            description = "Текст комментария",
            example = "Очень полезная задача, спасибо!",
            maxLength = CommentConstrains.MAX_TEXT_LENGTH
    )
    @NotBlank(message = "Текст комментария обязателен")
    @Size(
            max = CommentConstrains.MAX_TEXT_LENGTH,
            message = "Текст комментария не должен превышать " + CommentConstrains.MAX_TEXT_LENGTH + " символов"
    )
    private String text;

    /**
     * ID задачи, к которой прикрепляется комментарий.
     * Должен быть положительным числом и не может быть null.
     */
    @Schema(
            description = "ID задачи, к которой прикрепляется комментарий",
            example = "42"
    )
    @Positive(message = "ID должен быть положительным")
    @NotNull(message = "ID задачи обязателен")
    private Long task_id;
}