package com.tema_kuznetsov.task_manager.services;

import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskCreateDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskResponseDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskUpdateDto;
import com.tema_kuznetsov.task_manager.exceptions.taskException.TaskIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.titleException.TaskTitleNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.UserEmailNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.ownerException.OwnerIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.performerException.PerformerIdNotFoundException;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.models.Task;
import com.tema_kuznetsov.task_manager.repositories.TaskRepository;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

/**
 * Сервис для работы с задачами.
 * Предоставляет методы для создания, обновления, удаления и поиска задач.
 */
@RequiredArgsConstructor
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;

    /**
     * Создает новую задачу.
     * Включает в себя установку владельца задачи (из аутентификации пользователя) и исполнителя (если указан).
     *
     * @param dto объект, содержащий информацию о новой задаче
     * @return созданная задача
     */
    @Transactional
    public Task createTask(@Valid TaskCreateDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser owner = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UserEmailNotFoundException(username));

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setOwner(owner);

        if (dto.getPerformerId() != null) {
            task.setPerformer(userRepository.findById(dto.getPerformerId())
                    .orElseThrow(() -> new PerformerIdNotFoundException(dto.getPerformerId())));
        }

        return taskRepository.save(task);
    }

    /**
     * Находит задачу по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @return DTO с информацией о задаче
     * @throws TaskIdNotFoundException если задача не найдена
     */
    public TaskResponseDto findTaskById(Long id) {
        Task task = getTaskByIdOrThrow(id);
        return new TaskResponseDto(task);
    }

    /**
     * Находит задачу по точному совпадению названия.
     *
     * @param title название задачи
     * @return DTO с информацией о задаче
     * @throws TaskTitleNotFoundException если задача не найдена
     */
    public TaskResponseDto findTaskByExactTitle(String title) {
        Task task = taskRepository.findTaskByTitle(title)
                .orElseThrow(() -> new TaskTitleNotFoundException(title));
        return new TaskResponseDto(task);
    }

    /**
     * Находит задачи, содержащие в названии указанную подстроку.
     *
     * @param titlePart подстрока для поиска в названии задачи
     * @param pageable объект для пагинации
     * @return список задач в виде страниц
     * @throws TaskTitleNotFoundException если задачи не найдены
     */
    public Page<TaskResponseDto> findTaskByTitleContaining(String titlePart, Pageable pageable) {
        Page<Task> tasks = taskRepository.findTaskByTitleContaining(titlePart, pageable);
        if (tasks.isEmpty()) {
            throw new TaskTitleNotFoundException(titlePart);
        }
        return convertToDtoList(tasks);
    }

    /**
     * Получает все задачи.
     *
     * @param pageable объект для пагинации
     * @return список всех задач в виде страниц
     */
    public Page<TaskResponseDto> findAllTasks(Pageable pageable) {
        return convertToDtoList(taskRepository.findAll(pageable));
    }

    /**
     * Обновляет задачу по ее идентификатору.
     * Обновляются только те поля, которые указаны в DTO и не являются пустыми.
     *
     * @param id идентификатор задачи
     * @param dto объект с обновленными данными задачи
     * @return DTO с информацией об обновленной задаче
     */
    @Transactional
    public TaskResponseDto updateTaskById(Long id, TaskUpdateDto dto) {
        Task task = getTaskByIdOrThrow(id);

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            task.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            task.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            task.setStatus(dto.getStatus());
        }

        if (dto.getPriority() != null && !dto.getPriority().isBlank()) {
            task.setPriority(dto.getPriority());
        }

        if (dto.getOwnerId() != null) {
            task.setOwner(userRepository.findById(dto.getOwnerId())
                    .orElseThrow(() -> new OwnerIdNotFoundException(dto.getOwnerId())));
        }

        if (dto.getPerformerId() != null) {
            task.setPerformer(userRepository.findById(dto.getPerformerId())
                    .orElseThrow(() -> new PerformerIdNotFoundException(dto.getPerformerId())));
        }

        return new TaskResponseDto(task);
    }

    /**
     * Удаляет задачу по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @throws TaskIdNotFoundException если задача не найдена
     */
    @Transactional
    public void deleteTaskById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskIdNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    /**
     * Удаляет задачу по ее названию.
     *
     * @param title название задачи
     * @throws TaskTitleNotFoundException если задача не найдена
     */
    @Transactional
    public void deleteTaskByTitle(String title) {
        if (!taskRepository.existsByTitle(title)) {
            throw new TaskTitleNotFoundException(title);
        }
        taskRepository.deleteTaskByTitle(title);
    }

    /**
     * Обновляет статус задачи по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @param status новый статус задачи
     * @return DTO с информацией об обновленной задаче
     */
    @Transactional
    public TaskResponseDto updateTaskStatusById(Long id, String status) {
        Task task = getTaskByIdOrThrow(id);
        task.setStatus(status);
        return new TaskResponseDto(task);
    }

    /**
     * Обновляет приоритет задачи по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @param priority новый приоритет задачи
     * @return DTO с информацией об обновленной задаче
     */
    @Transactional
    public TaskResponseDto updateTaskPriorityById(Long id, String priority) {
        Task task = getTaskByIdOrThrow(id);
        task.setPriority(priority);
        return new TaskResponseDto(task);
    }

    /**
     * Обновляет исполнителя задачи по ее идентификатору.
     *
     * @param id идентификатор задачи
     * @param performerId идентификатор нового исполнителя
     * @return DTO с информацией об обновленной задаче
     * @throws PerformerIdNotFoundException если исполнитель не найден
     */
    @Transactional
    public TaskResponseDto updateTaskPerformer(Long id, Long performerId) {
        Task task = getTaskByIdOrThrow(id);
        AppUser performer = userRepository.findById(performerId)
                .orElseThrow(() -> new PerformerIdNotFoundException(performerId));
        task.setPerformer(performer);
        return new TaskResponseDto(task);
    }

    /**
     * Находит задачи по статусу.
     *
     * @param status статус задачи для поиска
     * @param pageable объект для пагинации
     * @return список задач в виде страниц
     */
    public Page<TaskResponseDto> findTasksByStatus(String status, Pageable pageable) {
        return convertToDtoList(taskRepository.findTasksByStatus(status, pageable));
    }

    /**
     * Находит задачи по приоритету.
     *
     * @param priority приоритет задачи для поиска
     * @param pageable объект для пагинации
     * @return список задач в виде страниц
     */
    public Page<TaskResponseDto> findTasksByPriority(String priority, Pageable pageable) {
        return convertToDtoList(taskRepository.findTasksByPriority(priority, pageable));
    }

    /**
     * Получает комментарии для указанной задачи.
     *
     * @param taskId идентификатор задачи
     * @param pageable объект для пагинации
     * @return список комментариев в виде страниц
     */
    public Page<CommentResponseDto> findCommentsByTaskId(Long taskId, Pageable pageable) {
        getTaskByIdOrThrow(taskId);
        return commentService.getCommentsForTask(taskId, pageable);
    }

    /**
     * Преобразует страницу задач в страницу DTO.
     *
     * @param tasks страница задач
     * @return страница DTO задач
     */
    private Page<TaskResponseDto> convertToDtoList(Page<Task> tasks) {
        return tasks.map(TaskResponseDto::new);
    }

    /**
     * Находит задачу по ее идентификатору.
     * Если задача не найдена, выбрасывает исключение {@link TaskIdNotFoundException}.
     *
     * @param id идентификатор задачи
     * @return найденная задача
     * @throws TaskIdNotFoundException если задача не найдена
     */
    private Task getTaskByIdOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskIdNotFoundException(id));
    }

    /**
     * Проверяет, является ли пользователь владельцем задачи.
     *
     * @param taskId идентификатор задачи
     * @param userEmail email пользователя
     * @return true, если пользователь является владельцем задачи, иначе false
     */
    public boolean isOwner(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId).orElse(null);
        return task != null && task.getOwner().getEmail().equals(userEmail);
    }

    /**
     * Проверяет, является ли пользователь исполнителем задачи.
     *
     * @param taskId идентификатор задачи
     * @param userEmail email пользователя
     * @return true, если пользователь является исполнителем задачи, иначе false
     */
    public boolean isPerformer(Long taskId, String userEmail) {
        Task task = taskRepository.findById(taskId).orElse(null);
        return task != null && task.getPerformer().getEmail().equals(userEmail);
    }
}