package com.tema_kuznetsov.task_manager.dto.comment;

import com.tema_kuznetsov.task_manager.model.constrain.CommentConstrains;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateDto {
    @NotBlank
    @Size(max = CommentConstrains.MAX_TEXT_LENGTH)
    private String text;
}
