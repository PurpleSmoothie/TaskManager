package com.tema_kuznetsov.task_manager.services;

import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskCreateDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskResponseDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskUpdateDto;
import com.tema_kuznetsov.task_manager.exceptions.taskException.priorityException.IncorrectPriorityTitleException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.statusException.IncorrectStatusTitleException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.TaskIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.titleException.TaskTitleNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.UserEmailNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.ownerException.OwnerIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.performerException.PerformerIdNotFoundException;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.models.Task;
import com.tema_kuznetsov.task_manager.models.enums.TaskPriority;
import com.tema_kuznetsov.task_manager.models.enums.TaskStatus;
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

@RequiredArgsConstructor // конструктор только для файнал полей
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;


    //1. Создание задачи
    @Transactional
    public Task createTask(@Valid TaskCreateDto dto) {
        // Извлекаем текущего пользователя из Authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AppUser owner = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UserEmailNotFoundException(username));  // Получаем пользователя по email

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

    //2. Получение задачи по ID
    public TaskResponseDto findTaskById(Long id) {
        Task task = getTaskByIdOrThrow(id);
        return new TaskResponseDto(task); // Конвертируем Task в TaskResponseDto
    }

    //3. Получение задачи по её точному имени
    public TaskResponseDto findTaskByExactTitle(String name) {
        Task task = taskRepository.findTaskByTitle(name)
                .orElseThrow(() -> new TaskTitleNotFoundException(name));
        return new TaskResponseDto(task); // Конвертируем Task в TaskResponseDto
    }

    //4. Получение задач по части имени
    public Page <TaskResponseDto> findTaskByTitleContaining(String titlePart, Pageable pageable) {
        Page<Task> tasks = taskRepository.findTaskByTitleContaining(titlePart,pageable);
        if (tasks.isEmpty()) {
            throw new TaskTitleNotFoundException(titlePart);
        }
        return convertToDtoList(tasks);
    }

    //5. Получение всех задач
    public Page<TaskResponseDto> findAllTasks(Pageable pageable) {
        return convertToDtoList(taskRepository.findAll(pageable));
    }

    //6. Обновление задачи по айди
    @Transactional
    public TaskResponseDto updateTaskById(Long id, TaskUpdateDto dto) {
        Task task = getTaskByIdOrThrow(id);

        // Обновление полей с проверкой на null
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

        if (dto.getOwner_id() != null) {
            task.setOwner(userRepository.findById(dto.getOwner_id())
                    .orElseThrow(() -> new OwnerIdNotFoundException(dto.getOwner_id())));
        }

        // Явная обработка исполнителя (включая сброс)
        if (dto.getPerformer_id() != null) {
            task.setPerformer(userRepository.findById(dto.getPerformer_id())
                    .orElseThrow(() -> new PerformerIdNotFoundException(dto.getPerformer_id())));
        } else {
            task.setPerformer(null);
        }

        return new TaskResponseDto(task);
    }

    //7. Удаление задачи по айди
    @Transactional
    public void deleteTaskById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskIdNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }

    //8. Удаление задачи по имени
    @Transactional
    public void deleteTaskByTitle(String title) {
        if (!taskRepository.existsByTitle(title)) {
            throw new TaskTitleNotFoundException(title);
        }
        taskRepository.deleteTaskByTitle(title);
    }

   //9. Обновление статуса по айди
   @Transactional
   public TaskResponseDto updateTaskStatusById(Long id, String status) {
    Task task = getTaskByIdOrThrow(id);

    if (!TaskStatus.isValid(status)) {
        throw new IncorrectStatusTitleException(status);
    }

    task.setStatus(status);
    return new TaskResponseDto(task); // Автосохранение благодаря @Transactional
}

    //10. Обновление приоритета по айди
    @Transactional
    public TaskResponseDto updateTaskPriorityById(Long id, String priority) {
        Task task = getTaskByIdOrThrow(id);

        if (!TaskPriority.isValid(priority)) {
            throw new IncorrectPriorityTitleException(priority);
        }

        task.setPriority(priority);
        return new TaskResponseDto(task);
    }

    //11. Обновление перформера по айди
    @Transactional
    public TaskResponseDto updateTaskPerformer(Long id, Long performerId) {
        Task task = getTaskByIdOrThrow(id);
        AppUser performer = userRepository.findById(performerId)
                .orElseThrow(() -> new PerformerIdNotFoundException(performerId));

        task.setPerformer(performer);
        return new TaskResponseDto(task);
    }

    //12. Поиск по статусу
    public Page <TaskResponseDto> findTasksByStatus(String status, Pageable pageable) {
        if (TaskStatus.isValid(status)) {
            return convertToDtoList(taskRepository.findTasksByStatus(status,pageable));
        } else {
            throw new IncorrectStatusTitleException(status);
        }
    }

    //13. Поиск по приоритету
    public Page<TaskResponseDto> findTasksByPriority(String priority, Pageable pageable) {
        if (TaskPriority.isValid(priority)) {
            return convertToDtoList(taskRepository.findTasksByPriority(priority,pageable));
        } else {
            throw new IncorrectPriorityTitleException(priority);
        }
    }

    //14. Вывод всех комментариев определенной задачи по айди
    public Page<CommentResponseDto> findCommentsByTaskId(Long taskId, Pageable pageable) {
        getTaskByIdOrThrow(taskId);
        return commentService.getCommentsForTask(taskId, pageable);
    }

    // Вспомогательный метод для преобразования коллекци таксов в коллекцию ДТО
    private Page<TaskResponseDto> convertToDtoList(Page<Task> tasks) {
        return tasks.map(TaskResponseDto::new);

    }

    // метод для поиска задач по айди + валидация
    private Task getTaskByIdOrThrow(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskIdNotFoundException(id));
    }

    // метод для подтверждения мол пользователь и владелец задачи это один и тот же юзер
    public boolean isOwner(Long taskId, String userEmail) {
        Task task = getTaskByIdOrThrow(taskId);

        return task.getOwner().getEmail().equals(userEmail); // или getUsername(), зависит от модели
    }

    // метод для подтверждения мол пользователь и раб задачи это один и тот же юзер
    public boolean isPerformer(Long taskId, String userEmail) {
        Task task = getTaskByIdOrThrow(taskId);

        return task.getPerformer().getEmail().equals(userEmail); // или getUsername(), зависит от модели
    }


}
