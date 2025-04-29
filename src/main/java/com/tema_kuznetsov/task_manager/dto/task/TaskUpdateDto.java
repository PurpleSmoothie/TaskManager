package com.tema_kuznetsov.task_manager.dto.task;

import com.tema_kuznetsov.task_manager.annotations.taskAnnotations.NullableTaskPriority;
import com.tema_kuznetsov.task_manager.annotations.taskAnnotations.NullableTaskStatus;
import com.tema_kuznetsov.task_manager.annotations.taskAnnotations.UniqueTaskTitle;
import com.tema_kuznetsov.task_manager.annotations.userAnnotations.NullableSize;
import com.tema_kuznetsov.task_manager.models.constrains.TaskConstrains;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для обновления задачи.
 * Содержит новый текст, описание, статус, приоритет и ID владельца или исполнителя.
 */
@Getter
@Setter
public class TaskUpdateDto {

    /**
     * Новое название задачи.
     * Может быть null или пустым для частичного обновления.
     * Должно содержать от минимальной до максимальной длины, если указано.
     *
     * @see TaskConstrains#MIN_TITLE_LENGTH
     * @see TaskConstrains#MAX_TITLE_LENGTH
     */
    @Schema(description = "Название задачи", example = "Обновить API")
    @UniqueTaskTitle
    @NullableSize(
            min = TaskConstrains.MIN_TITLE_LENGTH,
            max = TaskConstrains.MAX_TITLE_LENGTH,
            message = "Название должно содержать от " + TaskConstrains.MIN_TITLE_LENGTH +
                    " до " + TaskConstrains.MAX_TITLE_LENGTH + " символов"
    )
    private String title;

    /**
     * Новое описание задачи.
     * Может быть null или пустым для частичного обновления.
     * Должно содержать от минимальной до максимальной длины, если указано.
     *
     * @see TaskConstrains#MIN_DESC_LENGTH
     * @see TaskConstrains#MAX_DESC_LENGTH
     */
    @Schema(description = "Описание задачи", example = "Добавить новые эндпоинты для задач")
    @NullableSize(
            min = TaskConstrains.MIN_DESC_LENGTH,
            max = TaskConstrains.MAX_DESC_LENGTH,
            message = "Описание должно содержать от " + TaskConstrains.MIN_DESC_LENGTH +
                    " до " + TaskConstrains.MAX_DESC_LENGTH + " символов"
    )
    private String description;

    /**
     * Новый статус задачи.
     * Может быть null для частичного обновления.
     * Допустимые значения: OPEN, IN_PROGRESS, COMPLETED, CANCELLED.
     */
    @Schema(description = "Статус задачи", allowableValues = {"OPEN", "IN_PROGRESS", "COMPLETED", "CANCELLED"},
            example = "IN_PROGRESS")
    @NullableTaskStatus
    @Pattern(
            regexp = "OPEN|IN_PROGRESS|COMPLETED|CANCELLED",
            message = "Допустимые статусы: OPEN, IN_PROGRESS, COMPLETED, CANCELLED"
    )
    private String status;

    /**
     * Новый приоритет задачи.
     * Может быть null для частичного обновления.
     * Допустимые значения: LOW, MEDIUM, HIGH, CRITICAL.
     */
    @Schema(description = "Приоритет задачи", allowableValues = {"LOW", "MEDIUM", "HIGH", "CRITICAL"}, example = "MEDIUM")
    @NullableTaskPriority
    @Pattern(
            regexp = "LOW|MEDIUM|HIGH|CRITICAL",
            message = "Допустимые приоритеты: LOW, MEDIUM, HIGH, CRITICAL"
    )
    private String priority;

    /**
     * Новый ID владельца задачи.
     * Не может быть отрицательным числом.
     */
    @Positive(message = "ID должен быть положительным")
    @Schema(description = "ID владельца задачи", example = "1")
    private Long ownerId;

    /**
     * Новый ID исполнителя задачи.
     * Может быть null для частичного обновления.
     */
    @Schema(description = "ID исполнителя задачи", example = "2")
    private Long performerId;
}