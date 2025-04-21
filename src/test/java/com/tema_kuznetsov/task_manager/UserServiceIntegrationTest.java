//package com.tema_kuznetsov.task_manager;
//
//
//import com.tema_kuznetsov.task_manager.dto.user.UserCreateDto;
//import com.tema_kuznetsov.task_manager.dto.user.UserResponseDto;
//import com.tema_kuznetsov.task_manager.model.AppUser;
//import com.tema_kuznetsov.task_manager.model.enums.UserRole;
//import com.tema_kuznetsov.task_manager.repository.UserRepository;
//import com.tema_kuznetsov.task_manager.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@Transactional
//public class UserServiceIntegrationTest {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private AppUser testUser;
//
//    @Transactional
//    @BeforeEach
//    public void setUp() {
//        // Генерация пароля и кодирование через BCrypt
//        String rawPassword = "password123";
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//
//        // Создание пользователя с зашифрованным паролем
//        testUser = new AppUser();
//        testUser.setLogin("testUserLogin");
//        testUser.setEmail("test@example.com");
//        testUser.setPassword(encodedPassword);
//        testUser.setRole(UserRole.USER);
//        userRepository.save(testUser);
//    }
//
//    @Test
//    public void shouldFindUserById() {
//        // Тестирование поиска пользователя по ID
//        UserResponseDto userResponse = userService.findUserById(testUser.getId());
//        assertThat(userResponse).isNotNull();
//        assertThat(userResponse.getLogin()).isEqualTo(testUser.getLogin());
//    }
//
//    @Test
//    public void shouldFindUserByExactLogin() {
//        // Тестирование поиска пользователя по точному имени
//        UserResponseDto userResponse = userService.findUserByExactLogin(testUser.getLogin()); // Здесь нужно использовать правильный логин
//        assertThat(userResponse).isNotNull();
//        assertThat(userResponse.getLogin()).isEqualTo(testUser.getLogin());
//    }
//
//    @Test
//    public void shouldFindUserByEmail() {
//        // Тестирование поиска пользователя по email
//        UserResponseDto userResponse = userService.findUserByEmail(testUser.getEmail());
//        assertThat(userResponse).isNotNull();
//        assertThat(userResponse.getEmail()).isEqualTo(testUser.getEmail());
//    }
//
//    @Test
//    public void shouldUpdateUserPassword() {
//        // Новый пароль
//        String newPassword = "new_password123";
//
//        // Старый зашифрованный пароль
//        String oldPassword = testUser.getPassword();
//
//        // Обновляем пароль
//        userService.updateUserPasswordById(testUser.getId(), newPassword);
//
//        // Загружаем обновленного пользователя
//        AppUser updatedUser = userRepository.findById(testUser.getId()).get();
//
//        // Проверяем, что новый пароль зашифрован правильно
//        assertThat(passwordEncoder.matches(newPassword, updatedUser.getPassword())).isTrue(); // Новый пароль должен быть правильным
//        assertThat(passwordEncoder.matches(oldPassword, updatedUser.getPassword())).isFalse(); // Старый пароль не должен совпадать
//    }
//
//    @Test
//    public void shouldFindUsersByRole() {
//        // Тестирование поиска пользователей по роли
//        Page<UserResponseDto> usersPage = userService.findUsersByRole("USER", PageRequest.of(0, 10));
//        assertThat(usersPage).isNotEmpty();
//        assertThat(usersPage.getContent().get(0).getRole()).isEqualTo("USER");
//    }
//
//    @Test
//    public void shouldDeleteUserByLogin() {
//        // Тестирование удаления пользователя по логину
//        userService.deleteUserByLogin(testUser.getLogin());
//        assertThat(userRepository.existsByLogin(testUser.getLogin())).isFalse();
//    }
//
//    @Test
//    public void shouldDeleteUserById() {
//        // Тестирование удаления пользователя по ID
//        userService.deleteUserById(testUser.getId());
//        assertThat(userRepository.existsById(testUser.getId())).isFalse();
//    }
//
//    @Test
//    public void shouldFindUsersByLoginPart() {
//        // Тестирование поиска пользователей по части имени
//        Page<UserResponseDto> usersPage = userService.findUserByLoginContaining("test", PageRequest.of(0, 10));
//        assertThat(usersPage).isNotEmpty();
//        assertThat(usersPage.getContent().get(0).getLogin()).contains("test");
//    }
//
//}