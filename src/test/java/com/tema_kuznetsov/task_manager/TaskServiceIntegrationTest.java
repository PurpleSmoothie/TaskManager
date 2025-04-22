package com.tema_kuznetsov.task_manager;

import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskCreateDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskResponseDto;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.models.Comment;
import com.tema_kuznetsov.task_manager.models.Task;
import com.tema_kuznetsov.task_manager.repositories.TaskRepository;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import com.tema_kuznetsov.task_manager.services.CommentService;
import com.tema_kuznetsov.task_manager.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @MockBean
    private CommentService commentService;

    private AppUser owner;
    private AppUser performer;

    @Transactional
    @BeforeEach
    void setUp() {
        // Создание пользователей
        owner = new AppUser();
        owner.setEmail("owner@mail.com");
        owner.setPassword("12345678LOL");
        owner.setLogin("Owner");
        userRepository.save(owner);

        performer = new AppUser();
        performer.setEmail("performer@mail.com");
        performer.setPassword("12345678LOL");
        performer.setLogin("Performer");
        userRepository.save(performer);

        // Аутентификация владельца
        var auth = new UsernamePasswordAuthenticationToken(owner.getEmail(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testCreateTask() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        Task createdTask = taskService.createTask(dto);

        assertThat(createdTask.getId()).isNotNull();
        assertThat(createdTask.getTitle()).isEqualTo("Тестовая задача");
        assertThat(createdTask.getOwner().getId()).isEqualTo(owner.getId());
        assertThat(createdTask.getPerformer().getId()).isEqualTo(performer.getId());
    }

    @Test
    void testFindTaskById() {
        Task task = createSampleTask("Test ID");
        TaskResponseDto dto = taskService.findTaskById(task.getId());

        assertThat(dto.getId()).isEqualTo(task.getId());
        assertThat(dto.getTitle()).isEqualTo("Test ID");
    }

    @Test
    void testFindTaskByExactTitle() {
        createSampleTask("Exact Match");

        TaskResponseDto dto = taskService.findTaskByExactTitle("Exact Match");

        assertThat(dto.getTitle()).isEqualTo("Exact Match");
    }

    @Test
    void testFindTaskByTitleContaining() {
        createSampleTask("Find Me");

        Page<TaskResponseDto> page = taskService.findTaskByTitleContaining("Find", PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getTitle()).contains("Find");
    }

    @Test
    void testFindCommentsByTaskId() {
        Task task = createSampleTask("Комментируемая");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Nice task");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(owner);
        comment.setTask(task);

        CommentResponseDto dto = new CommentResponseDto(comment);
        Page<CommentResponseDto> commentPage = new PageImpl<>(List.of(dto));

        Mockito.when(commentService.getCommentsForTask(eq(task.getId()), any()))
                .thenReturn(commentPage);

        Page<CommentResponseDto> result = taskService.findCommentsByTaskId(task.getId(), PageRequest.of(0, 10));

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getText()).isEqualTo("Nice task");
        assertThat(result.getContent().get(0).getAuthorId()).isEqualTo(owner.getId());
        assertThat(result.getContent().get(0).getTaskId()).isEqualTo(task.getId());
    }

    @Test
    void testDeleteById() {
        Task task = createSampleTask("To be deleted");

        taskService.deleteTaskById(task.getId());

        assertThat(taskRepository.findById(task.getId())).isEmpty();
    }

    @Test
    void testIsOwner() {
        Task task = createSampleTask("OwnerCheck");
        boolean result = taskService.isOwner(task.getId(), owner.getEmail());

        assertThat(result).isTrue();
    }

    @Test
    void testIsPerformer() {
        Task task = createSampleTask("PerformerCheck");
        task.setPerformer(performer);
        taskRepository.save(task);

        boolean result = taskService.isPerformer(task.getId(), performer.getEmail());
        assertThat(result).isTrue();
    }

    private Task createSampleTask(String title) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("Описание");
        task.setStatus("IN_PROGRESS");
        task.setPriority("MEDIUM");
        task.setOwner(owner);
        task.setPerformer(performer);
        return taskRepository.save(task);
    }
}