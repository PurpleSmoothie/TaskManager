package com.tema_kuznetsov.task_manager.dto.taskDto;

import com.tema_kuznetsov.task_manager.model.Task;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long owner_id; // Только ID, без циклической ссылки
    private Long performer_id;

    public TaskResponseDto(Task task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.priority = task.getPriority();
        this.createdAt = task.getCreatedAt();
        this.updatedAt = task.getUpdatedAt();
        this.owner_id = task.getOwnerId();
        this.performer_id = task.getPerformer() != null ? task.getPerformer().getId() : null;
        // проверка, если у оригинального таска нету перформера, то тут тоже будет нулл
    }
}
