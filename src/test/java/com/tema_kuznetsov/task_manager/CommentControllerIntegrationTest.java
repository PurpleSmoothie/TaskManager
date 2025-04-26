package com.tema_kuznetsov.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tema_kuznetsov.task_manager.dto.comment.CommentCreateDto;
import com.tema_kuznetsov.task_manager.dto.comment.CommentUpdateDto;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.models.Task;
import com.tema_kuznetsov.task_manager.repositories.CommentRepository;
import com.tema_kuznetsov.task_manager.repositories.TaskRepository;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import com.tema_kuznetsov.task_manager.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private Task task;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {

        AppUser admin = new AppUser();
        admin.setEmail("admin@mail.com");
        admin.setPassword("12345678LOL");
        admin.setLogin("Admin");
        admin.setRole("ADMIN");
        userRepository.save(admin);


        AppUser user = new AppUser();
        user.setEmail("user@mail.com");
        user.setPassword("12345678LOL");
        user.setLogin("User");
        user.setRole("USER");
        userRepository.save(user);


        AppUser moderator = new AppUser();
        moderator.setEmail("moderator@mail.com");
        moderator.setPassword("12345678LOL");
        moderator.setLogin("Moderator");
        moderator.setRole("MODERATOR");
        userRepository.save(moderator);


        task = new Task();
        task.setTitle("Тестовая задача");
        task.setDescription("Описание задачи");
        task.setStatus("IN_PROGRESS");
        task.setPriority("LOW");
        task.setOwner(admin);
        taskRepository.save(task);


        adminToken = "Bearer " + jwtService.generateToken(admin.getEmail());
        userToken = "Bearer " + jwtService.generateToken(user.getEmail());
        String moderatorToken = "Bearer " + jwtService.generateToken(moderator.getEmail());
    }

    @Test
    void shouldCreateCommentSuccessfully() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Отличная задача!");
        dto.setTask_id(task.getId());

        mockMvc.perform(post("/api/comments/create")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.text").value("Отличная задача!"))
                .andExpect(jsonPath("$.taskId").value(task.getId()));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingCommentWithInvalidData() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("");
        dto.setTask_id(task.getId());

        mockMvc.perform(post("/api/comments/create")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации"));
    }

    @Test
    void shouldReturnNotFoundWhenCreatingCommentWithNonExistentTask() throws Exception {
        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Отличная задача!");
        dto.setTask_id(999999L);

        mockMvc.perform(post("/api/comments/create")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }

    @Test
    void shouldReturnCommentByIdWhenAuthorized() throws Exception {

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Отличная задача!");
        dto.setTask_id(task.getId());

        String response = mockMvc.perform(post("/api/comments/create")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long commentId = objectMapper.readTree(response).get("id").asLong();


        mockMvc.perform(get("/api/comments/" + commentId)
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Отличная задача!"));
    }

    @Test
    void shouldReturnNotFoundWhenCommentByIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/comments/999999")
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }

    @Test
    void shouldDeleteCommentSuccessfullyWhenAuthorizedAsOwner() throws Exception {

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Отличная задача!");
        dto.setTask_id(task.getId());

        String response = mockMvc.perform(post("/api/comments/create")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long commentId = objectMapper.readTree(response).get("id").asLong();


        mockMvc.perform(delete("/api/comments/" + commentId)
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToDeleteCommentWithoutPermission() throws Exception {

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Отличная задача!");
        dto.setTask_id(task.getId());

        String response = mockMvc.perform(post("/api/comments/create")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long commentId = objectMapper.readTree(response).get("id").asLong();


        mockMvc.perform(delete("/api/comments/" + commentId)
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldUpdateCommentSuccessfullyWhenAuthorizedAsOwner() throws Exception {

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Отличная задача!");
        dto.setTask_id(task.getId());

        String response = mockMvc.perform(post("/api/comments/create")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long commentId = objectMapper.readTree(response).get("id").asLong();

        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setText("Обновлённый текст комментария!");

        mockMvc.perform(patch("/api/comments/" + commentId)
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Обновлённый текст комментария!"));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToUpdateCommentWithoutPermission() throws Exception {

        CommentCreateDto dto = new CommentCreateDto();
        dto.setText("Отличная задача!");
        dto.setTask_id(task.getId());

        String response = mockMvc.perform(post("/api/comments/create")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long commentId = objectMapper.readTree(response).get("id").asLong();

        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setText("Обновлённый текст комментария!");

        mockMvc.perform(patch("/api/comments/" + commentId)
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentComment() throws Exception {
        CommentUpdateDto updateDto = new CommentUpdateDto();
        updateDto.setText("Обновлённый текст комментария!");

        mockMvc.perform(patch("/api/comments/999999")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }
}
