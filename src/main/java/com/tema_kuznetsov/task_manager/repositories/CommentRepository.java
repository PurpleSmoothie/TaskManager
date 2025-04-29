package com.tema_kuznetsov.task_manager.repositories;

import com.tema_kuznetsov.task_manager.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью {@link Comment}.
 * Предоставляет методы для работы с комментариями, включая их поиск по задаче и автору.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Находит все комментарии для задачи, отсортированные по времени создания в убывающем порядке.
     *
     * @param taskId  Идентификатор задачи.
     * @param pageable Параметры пагинации.
     * @return Страница с комментариями.
     */
    Page<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId, Pageable pageable);

    /**
     * Находит все комментарии для пользователя (автора), отсортированные по времени создания в убывающем порядке.
     *
     * @param authorId Идентификатор пользователя (автора).
     * @param pageable Параметры пагинации.
     * @return Страница с комментариями.
     */
    Page<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
}