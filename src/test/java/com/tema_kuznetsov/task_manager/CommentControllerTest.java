package com.tema_kuznetsov.task_manager;

import com.tema_kuznetsov.task_manager.controller.CommentController;
import com.tema_kuznetsov.task_manager.dto.comment.CommentCreateDto;
import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.model.AppUser;
import com.tema_kuznetsov.task_manager.model.Comment;
import com.tema_kuznetsov.task_manager.model.Task;
import com.tema_kuznetsov.task_manager.repository.CommentRepository;
import com.tema_kuznetsov.task_manager.repository.TaskRepository;
import com.tema_kuznetsov.task_manager.repository.UserRepository;
import com.tema_kuznetsov.task_manager.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private AppUser user;
    private AppUser performer;
    private Task task;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();

        user = new AppUser();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setPassword("password");

        performer = new AppUser();
        performer.setId(2L);
        performer.setEmail("performer@example.com");
        performer.setLogin("performerlogin");
        performer.setPassword("password");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Task description");
        task.setStatus("OPEN");
        task.setPriority("HIGH");
        task.setOwner(user);
        task.setPerformer(performer);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test Comment");
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setTask(task);

        when(commentRepository.findAll()).thenReturn(Arrays.asList(comment));
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(performer));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    }

    @Test
    public void testGetCommentById() throws Exception {
        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);

        when(commentService.findCommentById(1L)).thenReturn(commentResponseDto);

        mockMvc.perform(get("/api/comments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test Comment"))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.taskId").value(1L))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    public void testCreateComment() throws Exception {
        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);

        when(commentService.createComment(any(CommentCreateDto.class))).thenReturn(commentResponseDto);

        mockMvc.perform(post("/api/comments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"text\": \"New Comment\", \"task_id\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test Comment"))
                .andExpect(jsonPath("$.authorId").value(1L))
                .andExpect(jsonPath("$.taskId").value(1L))
                .andExpect(jsonPath("$.createdAt").exists());
    }
}