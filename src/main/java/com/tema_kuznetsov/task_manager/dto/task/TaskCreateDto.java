package com.tema_kuznetsov.task_manager.dto.task;

import com.tema_kuznetsov.task_manager.annotations.taskAnnotations.UniqueTaskTitle;
import com.tema_kuznetsov.task_manager.models.constrains.TaskConstrains;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskCreateDto {

    @Schema(description = "Название задачи", example = "Разработать API")
    @NotBlank(message = "Название задачи обязательно")
    @Size(
            min = TaskConstrains.MIN_TITLE_LENGTH,
            max = TaskConstrains.MAX_TITLE_LENGTH,
            message = "Название должно содержать от " + TaskConstrains.MIN_TITLE_LENGTH +
                    " до " + TaskConstrains.MAX_TITLE_LENGTH + " символов"
    )
    @UniqueTaskTitle
    private String title;

    @Schema(description = "Описание задачи", example = "Разработать REST API для управления задачами")
    @NotBlank(message = "Описание задачи обязательно")
    @Size(
            min = TaskConstrains.MIN_DESC_LENGTH,
            max = TaskConstrains.MAX_DESC_LENGTH,
            message = "Описание должно содержать от " + TaskConstrains.MIN_DESC_LENGTH +
                    " до " + TaskConstrains.MAX_DESC_LENGTH + " символов"
    )
    private String description;

    @Schema(description = "Статус задачи", allowableValues = {"OPEN", "IN_PROGRESS", "COMPLETED", "CANCELLED"}, example = "OPEN")
    @Pattern(
            regexp = "OPEN|IN_PROGRESS|COMPLETED|CANCELLED",
            message = "Допустимые статусы: OPEN, IN_PROGRESS, COMPLETED, CANCELLED"
    )
    private String status;

    @Schema(description = "Приоритет задачи", allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}, example = "HIGH")
    @Pattern(
            regexp = "LOW|MEDIUM|HIGH|CRITICAL",
            message = "Допустимые приоритеты: LOW, MEDIUM, HIGH, CRITICAL"
    )
    private String priority;

    @Schema(description = "ID исполнителя задачи", example = "1")
    private Long performerId;
}