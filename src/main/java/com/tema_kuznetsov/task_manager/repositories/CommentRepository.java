package com.tema_kuznetsov.task_manager.repositories;

import com.tema_kuznetsov.task_manager.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 1. Поиск всех комментариев задачи по айди
    Page<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId, Pageable pageable);

    // 2. Поиск всех комментариев пользователя по айди(с пагинацией)
    Page<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);
}
