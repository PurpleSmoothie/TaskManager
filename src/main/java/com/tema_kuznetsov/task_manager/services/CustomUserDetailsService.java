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

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Lazy
    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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

    @Transactional
    public AppUser createUser(@Valid UserCreateDto dto) {
        AppUser appUser = new AppUser();
        appUser.setLogin(dto.getLogin());
        appUser.setRole(UserRole.USER.toString());
        appUser.setEmail(dto.getEmail());
        appUser.setPassword(passwordEncoder.encode(dto.getPassword()));
        logger.info("Creating user with email: {}", dto.getEmail());

        return userRepository.save(appUser);
    }
}