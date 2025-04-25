package com.tema_kuznetsov.task_manager.dto.comment;

import com.tema_kuznetsov.task_manager.models.constrains.CommentConstrains;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateDto {

    @Schema(
            description = "Обновлённый текст комментария",
            example = "Я передумал, это была не такая уж и полезная задача.",
            maxLength = CommentConstrains.MAX_TEXT_LENGTH
    )
    @NotBlank(message = "Текст комментария обязателен")
    @Size(
            max = CommentConstrains.MAX_TEXT_LENGTH,
            message = "Текст комментария не должен превышать " + CommentConstrains.MAX_TEXT_LENGTH + " символов"
    )
    private String text;
}
