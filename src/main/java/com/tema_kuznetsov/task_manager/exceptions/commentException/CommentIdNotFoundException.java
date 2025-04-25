package com.tema_kuznetsov.task_manager.exceptions.commentException;

public class CommentIdNotFoundException extends RuntimeException {
    public CommentIdNotFoundException(Long id) {
        super("Комментарий с id " + id + " не найден");
    }
}