package com.tema_kuznetsov.task_manager.dto.taskDto;
import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.UniqueTaskTitle;
import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.ValidTaskPriority;
import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.ValidTaskStatus;
import com.tema_kuznetsov.task_manager.model.constrain.TaskConstrains;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO для обновления задачи. Все поля опциональны (nullable).
 * При обновлении изменяются только переданные не-null поля.
 */
@Getter
@Setter
    public class TaskCreateDto {
        @NotBlank(message = "Title is required")
        @Size(max = TaskConstrains.MAX_TITLE_LENGTH)
        @UniqueTaskTitle
        private String title;

        @NotBlank(message = "Description is required")
        @Size(max = TaskConstrains.MAX_DESC_LENGTH)
        private String description;

        @NotNull
        private Long owner_id;

        @ValidTaskStatus
        private String status;

        @ValidTaskPriority
        private String priority;

        private Long performer_id;
    }

