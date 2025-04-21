//package com.tema_kuznetsov.task_manager;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.tema_kuznetsov.task_manager.dto.task.TaskCreateDto;
//import com.tema_kuznetsov.task_manager.model.AppUser;
//import com.tema_kuznetsov.task_manager.repository.UserRepository;
//import com.tema_kuznetsov.task_manager.security.jwt.JwtService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//class TaskControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private JwtService jwtService;
//
//    private AppUser owner;
//    private AppUser performer;
//    private String ownerToken;
//
//    @BeforeEach
//    void setUp() {
//        // Создаём владельца (админа)
//        owner = new AppUser();
//        owner.setEmail("owner@mail.com");
//        owner.setPassword("12345678LOL");
//        owner.setLogin("Owner");
//        owner.setRole("ADMIN");
//        userRepository.save(owner);
//
//        // Создаём исполнителя
//        performer = new AppUser();
//        performer.setEmail("performer@mail.com");
//        performer.setPassword("12345678LOL");
//        performer.setLogin("Performer");
//        performer.setRole("USER");
//        userRepository.save(performer);
//
//        // Генерируем JWT токен для владельца
//        ownerToken = "Bearer " + jwtService.generateToken(owner.getEmail());
//    }
//
//    @Test
//    void testCreateTask() throws Exception {
//        TaskCreateDto dto = new TaskCreateDto();
//        dto.setTitle("Тестовая задача");
//        dto.setDescription("Описание задачи");
//        dto.setStatus("IN_PROGRESS");
//        dto.setPriority("LOW");
//        dto.setPerformerId(performer.getId());
//
//        mockMvc.perform(post("/api/tasks/create")
//                        .header("Authorization", ownerToken)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.title").value("Тестовая задача"))
//                .andExpect(jsonPath("$.performerId").value(performer.getId()));
//    }
//
//    @Test
//    void testFindTaskByTitleContaining() throws Exception {
//        // Сначала создаём задачу напрямую через контроллер
//        testCreateTask();
//
//        mockMvc.perform(get("/api/tasks/search")
//                        .header("Authorization", ownerToken)
//                        .param("titlePart", "Тест"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].title").value("Тестовая задача"));
//    }
//
//    @Test
//    void testDeleteTaskById() throws Exception {
//        // Сначала создаём задачу напрямую
//        TaskCreateDto dto = new TaskCreateDto();
//        dto.setTitle("Удаляемая задача");
//        dto.setDescription("Удалить меня");
//        dto.setStatus("IN_PROGRESS");
//        dto.setPriority("LOW");
//        dto.setPerformerId(performer.getId());
//
//        String response = mockMvc.perform(post("/api/tasks/create")
//                        .header("Authorization", ownerToken)
//                        .contentType("application/json")
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isCreated())
//                .andReturn().getResponse().getContentAsString();
//
//        Long taskId = objectMapper.readTree(response).get("id").asLong();
//
//        // Удаляем её
//        mockMvc.perform(delete("/api/tasks/" + taskId)
//                        .header("Authorization", ownerToken))
//                .andExpect(status().isNoContent());
//    }
//}