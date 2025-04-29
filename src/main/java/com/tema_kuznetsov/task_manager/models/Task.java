package com.tema_kuznetsov.task_manager.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tema_kuznetsov.task_manager.models.constrains.TaskConstrains;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Сущность задачи в системе. Представляет задачу с заголовком, описанием, статусом и приоритетом.
 * Связана с пользователями, которые являются владельцем и исполнителем задачи, а также с комментариями.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    /**
     * Уникальный идентификатор задачи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Заголовок задачи. Ограничен максимальной длиной.
     */
    @Size(max = TaskConstrains.MAX_TITLE_LENGTH)
    private String title;

    /**
     * Описание задачи. Ограничено максимальной длиной.
     */
    @Size(max = TaskConstrains.MAX_DESC_LENGTH)
    private String description;

    /**
     * Статус задачи, например, "Новая", "В процессе", "Завершена".
     */
    @Column(length = 20, nullable = false)
    private String status;

    /**
     * Приоритет задачи, например, "Низкий", "Средний", "Высокий".
     */
    @Column(length = 20, nullable = false)
    private String priority;

    /**
     * Дата и время создания задачи. Не может быть изменена после создания.
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления задачи.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Владелец задачи, который может редактировать и назначать исполнителей.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    /**
     * Получить ID владельца задачи.
     *
     * @return ID владельца задачи или null, если владелец не задан.
     */
    @JsonProperty("owner_id")
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    /**
     * Получить ID исполнителя задачи.
     *
     * @return ID исполнителя задачи или null, если исполнитель не задан.
     */
    @JsonProperty("performer_id")
    public Long getPerformerId() {
        return performer != null ? performer.getId() : null;
    }

    /**
     * Исполнитель задачи, который должен выполнить задачу.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performer_id")
    private AppUser performer;

    /**
     * Список комментариев, привязанных к задаче.
     */
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}