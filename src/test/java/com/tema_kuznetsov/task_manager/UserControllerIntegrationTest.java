//package com.tema_kuznetsov.task_manager;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tema_kuznetsov.task_manager.dto.user.UserUpdateDto;
//import com.tema_kuznetsov.task_manager.model.AppUser;
//import com.tema_kuznetsov.task_manager.model.enums.UserRole;
//import com.tema_kuznetsov.task_manager.repository.UserRepository;
//import com.tema_kuznetsov.task_manager.security.jwt.JwtService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@AutoConfigureMockMvc
//@Transactional
//public class UserControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private AppUser adminUser;
//    private String adminToken;
//
//    @Transactional
//    @BeforeEach
//    void setup() {
//        String password = passwordEncoder.encode("adminPass");
//
//        adminUser = new AppUser();
//        adminUser.setLogin("adminUser");
//        adminUser.setEmail("admin@example.com");
//        adminUser.setPassword(password);
//        adminUser.setRole(UserRole.ADMIN);
//
//        userRepository.save(adminUser);
//
//        adminToken = "Bearer " + jwtService.generateToken(adminUser.getEmail());
//    }
//
//    @Test
//    void shouldFindUserById() throws Exception {
//        mockMvc.perform(get("/api/users/" + adminUser.getId())
//                        .header("Authorization", adminToken))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.login").value("adminUser"));
//    }
//
//    @Test
//    void shouldUpdateUser() throws Exception {
//        UserUpdateDto updateDto = new UserUpdateDto();
//        updateDto.setLogin("updatedLogin");
//        updateDto.setEmail("updated@example.com");
//
//        mockMvc.perform(patch("/api/users/" + adminUser.getId())
//                        .header("Authorization", adminToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.login").value("updatedLogin"))
//                .andExpect(jsonPath("$.email").value("updated@example.com"));
//    }
//
//    @Test
//    void shouldDeleteUserById() throws Exception {
//        mockMvc.perform(delete("/api/users/" + adminUser.getId())
//                        .header("Authorization", adminToken))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void shouldFindUserByExactLogin() throws Exception {
//        mockMvc.perform(get("/api/users/search/exact")
//                        .header("Authorization", adminToken)
//                        .param("login", "adminUser"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.login").value("adminUser"));
//    }
//
//    @Test
//    void shouldFindUserByEmail() throws Exception {
//        mockMvc.perform(get("/api/users/search/email")
//                        .header("Authorization", adminToken)
//                        .param("email", "admin@example.com"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.email").value("admin@example.com"));
//    }
//
//}