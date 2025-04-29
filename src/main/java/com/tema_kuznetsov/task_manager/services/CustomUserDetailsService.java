package com.tema_kuznetsov.task_manager.services;

import com.tema_kuznetsov.task_manager.dto.user.UserCreateDto;
import com.tema_kuznetsov.task_manager.models.AppUser;
import com.tema_kuznetsov.task_manager.models.enums.UserRole;
import com.tema_kuznetsov.task_manager.repositories.UserRepository;
import com.tema_kuznetsov.task_manager.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для работы с деталями пользователя.
 * Реализует загрузку пользователя по email и создание нового пользователя.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор, инициализирующий сервис.
     * Используется для внедрения зависимости `PasswordEncoder`.
     *
     * @param passwordEncoder объект для кодирования паролей
     */
    @Lazy
    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Загружает пользователя по email.
     * В случае, если пользователь не найден, выбрасывается исключение {@link UsernameNotFoundException}.
     *
     * @param email email пользователя
     * @return объект {@link UserDetails} с информацией о пользователе
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);

        AppUser user = userRepository.findUserByEmail(email).orElse(null);

        if (user == null) {
            logger.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found");
        }

        logger.debug("User found: {}", user.getEmail());
        logger.debug("User role: {}", user.getRole());

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
        logger.debug("GrantedAuthority created: {}", authority);

        return new CustomUserDetails(user, List.of(authority));
    }

    /**
     * Создает нового пользователя.
     * Задает роль по умолчанию `USER` и шифрует пароль перед сохранением.
     *
     * @param dto объект, содержащий информацию о новом пользователе
     * @return созданный объект {@link AppUser}
     */
    @Transactional
    public AppUser createUser(@Valid UserCreateDto dto) {
        AppUser appUser = new AppUser();
        appUser.setLogin(dto.getLogin());
        appUser.setRole(UserRole.USER);
        appUser.setEmail(dto.getEmail());
        appUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        logger.info("Creating user with email: {}", dto.getEmail());

        return userRepository.save(appUser);
    }
}