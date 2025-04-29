package com.tema_kuznetsov.task_manager.controllers;

import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskCreateDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskResponseDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskUpdateDto;
import com.tema_kuznetsov.task_manager.models.Task;
import com.tema_kuznetsov.task_manager.models.constrains.TaskConstrains;
import com.tema_kuznetsov.task_manager.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.validation.constraints.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Контроллер для управления задачами.
 * Предоставляет CRUD-операции для создания, получения, обновления и удаления задач.
 * Поддерживает поиск задач по различным критериям и управление статусами/приоритетами.
 * Все операции защищены авторизацией через JWT и разграничением доступа по ролям.
 */
@Validated
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Управление задачами")
public class TaskController {
    private final TaskService taskService;

    /**
     * Создание новой задачи.
     * Доступно для ролей: ADMIN, USER, MODERATOR.
     *
     * @param dto данные для создания задачи
     * @return созданная задача с HTTP статусом 201
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(
            summary = "Создать задачу",
            description = "Доступно для ролей: ADMIN, USER, MODERATOR"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
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

    /**
     * Получение задачи по ID.
     * Доступно для ролей: ADMIN, USER, MODERATOR.
     *
     * @param id идентификатор задачи
     * @return найденная задача с HTTP статусом 200
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(
            summary = "Получить задачу по ID",
            description = "Доступно для ролей: ADMIN, USER, MODERATOR"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат идентификатора"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    public ResponseEntity<TaskResponseDto> findTaskById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id) {
        return ResponseEntity.ok(taskService.findTaskById(id));
    }

    /**
     * Поиск задачи по точному названию.
     * Доступно для ролей: ADMIN, USER, MODERATOR.
     *
     * @param title точное название задачи
     * @return найденная задача с HTTP статусом 200
     */
    @GetMapping("/search/exact")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(
            summary = "Поиск задачи по точному названию",
            description = "Поиск по точному совпадению названия задачи"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача с данным названием не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат названия"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    public ResponseEntity<TaskResponseDto> findTaskByExactTitle(
            @RequestParam
            @NotBlank(message = "Название обязательно")
            @Size(min = TaskConstrains.MIN_TITLE_LENGTH, max = TaskConstrains.MAX_TITLE_LENGTH,
                    message = "Название должно содержать от " + TaskConstrains.MIN_TITLE_LENGTH +
                            " до " + TaskConstrains.MAX_TITLE_LENGTH + " символов")
            String title) {
        return ResponseEntity.ok(taskService.findTaskByExactTitle(title));
    }

    /**
     * Поиск задач по части названия с пагинацией.
     * Доступно для ролей: ADMIN, USER, MODERATOR.
     *
     * @param titlePart часть названия для поиска
     * @param pageable параметры пагинации и сортировки
     * @return страница найденных задач с HTTP статусом 200
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'MODERATOR')")
    @Operation(
            summary = "Поиск задач по части названия",
            description = "Поддерживает пагинацию и сортировку"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задачи найдены"),
            @ApiResponse(responseCode = "404", description = "Задачи с данным названием не найдены"),
            @ApiResponse(responseCode = "400", description = "Неверный формат названия"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)")
    })
    public ResponseEntity<Page<TaskResponseDto>> findTaskByTitleContaining(
            @NotBlank(message = "Название обязательно")
            @Size(max = TaskConstrains.MAX_TITLE_LENGTH,
                    message = "Название должно содержать до " + TaskConstrains.MAX_TITLE_LENGTH + " символов")
            @RequestParam String titlePart,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.findTaskByTitleContaining(titlePart, pageable));
    }

    /**
     * Получение всех задач с пагинацией.
     * Доступно только для ролей: ADMIN и MODERATOR.
     *
     * @param pageable параметры пагинации и сортировки
     * @return страница всех задач с HTTP статусом 200
     */
    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(
            summary = "Получить все задачи",
            description = "Доступно только для ролей: ADMIN и MODERATOR, поддерживает пагинацию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задачи успешно получены"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Page<TaskResponseDto>> findAllTasks(
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.findAllTasks(pageable));
    }

