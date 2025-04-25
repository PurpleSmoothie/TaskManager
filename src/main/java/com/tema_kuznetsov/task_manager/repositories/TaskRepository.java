package com.tema_kuznetsov.task_manager.repositories;

import com.tema_kuznetsov.task_manager.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByTitle(String title);
    Optional<Task> findTaskByTitle(String title);
    Page<Task> findTaskByTitleContaining(String titlePart, Pageable pageable);
    Page<Task> findAll(Pageable pageable);

    Page<Task> findTasksByStatus(String status, Pageable pageable);
    Page<Task> findTasksByPriority(String priority, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Task t WHERE t.title = :title")
    void deleteTaskByTitle(@Param("title") String title);
}
