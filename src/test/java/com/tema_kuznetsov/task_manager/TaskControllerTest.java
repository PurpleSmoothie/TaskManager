package com.tema_kuznetsov.task_manager;

import com.tema_kuznetsov.task_manager.controller.TaskController;
import com.tema_kuznetsov.task_manager.dto.task.TaskResponseDto;
import com.tema_kuznetsov.task_manager.model.Task;
import com.tema_kuznetsov.task_manager.repository.TaskRepository;
import com.tema_kuznetsov.task_manager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    private TaskResponseDto task;

    @BeforeEach
    void setUp() {
        Task taskModel = new Task();
        taskModel.setId(1L);
        taskModel.setTitle("Задача по проекту");
        taskModel.setCreatedAt(LocalDateTime.now());

        task = new TaskResponseDto(taskModel);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFindTaskById() throws Exception {
        Mockito.when(taskService.findTaskById(anyLong())).thenReturn(task);

        mockMvc.perform(get("/api/tasks/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Задача по проекту"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateTask() throws Exception {
        Task taskModel = new Task();
        taskModel.setId(2L);
        taskModel.setTitle("Новая задача");
        taskModel.setCreatedAt(LocalDateTime.now());

        Mockito.when(taskService.createTask(any())).thenReturn(taskModel);

        String taskJson = """
        {
            "title": "Новая задача",
            "description": "Описание новой задачи",
            "status": "OPEN",
            "priority": "HIGH"
        }
    """;

        mockMvc.perform(post("/api/tasks/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.title").value("Новая задача"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteTaskById() throws Exception {
        Long taskId = 1L;

        mockMvc.perform(delete("/api/tasks/{id}", taskId)
                        .with(csrf()))  // Добавляем CSRF для DELETE-запросов
                .andExpect(status().isNoContent());

        Mockito.verify(taskService, Mockito.times(1)).deleteTaskById(taskId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateTask() throws Exception {
        Task updatedTaskModel = new Task();
        updatedTaskModel.setId(1L);
        updatedTaskModel.setTitle("Обновлённая задача");
        updatedTaskModel.setCreatedAt(LocalDateTime.now());

        TaskResponseDto updatedTaskDto = new TaskResponseDto(updatedTaskModel);

        Mockito.when(taskService.updateTaskById(eq(1L), any())).thenReturn(updatedTaskDto);

        mockMvc.perform(patch("/api/tasks/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Обновлённая задача\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Обновлённая задача"));
    }
}