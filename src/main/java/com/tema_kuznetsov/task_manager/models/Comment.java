package com.tema_kuznetsov.task_manager.models;

import com.tema_kuznetsov.task_manager.models.constrains.CommentConstrains;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


/**
 * Сущность комментария, связанная с задачами. Представляет текст комментария и связь с пользователем и задачей.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    /**
     * Уникальный идентификатор комментария.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Текст комментария. Не может быть пустым и ограничен максимальной длиной.
     */
    @NotBlank
    @Size(max = CommentConstrains.MAX_TEXT_LENGTH)
    private String text;

    /**
     * Дата и время создания комментария. Не может быть изменена после создания.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления комментария.
     */
    private LocalDateTime updatedAt;

    /**
     * Автор комментария, который является пользователем системы.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser author;

    /**
     * Задача, к которой привязан комментарий.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
}
