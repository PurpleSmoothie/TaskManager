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


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = TaskConstrains.MAX_TITLE_LENGTH)
    private String title;
    @Size(max = TaskConstrains.MAX_DESC_LENGTH)
    private String description;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(length = 20, nullable = false)
    private String priority;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    @JsonProperty("owner_id")
    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    @JsonProperty("performer_id")
    public Long getPerformerId() {
        return performer != null ? performer.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performer_id")
    private AppUser performer;

    @OneToMany(mappedBy = "task",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

}
