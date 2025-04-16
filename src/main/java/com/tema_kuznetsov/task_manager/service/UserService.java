package com.tema_kuznetsov.task_manager.service;

import com.tema_kuznetsov.task_manager.dto.comment.CommentResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserResponseDto;
import com.tema_kuznetsov.task_manager.dto.user.UserUpdateDto;
import com.tema_kuznetsov.task_manager.exception.userException.InvalidPasswordException;
import com.tema_kuznetsov.task_manager.exception.userException.UserIdNotFoundException;
import com.tema_kuznetsov.task_manager.exception.userException.emailException.IncorrectEmailFormatException;
import com.tema_kuznetsov.task_manager.exception.userException.emailException.UserEmailNotFoundException;
import com.tema_kuznetsov.task_manager.exception.userException.loginException.UserLoginNotFoundException;
import com.tema_kuznetsov.task_manager.exception.userException.roleException.IncorrectRoleTitleException;
import com.tema_kuznetsov.task_manager.model.AppUser;
import com.tema_kuznetsov.task_manager.model.enums.UserRole;
import com.tema_kuznetsov.task_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor // конструктор только для файнал полей
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;



    //2. Получение пользователя по ID
    public UserResponseDto findUserById(Long id) {
        AppUser app_user = getUserByIdOrThrow(id);
        return new UserResponseDto(app_user); // Конвертируем Task в TaskResponseDto
    }

    //3. Получение пользователя по его точному имени
    public UserResponseDto findUserByExactLogin(String login) {
        AppUser app_user = userRepository.findUserByLogin(login)
                .orElseThrow(() -> new UserLoginNotFoundException(login));
        return new UserResponseDto(app_user); // Конвертируем Task в TaskResponseDto
    }

    //4. Получение пользователя по его имейлу
    public UserResponseDto findUserByEmail(String email) {
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IncorrectEmailFormatException();
        }
        AppUser app_user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserEmailNotFoundException(email));
        return new UserResponseDto(app_user);
    }

    //5. Получение пользователя по части имени
    public Page<UserResponseDto> findUserByLoginContaining(String loginPart, Pageable pageable) {
        Page<AppUser> app_users = userRepository.findUserByLoginContaining(loginPart, pageable);
        if (app_users.isEmpty()) {
            throw new UserLoginNotFoundException(loginPart);
        }
        return convertToDtoList(app_users);
    }

    //6. Получение всех пользователей
    public Page<UserResponseDto> findAllUsers(Pageable pageable) {
        return convertToDtoList(userRepository.findAll(pageable));
    }

    //7. Обновление логина,почты и пароля пользователя по айди
    @Transactional
    public UserResponseDto updateUserById(Long id, UserUpdateDto dto) {
        AppUser app_user = getUserByIdOrThrow(id);

        // Обновление полей с проверкой на null
        if (dto.getLogin() != null && !dto.getLogin().isBlank()) {
                app_user.setLogin(dto.getLogin());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            app_user.setEmail(dto.getEmail());
        }

        // Кодирование пароля, если он был передан
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            String encodedPassword = passwordEncoder.encode(dto.getPassword()); // Используем PasswordEncoder
            app_user.setPassword(encodedPassword);
        }

        return new UserResponseDto(app_user);
    }

    //8. Обновление пароля пользователя по айди
    @Transactional
    public UserResponseDto updateUserPasswordById(Long id, String password) {
        AppUser app_user = getUserByIdOrThrow(id);

        // 1. Если пароль не указан - пропускаем обновление (для PATCH)
        if (password == null || password.isBlank()) {
            return new UserResponseDto(app_user);
        }

        // 2. Проверка длины (8-100 символов)
        if (password.length() < 8 || password.length() > 100) {
            throw new InvalidPasswordException();
        }

        // 3. Проверка наличия хотя бы одной буквы
        if (!password.matches(".*[a-zA-Z].*")) {
            throw new InvalidPasswordException();
        }

        app_user.setPassword(password);
        return new UserResponseDto(app_user);

    }

    //9. Обновление роли пользователя по айди
    @Transactional
    public UserResponseDto updateUserRoleById(Long id, String role) {
        AppUser app_user = getUserByIdOrThrow(id);

        if (!UserRole.isValid(role)) {
            throw new IncorrectRoleTitleException(role);
        }

        app_user.setRole(role);
        return new UserResponseDto(app_user);
    }

    //10. Удаление пользователя по логину
    @Transactional
    public void deleteUserByLogin(String login) {
        if (!userRepository.existsByLogin(login)) {
            throw new UserLoginNotFoundException(login);
        }
        userRepository.deleteUserByLogin(login);
    }

    //11. Удаление пользователя по айди
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserIdNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    //12. Поиск пользователей по роли
    public Page<UserResponseDto> findUsersByRole(String role, Pageable pageable) {
        if(UserRole.isValid(role)) {
            return convertToDtoList(userRepository.findUsersByRole(role, pageable));
        } else {
            throw new IncorrectRoleTitleException(role);
        }

    }

    //13. Вывод всех комментов пользователя по айди
    public Page<CommentResponseDto> findCommentsByUserId(Long userId, Pageable pageable) {
        AppUser app_user = getUserByIdOrThrow(userId);
        return commentService.getCommentsForUser(userId,pageable);
    }

    // Вспомогательный метод для преобразования коллекци пользователей в коллекцию ДТО
    private Page<UserResponseDto> convertToDtoList(Page<AppUser> app_users) {
        return app_users.map(UserResponseDto::new);
    }


    private AppUser getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(id));
    }
}
