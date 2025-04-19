package com.tema_kuznetsov.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tema_kuznetsov.task_manager.controller.UserController;
import com.tema_kuznetsov.task_manager.dto.user.UserResponseDto;
import com.tema_kuznetsov.task_manager.model.AppUser;
import com.tema_kuznetsov.task_manager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserResponseDto user;

    @BeforeEach
    void setUp() {
        AppUser appUser = new AppUser();
        appUser.setId(1L);
        appUser.setLogin("admin");
        appUser.setEmail("admin@localhost.com");
        appUser.setRole("ADMIN");
        appUser.setCreatedAt(LocalDateTime.now());

        user = new UserResponseDto(appUser);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFindUserById() throws Exception {
        Mockito.when(userService.findUserById(anyLong())).thenReturn(user);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.login").value("admin"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFindUserByEmail() throws Exception {
        Mockito.when(userService.findUserByEmail(eq("admin@localhost.com"))).thenReturn(user);

        mockMvc.perform(get("/api/users/search/email")
                        .param("email", "admin@localhost.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@localhost.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testFindUserByExactLogin() throws Exception {
        Mockito.when(userService.findUserByExactLogin(eq("admin"))).thenReturn(user);

        mockMvc.perform(get("/api/users/search/exact")
                        .param("login", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("admin"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteUserById() throws Exception {
        Long userId = 1L;

        // Отключаем CSRF для теста
        mockMvc.perform(delete("/api/users/{id}", userId)
                        .with(csrf()))  // Добавляем csrf
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserById() throws Exception {
        Long userId = 1L;

        when(userService.findUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.login").value(user.getLogin()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.role").value(user.getRole()));
    }
}