package com.tema_kuznetsov.task_manager.util;

import com.tema_kuznetsov.task_manager.exceptions.commentException.CommentIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.TaskIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.titleException.TaskTitleNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.*;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.IncorrectEmailFormatException;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.UserEmailAlreadyExistsException;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.UserEmailNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.loginException.UserLoginNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.ownerException.OwnerIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.performerException.PerformerIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.roleException.SelfRoleChangeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        String message = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {
        String paramName = ex.getParameterName();
        String message = "Обязательный параметр \"" + paramName + "\" отсутствует.";

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, message, request.getRequestURI()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotAllowed(HttpServletRequest request, HttpRequestMethodNotSupportedException ex) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        error.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        error.put("error", "Метод не поддерживается");
        error.put("message", "Метод '" + ex.getMethod() + "' не поддерживается для данного эндпоинта.");
        error.put("path", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorMessage.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Ошибка валидации", errorMessage.toString()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleInvalidPathVariable(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        if (ex.getRequiredType() == Long.class) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "error", "Bad Request",
                    "message", "ID должен быть числом",
                    "path", request.getRequestURI()
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", "Невалидный параметр запроса: " + ex.getName(),
                "path", request.getRequestURI()
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
        String message = ex.getMessage().contains("JSON parse error")
                ? "Неверная структура JSON"
                : "Некорректный JSON";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Ошибка валидации", message));
    }




    @ExceptionHandler(SelfRoleChangeException.class)
    public ResponseEntity<ErrorResponse> handleTitleAlreadyExists(SelfRoleChangeException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Некорректный запрос", ex.getMessage()));
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserIdNotFound(UserIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Некорректный идентификатор", ex.getMessage()));
    }

    @ExceptionHandler(PerformerIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePerformerIdNotFound(PerformerIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Некорректный идентификатор", ex.getMessage()));
    }

    @ExceptionHandler(OwnerIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOwnerIdNotFound(OwnerIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Некорректный идентификатор", ex.getMessage()));
    }

    @ExceptionHandler(TaskTitleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskTitleNotFound(TaskTitleNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Некорректное название", ex.getMessage()));
    }

    @ExceptionHandler(SelfDeletionException.class)
    public ResponseEntity<ErrorResponse> handleTaskTitleNotFound(SelfDeletionException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Некорректный запрос", ex.getMessage()));
    }

    @ExceptionHandler(TaskIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskIdNotFound(TaskIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Некорректный идентификатор", ex.getMessage()));
    }

    @ExceptionHandler(UserLoginNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoginNotFound(UserLoginNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Некорректный логин", ex.getMessage()));
    }


    @ExceptionHandler(UserEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(UserEmailAlreadyExistsException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Некорректный email", ex.getMessage()));
    }

    @ExceptionHandler(CommentIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(CommentIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Некорректный идентификатор", ex.getMessage()));
    }

    @ExceptionHandler(UserEmailNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotFound(UserEmailNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Некорректный email", ex.getMessage()));
    }

    @ExceptionHandler(IncorrectEmailFormatException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectEmailFormat(IncorrectEmailFormatException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Некорректный email", ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectEmailFormat(BadCredentialsException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Неверный email или логин", ex.getMessage()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "Маршрут " + request.getRequestURI() + " не найден",
                request.getRequestURI()
        );
    }
}