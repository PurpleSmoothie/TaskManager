package com.tema_kuznetsov.task_manager.exceptions.taskException.titleException;

public class TaskTitleLengthExceededException extends RuntimeException {
    public TaskTitleLengthExceededException(int currentLength, int maxLength) {
        super(String.format("Title length %d exceeds maximum allowed %d characters",
                currentLength, maxLength));
    }
}
