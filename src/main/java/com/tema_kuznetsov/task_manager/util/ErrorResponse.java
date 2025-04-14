package com.tema_kuznetsov.task_manager.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final int status; // HTTP статус код
    private final String error; // Тип ошибки (например, "VALIDATION_ERROR")
    private final String message; // Детальное сообщение
    private final LocalDateTime timestamp = LocalDateTime.now();

    // Конструктор для удобного создания с HttpStatus
    public ErrorResponse(HttpStatus status, String error, String message) {
        this(status.value(), error, message);
    }
}