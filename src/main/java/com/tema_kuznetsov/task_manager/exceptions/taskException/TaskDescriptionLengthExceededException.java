package com.tema_kuznetsov.task_manager.exceptions.taskException;

public class TaskDescriptionLengthExceededException extends RuntimeException {
    public TaskDescriptionLengthExceededException(int currentLength, int maxLength) {
        super(String.format("Description length %d exceeds maximum allowed %d characters",
                currentLength, maxLength));
    }
}