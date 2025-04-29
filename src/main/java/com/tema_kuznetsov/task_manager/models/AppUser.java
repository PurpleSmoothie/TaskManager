package com.tema_kuznetsov.task_manager.models;

import com.tema_kuznetsov.task_manager.models.constrains.UserConstrains;
import com.tema_kuznetsov.task_manager.models.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Сущность пользователя системы. Представляет информацию о пользователе, включая его роль, логин, пароль и список задач.
 * Используется для хранения данных о пользователях в базе данных.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_users")
public class AppUser {

    /**
     * Уникальный идентификатор пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Электронная почта пользователя. Не может быть пустой и должна быть в правильном формате.
     */
    @Size(min = UserConstrains.MIN_EMAIL_LENGTH, max = UserConstrains.MAX_EMAIL_LENGTH)
    @NotBlank
    @Email
    private String email;

    /**
     * Роль пользователя в системе. По умолчанию - "USER".
     */
    @Column(length = 20, nullable = false)
    private String role = UserRole.USER;

    /**
     * Логин пользователя. Не может быть пустым и имеет ограничение на длину.
     */
    @NotBlank
    @Size(min = UserConstrains.MIN_LOGIN_LENGTH, max = UserConstrains.MAX_LOGIN_LENGTH)
    private String login;

    /**
     * Пароль пользователя. Должен соответствовать заданным ограничениям по длине.
     */
    @Size(min = UserConstrains.MIN_PASSWORD_LENGTH, max = UserConstrains.MAX_PASSWORD_LENGTH)
    private String password;

    /**
     * Список задач, принадлежащих пользователю.
     */
    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private List<Task> ownedTasks = new ArrayList<>();

    /**
     * Список задач, назначенных пользователю для выполнения.
     */
    @OneToMany(mappedBy = "performer", fetch = FetchType.LAZY)
    private List<Task> assignedTasks = new ArrayList<>();

    /**
     * Список комментариев, написанных пользователем.
     */
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    /**
     * Дата и время создания пользователя. Не может быть изменена после создания.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}