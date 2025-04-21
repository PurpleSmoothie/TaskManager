package com.tema_kuznetsov.task_manager.dto.task;

import com.tema_kuznetsov.task_manager.model.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class TaskResponseDto {

    @Schema(description = "ID задачи", example = "42")
    private Long id;

    @Schema(description = "Название задачи", example = "Реализовать JWT авторизацию")
    private String title;

    @Schema(description = "Описание задачи", example = "Нужно внедрить JWT фильтр, сервис и конфиг")
    private String description;

    @Schema(description = "Статус задачи", example = "IN_PROGRESS")
    private String status;

    @Schema(description = "Приоритет задачи", example = "HIGH")
    private String priority;

    @Schema(description = "Дата создания", example = "2024-04-17T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Дата последнего обновления", example = "2024-04-18T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "ID владельца задачи", example = "1")
    private Long ownerId;

    @Schema(description = "ID исполнителя задачи", example = "2")
    private Long performerId;

    public TaskResponseDto(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
        this.ownerId = task.getOwnerId();
        this.performerId = task.getPerformer() != null ? task.getPerformer().getId() : null;
    }
}