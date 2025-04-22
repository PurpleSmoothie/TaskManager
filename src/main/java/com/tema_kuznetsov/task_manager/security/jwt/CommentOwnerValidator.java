package com.tema_kuznetsov.task_manager.security.jwt;

import com.tema_kuznetsov.task_manager.repositories.CommentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("commentOwnerValidator")
public class CommentOwnerValidator {

    private final CommentRepository commentRepository;

    public CommentOwnerValidator(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public boolean isCommentOwner(Long commentId, Authentication authentication) {
        String userEmail = authentication.getName();
        return commentRepository.findById(commentId)
                .map(comment -> comment.getAuthor().getEmail().equals(userEmail))
                .orElse(false);
    }
}