package com.tema_kuznetsov.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tema_kuznetsov.task_manager.dto.task.TaskCreateDto;
import com.tema_kuznetsov.task_manager.dto.task.TaskUpdateDto;
import com.tema_kuznetsov.task_manager.models.AppUser;
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
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    private AppUser performer;
    private String ownerToken;

    @BeforeEach
    void setUp() {

        AppUser owner = new AppUser();
        owner.setEmail("owner@mail.com");
        owner.setPassword("12345678LOL");
        owner.setLogin("Owner");
        owner.setRole("ADMIN");
        userRepository.save(owner);

        performer = new AppUser();
        performer.setEmail("performer@mail.com");
        performer.setPassword("12345678LOL");
        performer.setLogin("Performer");
        performer.setRole("USER");
        userRepository.save(performer);

        ownerToken = "Bearer " + jwtService.generateToken(owner.getEmail());
        String performerToken = "Bearer " + jwtService.generateToken(performer.getEmail());
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Тестовая задача"))
                .andExpect(jsonPath("$.performerId").value(performer.getId()));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingTaskWithInvalidData() throws Exception {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации"));
    }

    @Test
    void shouldReturnTaskByIdWhenAuthorized() throws Exception {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long taskId = objectMapper.readTree(response).get("id").asLong();


        mockMvc.perform(get("/api/tasks/" + taskId)
                        .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Тестовая задача"));
    }

    @Test
    void shouldReturnNotFoundWhenTaskByIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/tasks/999999")
                        .header("Authorization", ownerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }

    @Test
    void shouldReturnTaskByExactTitleWhenAuthorized() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        mockMvc.perform(get("/api/tasks/search/exact")
                        .param("title", "Тестовая задача")
                        .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Тестовая задача"));
    }

    @Test
    void shouldReturnNotFoundWhenTaskByExactTitleDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/tasks/search/exact")
                        .param("title", "Несуществующая задача")
                        .header("Authorization", ownerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректное название"));
    }

    @Test
    void shouldReturnTasksByTitleContainingWhenAuthorized() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        mockMvc.perform(get("/api/tasks/search")
                        .param("titlePart", "Тест")
                        .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Тестовая задача"));
    }

    @Test
    void shouldReturnNotFoundWhenTasksByTitleContainingDoNotExist() throws Exception {
        mockMvc.perform(get("/api/tasks/search")
                        .param("titlePart", "Несуществующая")
                        .header("Authorization", ownerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректное название"));
    }

    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long taskId = objectMapper.readTree(response).get("id").asLong();


        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle("Обновлённая задача");
        updateDto.setDescription("Новое описание");

        mockMvc.perform(patch("/api/tasks/" + taskId)
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Обновлённая задача"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentTask() throws Exception {
        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle("Обновлённая задача");
        updateDto.setDescription("Новое описание");

        mockMvc.perform(patch("/api/tasks/999999")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long taskId = objectMapper.readTree(response).get("id").asLong();


        mockMvc.perform(delete("/api/tasks/" + taskId)
                        .header("Authorization", ownerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/999999")
                        .header("Authorization", ownerToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }

    @Test
    void shouldUpdateTaskStatusSuccessfully() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long taskId = objectMapper.readTree(response).get("id").asLong();


        mockMvc.perform(patch("/api/tasks/" + taskId + "/status")
                        .header("Authorization", ownerToken)
                        .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingTaskStatusWithInvalidValue() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long taskId = objectMapper.readTree(response).get("id").asLong();


        mockMvc.perform(patch("/api/tasks/" + taskId + "/status")
                        .header("Authorization", ownerToken)
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Допустимые статусы: OPEN, IN_PROGRESS, COMPLETED, CANCELLED"));
    }

    @Test
    void shouldUpdateTaskPrioritySuccessfully() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long taskId = objectMapper.readTree(response).get("id").asLong();


        mockMvc.perform(patch("/api/tasks/" + taskId + "/priority")
                        .header("Authorization", ownerToken)
                        .param("priority", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingTaskPriorityWithInvalidValue() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long taskId = objectMapper.readTree(response).get("id").asLong();


        mockMvc.perform(patch("/api/tasks/" + taskId + "/priority")
                        .header("Authorization", ownerToken)
                        .param("priority", "INVALID_PRIORITY"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Допустимые приоритеты: LOW, MEDIUM, HIGH, CRITICAL"));
    }

    @Test
    void shouldUpdateTaskPerformerSuccessfully() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();


        AppUser newPerformer = new AppUser();
        newPerformer.setEmail("new_performer@mail.com");
        newPerformer.setPassword("12345678LOL");
        newPerformer.setLogin("NewPerformer");
        newPerformer.setRole("USER");
        userRepository.save(newPerformer);


        mockMvc.perform(patch("/api/tasks/" + taskId + "/performer")
                        .header("Authorization", ownerToken)
                        .param("performerId", newPerformer.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.performerId").value(newPerformer.getId()));
    }


    @Test
    void shouldReturnForbiddenWhenUserTriesToUpdateTaskWithoutPermission() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();


        AppUser user = new AppUser();
        user.setEmail("user@mail.com");
        user.setPassword("12345678LOL");
        user.setLogin("User");
        user.setRole("USER");
        userRepository.save(user);

        String userToken = "Bearer " + jwtService.generateToken(user.getEmail());

        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle("Обновлённая задача");
        updateDto.setDescription("Новое описание");

        mockMvc.perform(patch("/api/tasks/" + taskId)
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToDeleteTaskWithoutPermission() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();


        AppUser user = new AppUser();
        user.setEmail("user@mail.com");
        user.setPassword("12345678LOL");
        user.setLogin("User");
        user.setRole("USER");
        userRepository.save(user);

        String userToken = "Bearer " + jwtService.generateToken(user.getEmail());

        mockMvc.perform(delete("/api/tasks/" + taskId)
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToUpdateTaskStatusWithoutPermission() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();


        AppUser user = new AppUser();
        user.setEmail("user@mail.com");
        user.setPassword("12345678LOL");
        user.setLogin("User");
        user.setRole("USER");
        userRepository.save(user);

        String userToken = "Bearer " + jwtService.generateToken(user.getEmail());

        mockMvc.perform(patch("/api/tasks/" + taskId + "/status")
                        .header("Authorization", userToken)
                        .param("status", "DONE"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToUpdateTaskPriorityWithoutPermission() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();


        AppUser user = new AppUser();
        user.setEmail("user@mail.com");
        user.setPassword("12345678LOL");
        user.setLogin("User");
        user.setRole("USER");
        userRepository.save(user);

        String userToken = "Bearer " + jwtService.generateToken(user.getEmail());

        mockMvc.perform(patch("/api/tasks/" + taskId + "/priority")
                        .header("Authorization", userToken)
                        .param("priority", "HIGH"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnTasksByStatusWhenAuthorizedAsAdmin() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        mockMvc.perform(get("/api/tasks/search/status")
                        .param("status", "IN_PROGRESS")
                        .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("IN_PROGRESS"));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToSearchTasksByStatusWithoutPermission() throws Exception {

        AppUser user = new AppUser();
        user.setEmail("user@mail.com");
        user.setPassword("12345678LOL");
        user.setLogin("User");
        user.setRole("USER");
        userRepository.save(user);

        String userToken = "Bearer " + jwtService.generateToken(user.getEmail());

        mockMvc.perform(get("/api/tasks/search/status")
                        .param("status", "IN_PROGRESS")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnTasksByPriorityWhenAuthorizedAsAdmin() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("HIGH");
        dto.setPerformerId(performer.getId());

        mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        mockMvc.perform(get("/api/tasks/search/priority")
                        .param("priority", "HIGH")
                        .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].priority").value("HIGH"));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToSearchTasksByPriorityWithoutPermission() throws Exception {

        AppUser user = new AppUser();
        user.setEmail("user@mail.com");
        user.setPassword("12345678LOL");
        user.setLogin("User");
        user.setRole("USER");
        userRepository.save(user);

        String userToken = "Bearer " + jwtService.generateToken(user.getEmail());

        mockMvc.perform(get("/api/tasks/search/priority")
                        .param("priority", "HIGH")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnCommentsByTaskIdWhenAuthorizedAsAdmin() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();


        mockMvc.perform(get("/api/tasks/" + taskId + "/comments")
                        .header("Authorization", ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToGetTaskCommentsWithoutPermission() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        String response = mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();

        AppUser user = new AppUser();
        user.setEmail("user@mail.com");
        user.setPassword("12345678LOL");
        user.setLogin("User");
        user.setRole("USER");
        userRepository.save(user);

        String userToken = "Bearer " + jwtService.generateToken(user.getEmail());

        mockMvc.perform(get("/api/tasks/" + taskId + "/comments")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldDeleteTaskByTitleSuccessfully() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        mockMvc.perform(delete("/api/tasks/by-title/Тестовая задача")
                        .header("Authorization", ownerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToDeleteTaskByTitleWithoutPermission() throws Exception {

        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Тестовая задача");
        dto.setDescription("Описание задачи");
        dto.setStatus("IN_PROGRESS");
        dto.setPriority("LOW");
        dto.setPerformerId(performer.getId());

        mockMvc.perform(post("/api/tasks/create")
                        .header("Authorization", ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        AppUser user = new AppUser();
        user.setEmail("user@mail.com");
        user.setPassword("12345678LOL");
        user.setLogin("User");
        user.setRole("USER");
        userRepository.save(user);

        String userToken = "Bearer " + jwtService.generateToken(user.getEmail());

        mockMvc.perform(delete("/api/tasks/by-title/Тестовая задача")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }
}