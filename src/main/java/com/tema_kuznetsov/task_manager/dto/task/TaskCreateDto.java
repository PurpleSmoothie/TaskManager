package com.tema_kuznetsov.task_manager.dto.task;
import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.UniqueTaskTitle;
import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.ValidTaskPriority;
import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.ValidTaskStatus;
import com.tema_kuznetsov.task_manager.models.constrain.TaskConstrains;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TaskCreateDto {
    @NotBlank(message = "Title is required")
    @Size(max = TaskConstrains.MAX_TITLE_LENGTH)
    @UniqueTaskTitle
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = TaskConstrains.MAX_DESC_LENGTH)
    private String description;

    private Long ownerId;

    @NotBlank(message = "Status is required")
    @ValidTaskStatus
    private String status;

    @NotBlank(message = "Priority is required")
    @ValidTaskPriority
    private String priority;

    private Long performerId;
}