    /**
     * Обновление задачи.
     * Доступно владельцу задачи или ADMIN.
     *
     * @param id идентификатор задачи
     * @param updateDto данные для обновления задачи
     * @return обновленная задача с HTTP статусом 200
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isTaskOwner(#id, authentication)")
    @Operation(
            summary = "Обновить задачу",
            description = "Доступно владельцу задачи или ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
            @ApiResponse(responseCode = "404", description = "Задача с данным идентификатором не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат данных"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id,
            @Valid @RequestBody TaskUpdateDto updateDto) {
        return ResponseEntity.ok(taskService.updateTaskById(id, updateDto));
    }

    /**
     * Удаление задачи по ID.
     * Доступно владельцу задачи или ADMIN.
     *
     * @param id идентификатор задачи для удаления
     * @return HTTP статус 204 (No Content) при успешном удалении
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isTaskOwner(#id, authentication)")
    @Operation(
            summary = "Удалить задачу по ID",
            description = "Доступно владельцу задачи или ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат идентификатора"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Void> deleteTaskById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Удаление задачи по названию.
     * Доступно только для ADMIN.
     *
     * @param title название задачи для удаления
     * @return HTTP статус 204 (No Content) при успешном удалении
     */
    @DeleteMapping("/by-title/{title}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Удалить задачу по названию",
            description = "Доступно только для ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
            @ApiResponse(responseCode = "404", description = "Задача с данным названием не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат названия"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Void> deleteTaskByTitle(
            @PathVariable
            @NotBlank(message = "Название обязательно")
            @Size(min = TaskConstrains.MIN_TITLE_LENGTH, max = TaskConstrains.MAX_TITLE_LENGTH,
                    message = "Название должно содержать от " + TaskConstrains.MIN_TITLE_LENGTH +
                            " до " + TaskConstrains.MAX_TITLE_LENGTH + " символов")
            String title) {
        taskService.deleteTaskByTitle(title);
        return ResponseEntity.noContent().build();
    }

    /**
     * Обновление статуса задачи.
     * Доступно владельцу задачи, исполнителю задачи или ADMIN.
     *
     * @param id идентификатор задачи
     * @param status новый статус задачи
     * @return обновленная задача с HTTP статусом 200
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isTaskOwner(#id, authentication) " +
            "or @taskOwnerValidator.isTaskPerformer(#id, authentication)")
    @Operation(
            summary = "Обновить статус задачи",
            description = "Доступно владельцу задачи, исполнителю задачи или ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Статус задачи успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат статуса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> updateTaskStatusById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id,
            @RequestParam
            @Pattern(regexp = "OPEN|IN_PROGRESS|COMPLETED|CANCELLED",
                    message = "Допустимые статусы: OPEN, IN_PROGRESS, COMPLETED, CANCELLED")
            String status) {
        return ResponseEntity.ok(taskService.updateTaskStatusById(id, status));
    }

    /**
     * Обновление приоритета задачи.
     * Доступно владельцу задачи, исполнителю задачи или ADMIN.
     *
     * @param id идентификатор задачи
     * @param priority новый приоритет задачи
     * @return обновленная задача с HTTP статусом 200
     */
    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isTaskOwner(#id, authentication) " +
            "or @taskOwnerValidator.isTaskPerformer(#id, authentication)")
    @Operation(
            summary = "Обновить приоритет задачи",
            description = "Доступно владельцу задачи, исполнителю задачи или ADMIN"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Приоритет задачи успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат приоритета"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> updateTaskPriorityById(
            @PathVariable
            @Min(value = 1, message = "ID должен быть положительным")
            Long id,
            @RequestParam
            @Pattern(regexp = "LOW|MEDIUM|HIGH|CRITICAL", message = "Допустимые приоритеты: LOW, MEDIUM, HIGH, CRITICAL")
            String priority) {
        return ResponseEntity.ok(taskService.updateTaskPriorityById(id, priority));
    }

    /**
     * Назначение исполнителя задачи.
     * Доступно только для ADMIN или владельца задачи.
     *
     * @param id идентификатор задачи
     * @param performerId идентификатор исполнителя
     * @return обновленная задача с HTTP статусом 200
     */
    @PatchMapping("/{id}/performer")
    @PreAuthorize("hasRole('ADMIN') or @taskOwnerValidator.isTaskOwner(#id, authentication)")
    @Operation(
            summary = "Назначить исполнителя задачи",
            description = "Доступно только для ADMIN или владельца задачи"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Исполнитель задачи успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Задача с данным ID не найдена"),
            @ApiResponse(responseCode = "400", description = "Неверный формат идентификатора"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<TaskResponseDto> updateTaskPerformerById(
            @PathVariable
            @Min(value = 1, message = "ID задачи должен быть положительным")
            Long id,
            @RequestParam
            @Min(value = 1, message = "ID исполнителя должен быть положительным")
            Long performerId) {
        return ResponseEntity.ok(taskService.updateTaskPerformer(id, performerId));
    }

    /**
     * Поиск задач по статусу с пагинацией.
     * Доступно для ADMIN и MODERATOR.
     *
     * @param status статус для поиска
     * @param pageable параметры пагинации и сортировки
     * @return страница найденных задач с HTTP статусом 200
     */
    @GetMapping("/search/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(
            summary = "Поиск задач по статусу",
            description = "Доступно для ADMIN и MODERATOR, поддерживает пагинацию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задачи успешно найдены"),
            @ApiResponse(responseCode = "400", description = "Неверный формат статуса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Page<TaskResponseDto>> findTasksByStatus(
            @RequestParam
            @Pattern(regexp = "OPEN|IN_PROGRESS|COMPLETED|CANCELLED",
                    message = "Допустимые статусы: OPEN, IN_PROGRESS, COMPLETED, CANCELLED")
            String status,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.findTasksByStatus(status, pageable));
    }

    /**
     * Поиск задач по приоритету с пагинацией.
     * Доступно для ADMIN и MODERATOR.
     *
     * @param priority приоритет для поиска
     * @param pageable параметры пагинации и сортировки
     * @return страница найденных задач с HTTP статусом 200
     */
    @GetMapping("/search/priority")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(
            summary = "Поиск задач по приоритету",
            description = "Доступно для ADMIN и MODERATOR, поддерживает пагинацию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задачи успешно найдены"),
            @ApiResponse(responseCode = "400", description = "Неверный формат приоритета"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Page<TaskResponseDto>> findTasksByPriority(
            @RequestParam
            @Pattern(regexp = "LOW|MEDIUM|HIGH|CRITICAL",
                    message = "Допустимые приоритеты: LOW, MEDIUM, HIGH, CRITICAL")
            String priority,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.findTasksByPriority(priority, pageable));
    }

    /**
     * Получение всех комментариев к задаче с пагинацией.
     * Доступно для ADMIN и MODERATOR.
     *
     * @param id идентификатор задачи
     * @param pageable параметры пагинации и сортировки
     * @return страница комментариев с HTTP статусом 200
     */
    @GetMapping("{id}/comments")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @Operation(
            summary = "Получить все комментарии к задаче",
            description = "Доступно для ADMIN и MODERATOR, поддерживает пагинацию"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комментарии получены"),
            @ApiResponse(responseCode = "400", description = "Неверный формат идентификатора"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован " +
                    "(JWT токен отсутствует или некорректен)"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    public ResponseEntity<Page<CommentResponseDto>> findCommentsByTaskId(
            @PathVariable
            @Min(value = 1, message = "ID задачи должен быть положительным")
            Long id,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(taskService.findCommentsByTaskId(id, pageable));
    }
}