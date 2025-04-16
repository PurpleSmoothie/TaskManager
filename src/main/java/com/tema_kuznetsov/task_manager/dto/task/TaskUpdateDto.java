package com.tema_kuznetsov.task_manager.dto.task;

import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.UniqueTaskTitle;
import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.ValidTaskPriority;
import com.tema_kuznetsov.task_manager.annotation.taskAnnotations.ValidTaskStatus;
import com.tema_kuznetsov.task_manager.model.constrain.TaskConstrains;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateDto {
    @UniqueTaskTitle
    @Size(max = TaskConstrains.MAX_TITLE_LENGTH)
    private String title;
    @Size(max = TaskConstrains.MAX_DESC_LENGTH)
    private String description;
    @ValidTaskStatus
    private String status;
    @ValidTaskPriority
    private String priority;

    private Long owner_id;

    private Long performer_id;
}
