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

/**
 * Репозиторий для работы с сущностью {@link Task}.
 * Предоставляет методы для поиска задач, их удаления и работы с их статусом и приоритетом.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Проверяет, существует ли задача с данным заголовком.
     *
     * @param title Заголовок задачи.
     * @return true, если задача с таким заголовком существует.
     */
    boolean existsByTitle(String title);

    /**
     * Находит задачу по заголовку.
     *
     * @param title Заголовок задачи.
     * @return Опциональная задача.
     */
    Optional<Task> findTaskByTitle(String title);

    /**
     * Находит задачи, заголовки которых содержат заданную часть строки.
     *
     * @param titlePart Часть заголовка.
     * @param pageable Параметры пагинации.
     * @return Страница с задачами.
     */
    Page<Task> findTaskByTitleContaining(String titlePart, Pageable pageable);

    /**
     * Находит все задачи с учетом пагинации.
     *
     * @param pageable Параметры пагинации.
     * @return Страница с задачами.
     */
    Page<Task> findAll(Pageable pageable);

    /**
     * Находит задачи по статусу.
     *
     * @param status Статус задачи.
     * @param pageable Параметры пагинации.
     * @return Страница с задачами.
     */
    Page<Task> findTasksByStatus(String status, Pageable pageable);

    /**
     * Находит задачи по приоритету.
     *
     * @param priority Приоритет задачи.
     * @param pageable Параметры пагинации.
     * @return Страница с задачами.
     */
    Page<Task> findTasksByPriority(String priority, Pageable pageable);

    /**
     * Удаляет задачу по заголовку.
     *
     * @param title Заголовок задачи.
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM Task t WHERE t.title = :title")
    void deleteTaskByTitle(@Param("title") String title);
}