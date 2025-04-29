package com.tema_kuznetsov.task_manager.validators.commentValidators;

import com.tema_kuznetsov.task_manager.repositories.CommentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Валидатор, который проверяет, является ли пользователь владельцем комментария.
 * Используется для проверки прав доступа пользователя к определенному комментарию.
 */
@Component("commentOwnerValidator")
public class CommentOwnerValidator {

    private final CommentRepository commentRepository;

    /**
     * Конструктор для инициализации CommentOwnerValidator с репозиторием комментариев.
     *
     * @param commentRepository Репозиторий для работы с комментариями.
     */
    public CommentOwnerValidator(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * Проверяет, является ли автор комментария текущим пользователем.
     *
     * @param commentId      ID комментария, для которого выполняется проверка.
     * @param authentication Аутентификация текущего пользователя.
     * @return true, если пользователь является автором комментария, иначе false.
     */
    public boolean isCommentOwner(Long commentId, Authentication authentication) {
        String userEmail = authentication.getName();
        return commentRepository.findById(commentId)
                .map(comment -> comment.getAuthor().getEmail().equals(userEmail))
                .orElse(false);
    }
}