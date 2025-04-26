package com.tema_kuznetsov.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tema_kuznetsov.task_manager.dto.security.JwtRequestDto;
import com.tema_kuznetsov.task_manager.dto.user.UserCreateDto;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import com.tema_kuznetsov.task_manager.security.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthenticationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private AppUser user;

    @BeforeEach
    void setUp() {

        user = new AppUser();
        user.setEmail("user@mail.com");
        user.setPassword(passwordEncoder.encode("12345678LOL"));
        user.setLogin("User");
        user.setRole("USER");
        userRepository.save(user);
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        JwtRequestDto loginRequest = new JwtRequestDto();
        loginRequest.setEmail("user@mail.com");
        loginRequest.setPassword("12345678LOL");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginWithInvalidCredentials() throws Exception {
        JwtRequestDto loginRequest = new JwtRequestDto();
        loginRequest.setEmail("user@mail.com");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Неверный логин или пароль"));
    }

    @Test
    void shouldReturnUnauthorizedWhenLoginWithNonExistentUser() throws Exception {
        JwtRequestDto loginRequest = new JwtRequestDto();
        loginRequest.setEmail("nonexistent@mail.com");
        loginRequest.setPassword("12345678LOL");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Неверный логин или пароль"));
    }

    @Test
    void shouldValidateTokenSuccessfully() throws Exception {

        String token = jwtService.generateToken(user.getEmail());

        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldReturnUnauthorizedWhenValidateTokenWithoutHeader() throws Exception {
        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void shouldReturnUnauthorizedWhenValidateTokenWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/auth/validate")
                        .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("newuser@mail.com");
        dto.setPassword("12345678LOL");
        dto.setLogin("NewUser");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@mail.com"))
                .andExpect(jsonPath("$.login").value("NewUser"));
    }

    @Test
    void shouldReturnBadRequestWhenRegisterUserWithInvalidData() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("");
        dto.setPassword("12345678LOL");
        dto.setLogin("NewUser");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации"));
    }

    @Test
    void shouldReturnBadRequestWhenRegisterUserWithExistingEmail() throws Exception {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("user@mail.com");
        dto.setPassword("12345678LOL");
        dto.setLogin("NewUser");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Ошибка валидации"));
    }
}