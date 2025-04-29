package com.tema_kuznetsov.task_manager.security;

import com.tema_kuznetsov.task_manager.models.AppUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Класс, представляющий детали пользователя, который используется для аутентификации в Spring Security.
 * Этот класс инкапсулирует информацию о пользователе, такую как ID, логин, пароль и его роли.
 */
@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Конструктор для создания экземпляра CustomUserDetails из сущности пользователя.
     *
     * @param user Пользовательская сущность, содержащая информацию о пользователе.
     * @param authorities Коллекция ролей, присвоенных пользователю.
     */
    public CustomUserDetails(AppUser user, Collection<? extends GrantedAuthority> authorities) {
        this.id = user.getId();
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.authorities = authorities;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}