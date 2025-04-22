package com.tema_kuznetsov.task_manager.dto.comment;

import com.tema_kuznetsov.task_manager.models.constrain.CommentConstrains;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentCreateDto {
    @NotBlank
    @Size(max = CommentConstrains.MAX_TEXT_LENGTH)
    private String text;

    private Long user_id;

    @NotNull
    private Long task_id;
}
