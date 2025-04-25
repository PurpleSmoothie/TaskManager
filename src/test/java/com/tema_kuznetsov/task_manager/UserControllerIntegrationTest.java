package com.tema_kuznetsov.task_manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tema_kuznetsov.task_manager.dto.user.UserUpdateDto;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.models.enums.UserRole;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import com.tema_kuznetsov.task_manager.security.jwt.JwtService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.tema_kuznetsov.task_manager.models.enums.UserRole.MODERATOR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

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

    private AppUser adminUser;
    private AppUser moderatorUser;
    private AppUser simpleUser;
    private String adminToken;
    private String moderatorToken;
    private String userToken;

    @BeforeEach
    void setup() {
        adminUser = createUser("adminUser", "admin@example.com", "adminPass", UserRole.ADMIN);
        moderatorUser = createUser("moderator", "mod@example.com", "modPass", MODERATOR);
        simpleUser = createUser("user", "user@example.com", "userPass",UserRole.USER);

        adminToken = generateToken(adminUser);
        moderatorToken = generateToken(moderatorUser);
        userToken = generateToken(simpleUser);
    }

    private AppUser createUser(String login, String email, String password, String role) {
        AppUser user = new AppUser();
        user.setLogin(login);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    private String generateToken(AppUser user) {
        return "Bearer " + jwtService.generateToken(user.getEmail());
    }

    private UserUpdateDto createUpdateDto(String login, String email) {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setLogin(login);
        dto.setEmail(email);
        return dto;
    }

    @Test
    void shouldReturnNotFoundForNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/users/999999")
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNoToken() throws Exception {
        mockMvc.perform(get("/api/users/" + adminUser.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void shouldReturnUnauthorizedWhenInvalidToken() throws Exception {
        mockMvc.perform(get("/api/users/" + adminUser.getId())
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void shouldReturnForbiddenWhenNoAccessRights() throws Exception {
        mockMvc.perform(get("/api/users/" + adminUser.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/users/abc")
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturnUserByIdWhenAuthorizedAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/" + simpleUser.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(simpleUser.getId()))
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void shouldReturnUserByIdWhenAuthorizedAsModerator() throws Exception {
        mockMvc.perform(get("/api/users/" + simpleUser.getId())
                        .header("Authorization", moderatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(simpleUser.getId()))
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void shouldReturnNotFoundOnUpdateNonExistentUser() throws Exception {
        UserUpdateDto updateDto = createUpdateDto("newLogin", "new@example.com");

        mockMvc.perform(patch("/api/users/999999")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNoTokenOnUpdate() throws Exception {
        UserUpdateDto updateDto = createUpdateDto("newLogin", "new@example.com");

        mockMvc.perform(patch("/api/users/" + adminUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToUpdateAnotherUser() throws Exception {
        UserUpdateDto updateDto = createUpdateDto("tryUpdate", "try@example.com");

        mockMvc.perform(patch("/api/users/" + adminUser.getId())
                        .header("Authorization", userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidJson() throws Exception {
        String invalidJson = "{\"login\": \"newLogin\", \"email\": 12345678}";

        mockMvc.perform(patch("/api/users/" + adminUser.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturnBadRequestWhenInvalidPathId() throws Exception {
        UserUpdateDto updateDto = createUpdateDto("newLogin", "new@example.com");

        mockMvc.perform(patch("/api/users/abc")
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        UserUpdateDto updateDto = createUpdateDto("updatedLogin", "updated@example.com");

        mockMvc.perform(patch("/api/users/" + adminUser.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUser.getId()))
                .andExpect(jsonPath("$.login").value("updatedLogin"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void shouldIgnoreBlankLoginAndUpdateEmailOnly() throws Exception {
        UserUpdateDto updateDto = createUpdateDto("", "updated@example.com");

        mockMvc.perform(patch("/api/users/" + adminUser.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUser.getId()))
                .andExpect(jsonPath("$.login").value(adminUser.getLogin()))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void shouldUpdateOnlyLoginIfEmailIsBlank() throws Exception {
        UserUpdateDto updateDto = createUpdateDto("newLogin", "");

        mockMvc.perform(patch("/api/users/" + adminUser.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUser.getId()))
                .andExpect(jsonPath("$.login").value("newLogin"))
                .andExpect(jsonPath("$.email").value(adminUser.getEmail()));
    }

    @Test
    void shouldIgnoreBlankFieldsAndReturnUnchangedUser() throws Exception {
        UserUpdateDto updateDto = createUpdateDto("", "");

        mockMvc.perform(patch("/api/users/" + adminUser.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUser.getId()))
                .andExpect(jsonPath("$.login").value(adminUser.getLogin()))
                .andExpect(jsonPath("$.email").value(adminUser.getEmail()));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingToDuplicateEmail() throws Exception {
        UserUpdateDto updateDto = createUpdateDto(null, "user@example.com");

        mockMvc.perform(patch("/api/users/" + adminUser.getId())
                        .header("Authorization", adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(Matchers.containsString("Пользователь с таким email уже существует")));
    }

    @Test
    void shouldReturnUsersByLoginPartWhenAuthorizedAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("loginPart", "user")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].login").value("user"));
    }

    @Test
    void shouldReturnEmptyPageWhenNoUserMatchesLoginPart() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("loginPart", "not_existing_login")
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    void shouldReturnUnauthorizedIfNoTokenProvided() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("loginPart", "login"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void shouldReturnForbiddenIfUserWithoutPermissionsTriesSearch() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("loginPart", "user")
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnBadRequestIfLoginPartIsBlank() throws Exception {
        mockMvc.perform(get("/api/users/search")
                        .param("loginPart", "")
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldDeleteUserSuccessfullyWhenAuthorizedAsAdmin() throws Exception {
        mockMvc.perform(delete("/api/users/" + simpleUser.getId())
                        .header("Authorization", adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnUnauthorizedWhenDeletingWithoutToken() throws Exception {
        mockMvc.perform(delete("/api/users/" + adminUser.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void shouldReturnUnauthorizedWhenDeletingWithInvalidToken() throws Exception {
        mockMvc.perform(delete("/api/users/" + adminUser.getId())
                        .header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToDeleteAnotherUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + adminUser.getId())
                        .header("Authorization", userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnBadRequestWhenDeletingWithInvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/api/users/invalid_id")
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
        mockMvc.perform(delete("/api/users/999999")
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }

    @Test
    void shouldReturnUserByExactLoginWhenAuthorizedAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/search/exact")
                        .param("login", adminUser.getLogin())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUser.getId()))
                .andExpect(jsonPath("$.login").value(adminUser.getLogin()));
    }

    @Test
    void shouldReturnNotFoundWhenUserWithExactLoginNotExists() throws Exception {
        mockMvc.perform(get("/api/users/search/exact")
                        .param("login", "nonexistent_login")
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный логин"));
    }

    @Test
    void shouldReturnUserByEmailWhenAuthorizedAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/search/email")
                        .param("email", adminUser.getEmail())
                        .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(adminUser.getId()))
                .andExpect(jsonPath("$.email").value(adminUser.getEmail()));
    }

    @Test
    void shouldReturnNotFoundWhenUserWithEmailNotExists() throws Exception {
        mockMvc.perform(get("/api/users/search/email")
                        .param("email", "nonexistent@example.com")
                        .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный email"));
    }

    @Test
    void shouldReturnBadRequestWhenEmailFormatIsInvalid() throws Exception {
        mockMvc.perform(get("/api/users/search/email")
                        .param("email", "invalid-email")
                        .header("Authorization", adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Некорректный формат email"));
    }

    @Test
    void shouldReturnUnauthorizedWhenEmailSearchWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/search/email")
                        .param("email", adminUser.getEmail()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void searchUsersByRole_asAdmin_shouldReturnUsers() throws Exception {
        mockMvc.perform(get("/api/users/search/role")
                        .param("role", "USER")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].login").value("user"));
    }

    @Test
    void searchUsersByRole_asModerator_shouldReturnUsers() throws Exception {
        mockMvc.perform(get("/api/users/search/role")
                        .param("role", "USER")
                        .header(HttpHeaders.AUTHORIZATION, moderatorToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].login").value("user"));
    }

    @Test
    void searchUsersByRole_asUser_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/users/search/role")
                        .param("role", "USER")
                        .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void searchUsersByRole_withoutToken_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/users/search/role")
                        .param("role", "USER"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void searchUsersByRole_withInvalidRole_shouldReturn400() throws Exception {
        mockMvc.perform(get("/api/users/search/role")
                        .param("role", "UNKNOWN")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Роль должна быть одной из: ADMIN, USER, MODERATOR"));
    }

    @Test
    void shouldReturnAllUsersWhenAuthorizedAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/list")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToListUsers() throws Exception {
        mockMvc.perform(get("/api/users/list")
                        .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnUnauthorizedWhenNoTokenProvidedToListUsers() throws Exception {
        mockMvc.perform(get("/api/users/list"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Невалидный или отсутствующий JWT токен"));
    }

    @Test
    void shouldUpdatePasswordSuccessfullyWhenAuthorizedAsAdmin() throws Exception {
        String newPassword = "newPassword123";
        mockMvc.perform(patch("/api/users/" + simpleUser.getId() + "/password")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .param("password", newPassword))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdatePasswordSuccessfullyWhenAuthorizedAsSelf() throws Exception {
        String newPassword = "newPassword123";
        mockMvc.perform(patch("/api/users/" + simpleUser.getId() + "/password")
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .param("password", newPassword))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToUpdateAnotherUserPassword() throws Exception {
        String newPassword = "newPassword123";
        mockMvc.perform(patch("/api/users/" + adminUser.getId() + "/password")
                        .header(HttpHeaders.AUTHORIZATION, userToken)
                        .param("password", newPassword))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsInvalid() throws Exception {
        String invalidPassword = "short";
        mockMvc.perform(patch("/api/users/" + simpleUser.getId() + "/password")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .param("password", invalidPassword))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пароль должен содержать от 8 до 100 символов"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingPasswordForNonExistentUser() throws Exception {
        String newPassword = "newPassword123";
        mockMvc.perform(patch("/api/users/999999/password")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .param("password", newPassword))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }

    @Test
    void shouldUpdateUserRoleSuccessfully() throws Exception {
        mockMvc.perform(patch("/api/users/" + simpleUser.getId() + "/role")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .param("role", "MODERATOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MODERATOR"));
    }

    @Test
    void shouldReturnBadRequestWhenTryingToChangeOwnRole() throws Exception {
        mockMvc.perform(patch("/api/users/" + adminUser.getId() + "/role")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .param("role", "USER"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Некорректный запрос"));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidRoleProvided() throws Exception {
        mockMvc.perform(patch("/api/users/" + simpleUser.getId() + "/role")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .param("role", "INVALID_ROLE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Роль должна быть одной из: ADMIN, USER, MODERATOR"));
    }

    @Test
    void shouldReturnForbiddenWhenModeratorTriesToUpdateRole() throws Exception {
        mockMvc.perform(patch("/api/users/" + simpleUser.getId() + "/role")
                        .header(HttpHeaders.AUTHORIZATION, moderatorToken)
                        .param("role", "MODERATOR"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingRoleForNonExistentUser() throws Exception {
        mockMvc.perform(patch("/api/users/999999/role")
                        .header(HttpHeaders.AUTHORIZATION, adminToken)
                        .param("role", "MODERATOR"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }

    @Test
    void shouldDeleteUserByLoginSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/users/by-login/" + simpleUser.getLogin())
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestWhenTryingToDeleteSelfByLogin() throws Exception {
        mockMvc.perform(delete("/api/users/by-login/" + adminUser.getLogin())
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Некорректный запрос"));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentUserByLogin() throws Exception {
        mockMvc.perform(delete("/api/users/by-login/nonexistent_login")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный логин"));
    }

    @Test
    void shouldReturnForbiddenWhenModeratorTriesToDeleteUserByLogin() throws Exception {
        mockMvc.perform(delete("/api/users/by-login/" + simpleUser.getLogin())
                        .header(HttpHeaders.AUTHORIZATION, moderatorToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnCommentsByUserIdWhenAuthorizedAsAdmin() throws Exception {
        mockMvc.perform(get("/api/users/" + simpleUser.getId() + "/comments")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldReturnForbiddenWhenUserTriesToGetAnotherUsersComments() throws Exception {
        mockMvc.perform(get("/api/users/" + adminUser.getId() + "/comments")
                        .header(HttpHeaders.AUTHORIZATION, userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Доступ запрещен. У вас нет прав на выполнение этого действия."));
    }

    @Test
    void shouldReturnNotFoundWhenGettingCommentsForNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/users/999999/comments")
                        .header(HttpHeaders.AUTHORIZATION, adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Некорректный идентификатор"));
    }
}