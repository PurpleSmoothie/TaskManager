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
    // Базовые методы (уже есть в JpaRepository):
    // save(), findById(), findAll(), deleteById(), count() и т.д.


    boolean existsByTitle(String title);
    Optional<Task> findTaskByTitle(String title); // Важно: возвращаем Optional
    Page<Task> findTaskByTitleContaining(String titlePart, Pageable pageable); // По части названия
    Page<Task> findAll(Pageable pageable);

    // Фильтрация по статусу и приоритету
    Page<Task> findTasksByStatus(String status, Pageable pageable);
    Page<Task> findTasksByPriority(String priority, Pageable pageable);

    @Transactional // Весь метод выполняется как одна транзакция
    @Modifying // Указывает, что запрос изменяет данные (не SELECT)
               // Требуется для INSERT, UPDATE, DELETE операций
    @Query("DELETE FROM Task t WHERE t.title = :title")
    void deleteTaskByTitle(@Param("title") String title);
}
