package com.tema_kuznetsov.task_manager.model;

import com.tema_kuznetsov.task_manager.model.constrain.UserConstrains;
import com.tema_kuznetsov.task_manager.model.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_users")
public class AppUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = UserConstrains.MIN_EMAIL_LENGTH,max = UserConstrains.MAX_EMAIL_LENGTH)
    @NotBlank
    @Email
    private String email;

    @Column(length = 20, nullable = false)
    private String role = UserRole.USER;

    @NotBlank
    @Size(min = UserConstrains.MIN_LOGIN_LENGTH, max = UserConstrains.MAX_LOGIN_LENGTH)
    private String login;
    @Size(min = UserConstrains.MIN_PASSWORD_LENGTH,max = UserConstrains.MAX_PASSWORD_LENGTH)
    private String password;


    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private List<Task> ownedTasks = new ArrayList<>();


    @OneToMany(mappedBy = "performer", fetch = FetchType.LAZY)
    private List<Task> assignedTasks = new ArrayList<>();


    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
