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


@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    public UserResponseDto findUserById(Long id) {
        AppUser appUser = getUserByIdOrThrow(id);
        return new UserResponseDto(appUser);
    }

    public UserResponseDto findUserByExactLogin(String login) {
        AppUser appUser = userRepository.findUserByLogin(login)
                .orElseThrow(() -> new UserLoginNotFoundException(login));
        return new UserResponseDto(appUser);
    }

    public UserResponseDto findUserByEmail(String email) {

        AppUser appUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserEmailNotFoundException(email));
        return new UserResponseDto(appUser);
    }

    public Page<UserResponseDto> findUserByLoginContaining(String loginPart, Pageable pageable) {
        Page<AppUser> appUsers = userRepository.findUserByLoginContaining(loginPart, pageable);
        return convertToDtoList(appUsers);
    }

    public Page<UserResponseDto> findAllUsers(Pageable pageable) {
        return convertToDtoList(userRepository.findAll(pageable));
    }

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

    @Transactional
    public UserResponseDto updateUserPasswordById(Long id, String password) {
        AppUser appUser = getUserByIdOrThrow(id);

        String encodedPassword = passwordEncoder.encode(password);
        appUser.setPassword(encodedPassword);

        return new UserResponseDto(appUser);
    }

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

    public Page<UserResponseDto> findUsersByRole(String role, Pageable pageable) {
            return convertToDtoList(userRepository.findUsersByRole(role, pageable));

    }

    public Page<CommentResponseDto> findCommentsByUserId(Long userId, Pageable pageable) {
        AppUser appUser = getUserByIdOrThrow(userId);
        return commentService.getCommentsForUser(userId,pageable);
    }

    private Page<UserResponseDto> convertToDtoList(Page<AppUser> appUsers) {
        return appUsers.map(UserResponseDto::new);
    }

    private AppUser getUserByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserIdNotFoundException(id));
    }

}
