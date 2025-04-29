package com.tema_kuznetsov.task_manager.services;

import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserUpdateDto;
import com.tema_kuznetsov.task_manager.exceptions.userException.SelfDeletionException;
import com.tema_kuznetsov.task_manager.exceptions.userException.UserIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.IncorrectEmailFormatException;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.UserEmailNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.loginException.UserLoginNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.roleException.SelfRoleChangeException;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import com.tema_kuznetsov.task_manager.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

/**
 * Сервис для работы с пользователями.
 * Предоставляет методы для поиска, обновления и удаления пользователей.
 */
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO с информацией о пользователе
     * @throws UserIdNotFoundException если пользователь не найден
     */
    public UserResponseDto findUserById(Long id) {
        AppUser appUser = getUserByIdOrThrow(id);
        return new UserResponseDto(appUser);
    }

    /**
     * Находит пользователя по точному совпадению логина.
     *
     * @param login логин пользователя
     * @return DTO с информацией о пользователе
     * @throws UserLoginNotFoundException если пользователь не найден
     */
    public UserResponseDto findUserByExactLogin(String login) {
        AppUser appUser = userRepository.findUserByLogin(login)
                .orElseThrow(() -> new UserLoginNotFoundException(login));
        return new UserResponseDto(appUser);
    }

    /**
     * Находит пользователя по email.
     *
     * @param email email пользователя
     * @return DTO с информацией о пользователе
     * @throws UserEmailNotFoundException если пользователь не найден
     */
    public UserResponseDto findUserByEmail(String email) {
        AppUser appUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserEmailNotFoundException(email));
        return new UserResponseDto(appUser);
    }

    /**
     * Находит пользователей, содержащих в логине указанную подстроку.
     *
     * @param loginPart подстрока для поиска в логине пользователя
     * @param pageable объект для пагинации
     * @return список пользователей в виде страниц
     */
    public Page<UserResponseDto> findUserByLoginContaining(String loginPart, Pageable pageable) {
        Page<AppUser> appUsers = userRepository.findUserByLoginContaining(loginPart, pageable);
        return convertToDtoList(appUsers);
    }

    /**
     * Получает всех пользователей.
     *
     * @param pageable объект для пагинации
     * @return список всех пользователей в виде страниц
     */
    public Page<UserResponseDto> findAllUsers(Pageable pageable) {
        return convertToDtoList(userRepository.findAll(pageable));
    }

    /**
     * Обновляет данные пользователя по его идентификатору.
     * Обновляются только те поля, которые указаны в DTO и не являются пустыми.
     *
     * @param id идентификатор пользователя
     * @param dto объект с обновленными данными пользователя
     * @return DTO с информацией об обновленном пользователе
     * @throws IncorrectEmailFormatException если email имеет неверный формат
     */
    @Transactional
    public UserResponseDto updateUserById(Long id, UserUpdateDto dto) {
        AppUser appUser = getUserByIdOrThrow(id);

        String email = dto.getEmail();
        if (email != null && !email.isBlank()) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new IncorrectEmailFormatException();
            }
        }

        if (dto.getLogin() != null && !dto.getLogin().isBlank()) {
            appUser.setLogin(dto.getLogin());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            appUser.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(dto.getPassword());
            appUser.setPassword(encodedPassword);
        }

        return new UserResponseDto(appUser);
    }

    /**
     * Обновляет пароль пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @param password новый пароль
     * @return DTO с информацией об обновленном пользователе
     */
    @Transactional
    public UserResponseDto updateUserPasswordById(Long id, String password) {
        AppUser appUser = getUserByIdOrThrow(id);

        String encodedPassword = passwordEncoder.encode(password);
        appUser.setPassword(encodedPassword);

        return new UserResponseDto(appUser);
    }

    /**
     * Обновляет роль пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @param role новая роль
     * @return DTO с информацией об обновленном пользователе
     * @throws SelfRoleChangeException если пользователь пытается изменить свою собственную роль
     */
    @Transactional
    public UserResponseDto updateUserRoleById(Long id, String role) {
        AppUser appUser = getUserByIdOrThrow(id);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId.equals(appUser.getId())) {
            throw new SelfRoleChangeException();
        }

        appUser.setRole(role);
        return new UserResponseDto(appUser);
    }

    /**
     * Удаляет пользователя по его логину.
     *
     * @param login логин пользователя
     * @throws UserLoginNotFoundException если пользователь не найден
     * @throws SelfDeletionException если пользователь пытается удалить самого себя
     */
    @Transactional
    public void deleteUserByLogin(String login) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!userRepository.existsByLogin(login)) {
            throw new UserLoginNotFoundException(login);
        }

        AppUser user = getUserByIdOrThrow(currentUserId);
        if (login.equals(user.getLogin())) {
            throw new SelfDeletionException();
        }

        userRepository.deleteUserByLogin(login);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @throws UserIdNotFoundException если пользователь не найден
     * @throws SelfDeletionException если пользователь пытается удалить самого себя
     */
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserIdNotFoundException(id);
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId.equals(id)) {
            throw new SelfDeletionException();
        }

        userRepository.deleteById(id);
    }

    /**
     * Находит пользователей по роли.
     *
     * @param role роль для поиска
     * @param pageable объект для пагинации
     * @return список пользователей в виде страниц
     */
    public Page<UserResponseDto> findUsersByRole(String role, Pageable pageable) {
        return convertToDtoList(userRepository.findUsersByRole(role, pageable));
    }

    /**
     * Получает комментарии указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @param pageable объект для пагинации
     * @return список комментариев в виде страниц
     */
    public Page<CommentResponseDto> findCommentsByUserId(Long userId, Pageable pageable) {
        AppUser appUser = getUserByIdOrThrow(userId);
        return commentService.getCommentsForUser(userId, pageable);
    }

    /**
     * Преобразует страницу пользователей в страницу DTO.
     *
     * @param appUsers страница пользователей
     * @return страница DTO пользователей
     */
    private Page<UserResponseDto> convertToDtoList(Page<AppUser> appUsers) {
        return appUsers.map(UserResponseDto::new);
    }

    /**
     * Находит пользователя по его идентификатору.
     * Если пользователь не найден, выбрасывает исключение {@link UserIdNotFoundException}.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     * @throws UserIdNotFoundException если пользователь не найден
     */
    private AppUser getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(id));
    }
}