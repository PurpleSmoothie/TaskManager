package com.tema_kuznetsov.task_manager.exception.commentException;

public class CommentTextLengthExceededException extends RuntimeException {
    public CommentTextLengthExceededException(int maxLength) {
        super(String.format("Description length exceeds maximum allowed %d characters", maxLength));
    }
}
