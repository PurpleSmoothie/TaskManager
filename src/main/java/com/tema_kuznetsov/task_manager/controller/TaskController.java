package com.tema_kuznetsov.task_manager.controller;

import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskCreateDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskResponseDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskUpdateDto;
import com.tema_kuznetsov.task_manager.model.Task;
import com.tema_kuznetsov.task_manager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
@Tag(name = "Tasks", description = "Управление задачами")
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(
            summary = "Создать задачу",
            description = "Доступно для ролей: ADMIN, USER, MODERATOR"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверный запрос, ошибка в данных"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> createTask(
            @Valid @RequestBody TaskCreateDto dto) {

        Task createdTask = taskService.createTask(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTask.getId())
                .toUri();
        return ResponseEntity.created(location).body(new TaskResponseDto(createdTask));
    }

    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(
            summary = "Получить задачу по ID",
            description = "Доступно для ролей: ADMIN, USER, MODERATOR"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> findTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findTaskById(id));
    }

    @GetMapping("/search/exact")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(
            summary = "Поиск задачи по точному названию",
            description = "Поиск по точному совпадению названия задачи"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача с данным названием не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> findTaskByExactTitle(@RequestParam String title) {
        return ResponseEntity.ok(taskService.findTaskByExactTitle(title));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(
            summary = "Поиск задач по части названия",
            description = "Поддерживает пагинацию и сортировку"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задачи найдены"),
            @ApiResponse(responseCode = "404", description = "Задачи с данным названием не найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Page<TaskResponseDto>> findTaskByTitleContaining(
            @RequestParam String titlePart,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(taskService.findTaskByTitleContaining(titlePart, pageable));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(
            summary = "Получить все задачи",
            description = "Доступно только для ролей: ADMIN и MODERATOR, поддерживает пагинацию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задачи успешно получены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Page<TaskResponseDto>> findAllTasks(
            @PageableDefault(size = 10, sort = "createdAt", direction = DESC) Pageable pageable) {

        return ResponseEntity.ok(taskService.findAllTasks(pageable));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isTaskOwner(#id, authentication)")
    @Operation(
            summary = "Обновить задачу",
            description = "Доступно владельцу задачи или ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskUpdateDto updateDto) {

        TaskResponseDto updatedTask = taskService.updateTaskById(id, updateDto);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isTaskOwner(#id, authentication)")
    @Operation(
            summary = "Удалить задачу по ID",
            description = "Доступно владельцу задачи или ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/by-title/{title}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Удалить задачу по названию",
            description = "Доступно только для ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача с данным названием не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Void> deleteTaskByTitle(@PathVariable String title) {
        taskService.deleteTaskByTitle(title);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isTaskPerformer(#id, authentication)")
    @Operation(
            summary = "Обновить статус задачи",
            description = "Доступно исполнителю задачи или ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус задачи успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> updateTaskStatusById(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(taskService.updateTaskStatusById(id, status));
    }

    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Обновить приоритет задачи",
            description = "Доступно только для ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Приоритет задачи успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> updateTaskPriorityById(
            @PathVariable Long id,
            @RequestParam String priority) {

        return ResponseEntity.ok(taskService.updateTaskPriorityById(id, priority));
    }

    @PatchMapping("/{id}/performer")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Назначить исполнителя задачи",
            description = "Доступно только для ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Исполнитель задачи успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> updateTaskPerformerById(
            @PathVariable Long id,
            @RequestParam Long performer_id) {

        return ResponseEntity.ok(taskService.updateTaskPerformer(id, performer_id));
    }

    @GetMapping("/search/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(
            summary = "Поиск задач по статусу",
            description = "Доступно для ADMIN и MODERATOR, поддерживает пагинацию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задачи успешно найдены"),
            @ApiResponse(responseCode = "404", description = "Задачи с данным статусом не найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Page<TaskResponseDto>> findTasksByStatus(
            @RequestParam String status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(taskService.findTasksByStatus(status, pageable));
    }

    @GetMapping("/search/priority")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(
            summary = "Поиск задач по приоритету",
            description = "Доступно для ADMIN и MODERATOR, поддерживает пагинацию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задачи успешно найдены"),
            @ApiResponse(responseCode = "404", description = "Задачи с данным приоритетом не найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Page<TaskResponseDto>> findTasksByPriority(
            @RequestParam String priority,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(taskService.findTasksByPriority(priority, pageable));
    }

    @GetMapping("{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(
            summary = "Получить комментарии задачи",
            description = "Выводит все комментарии к задаче, доступно для ADMIN и MODERATOR, поддерживает пагинацию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарии получены"),
            @ApiResponse(responseCode = "404", description = "Комментарии для данной задачи не найдены"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Page<CommentResponseDto>> findCommentsByTaskId(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(taskService.findCommentsByTaskId(id, pageable));
    }
}