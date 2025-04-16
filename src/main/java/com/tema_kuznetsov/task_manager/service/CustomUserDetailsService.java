package com.tema_kuznetsov.task_manager.service;

import com.tema_kuznetsov.task_manager.dto.user.UserCreateDto;
import com.tema_kuznetsov.task_manager.model.AppUser;
import com.tema_kuznetsov.task_manager.model.enums.UserRole;
import com.tema_kuznetsov.task_manager.repository.UserRepository;
import com.tema_kuznetsov.task_manager.security.CustomUserDetails;
import jakarta.validation.Valid;
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

    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Lazy
    public CustomUserDetailsService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        return new CustomUserDetails(user, List.of(authority));
    }

    //1. Создание нового пользователя
    @Transactional
    public AppUser createUser(@Valid UserCreateDto dto) {

        AppUser appUser = new AppUser();
        appUser.setLogin(dto.getLogin());
        appUser.setRole(UserRole.USER.toString());
        appUser.setEmail(dto.getEmail());

        // Кодирование пароля перед сохранением
        appUser.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(appUser);
    }

}
