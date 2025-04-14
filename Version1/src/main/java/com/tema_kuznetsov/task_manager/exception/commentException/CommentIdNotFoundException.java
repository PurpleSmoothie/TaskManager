package com.tema_kuznetsov.task_manager.exception.commentException;

public class CommentIdNotFoundException extends RuntimeException {
    public CommentIdNotFoundException(Long id) {
        super("Comment not found with id: " + id);
    }
}
