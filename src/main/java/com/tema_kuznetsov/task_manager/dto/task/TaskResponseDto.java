package com.tema_kuznetsov.task_manager.dto.task;

import com.tema_kuznetsov.task_manager.models.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO для ответа с данными задачи.
 * Включает информацию о задаче, такую как статус, приоритет, даты и идентификаторы.
 */
@Getter
@Setter
@NoArgsConstructor
public class TaskResponseDto {

    /**
     * ID задачи.
     */
    @Schema(description = "ID задачи", example = "42")
    private Long id;

    /**
     * Название задачи.
     */
    @Schema(description = "Название задачи", example = "Реализовать JWT авторизацию")
    private String title;

    /**
     * Описание задачи.
     */
    @Schema(description = "Описание задачи", example = "Нужно внедрить JWT фильтр, сервис и конфиг")
    private String description;

    /**
     * Статус задачи.
     */
    @Schema(description = "Статус задачи", example = "IN_PROGRESS")
    private String status;

    /**
     * Приоритет задачи.
     */
    @Schema(description = "Приоритет задачи", example = "HIGH")
    private String priority;

    /**
     * Дата создания задачи.
     * В формате ISO 8601.
     */
    @Schema(description = "Дата создания", example = "2024-04-17T12:00:00")
    private LocalDateTime createdAt;

    /**
     * Дата последнего обновления задачи.
     * В формате ISO 8601.
     */
    @Schema(description = "Дата последнего обновления", example = "2024-04-18T10:00:00")
    private LocalDateTime updatedAt;

    /**
     * ID владельца задачи.
     */
    @Schema(description = "ID владельца задачи", example = "1")
    private Long ownerId;

    /**
     * ID исполнителя задачи.
     * Может быть null, если задача не назначена исполнителю.
     */
    @Schema(description = "ID исполнителя задачи", example = "2")
    private Long performerId;

    /**
     * Конструктор, преобразующий сущность Task в TaskResponseDto.
     *
     * @param task Объект Task для преобразования.
     */
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