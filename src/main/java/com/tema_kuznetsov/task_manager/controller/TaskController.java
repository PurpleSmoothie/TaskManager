package com.tema_kuznetsov.task_manager.controller;

import com.tema_kuznetsov.task_manager.dto.commentDto.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.taskDto.TaskCreateDto;
import com.tema_kuznetsov.task_manager.dto.taskDto.TaskResponseDto;
import com.tema_kuznetsov.task_manager.dto.taskDto.TaskUpdateDto;
import com.tema_kuznetsov.task_manager.model.Task;
import com.tema_kuznetsov.task_manager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    // 1. Создать задачу
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskCreateDto dto) {
        Task createdTask = taskService.createTask(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();
        return ResponseEntity.ok(new TaskResponseDto(createdTask));
    }

    // 2. Получить задачу по ID
    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    public ResponseEntity<TaskResponseDto> findTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findTaskById(id));
    }

    // 3. Поиск по точному названию
    @GetMapping("/search/exact")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    public ResponseEntity<TaskResponseDto> findTaskByExactTitle(@RequestParam String title) {
        return ResponseEntity.ok(taskService.findTaskByExactTitle(title));
    }

    // 4. Поиск по кусочку названия
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    public ResponseEntity<Page<TaskResponseDto>> findTaskByTitleContaining(
            @RequestParam String titlePart,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.findTaskByTitleContaining(titlePart,pageable));
    }

    // 5. Получить все задачи
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<TaskResponseDto>> findAllTasks(
            @PageableDefault(size = 10, sort = "createdAt", direction = DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.findAllTasks(pageable));
    }

    // 6. Обновление задачи по ID
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isTaskOwner(#id, authentication)")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskUpdateDto updateDto
    ) {
        TaskResponseDto updatedTask = taskService.updateTaskById(id, updateDto);
        return ResponseEntity.ok(updatedTask);
    }

    // 7. Удалить задачу по айди
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    // 8. Удалить задачу по имени
    @DeleteMapping("/by-title/{title}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTaskByTitle(
            @PathVariable String title) {
        taskService.deleteTaskByTitle(title);
        return ResponseEntity.noContent().build();
    }

    // 9. Обновление статуса по айди
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isTaskPerformer(#id, authentication)")
    public ResponseEntity<TaskResponseDto> updateTaskStatusById(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(taskService.updateTaskStatusById(id, status));
    }

    // 10. Обновление приоритета по айди
    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponseDto> updateTaskPriorityById(
            @PathVariable Long id,
            @RequestParam String priority) {
        return ResponseEntity.ok(taskService.updateTaskPriorityById(id, priority));
    }

    // 11. Обновление перформера по айди
    @PatchMapping("/{id}/performer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponseDto> updateTaskPerformerById(
            @PathVariable Long id,
            @RequestParam Long performer_id) {
        return ResponseEntity.ok(taskService.updateTaskPerformer(id,performer_id));
    }

    // 12. Поиск по статусу
    @GetMapping("/search/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<TaskResponseDto>> findTasksByStatus(
            @RequestParam String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.findTasksByStatus(status,pageable));
    }

    // 13. Поиск по приоритету
    @GetMapping("/search/priority")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Page<TaskResponseDto>> findTasksByPriority(
            @RequestParam String priority,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.findTasksByPriority(priority,pageable));
    }

    // 14. Вывод всех комментов определенной задачи по айди
    @GetMapping("{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    public ResponseEntity<Page<CommentResponseDto>> findCommentsByTaskId(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.findCommentsByTaskId(id,pageable));
    }
}