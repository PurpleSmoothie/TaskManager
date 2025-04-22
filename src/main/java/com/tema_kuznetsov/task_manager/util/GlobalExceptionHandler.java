package com.tema_kuznetsov.task_manager.util;

import com.tema_kuznetsov.task_manager.exceptions.commentException.CommentIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.priorityException.IncorrectPriorityTitleException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.statusException.IncorrectStatusTitleException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.TaskIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.titleException.TaskTitleAlreadyExistsException;
import com.tema_kuznetsov.task_manager.exceptions.taskException.titleException.TaskTitleNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.*;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.IncorrectEmailFormatException;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.UserEmailAlreadyExistsException;
import com.tema_kuznetsov.task_manager.exceptions.userException.emailException.UserEmailNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.loginException.UserLoginAlreadyExistsException;
import com.tema_kuznetsov.task_manager.exceptions.userException.loginException.UserLoginNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.ownerException.OwnerIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.performerException.PerformerIdNotFoundException;
import com.tema_kuznetsov.task_manager.exceptions.userException.roleException.IncorrectRoleTitleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

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
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", errorMessage.toString()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
        String message = ex.getMessage().contains("JSON parse error")
                ? "Invalid JSON structure"
                : "Malformed JSON";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message));
    }

    @ExceptionHandler(IncorrectPriorityTitleException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectPriority(IncorrectPriorityTitleException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid priority", ex.getMessage()));
    }

    @ExceptionHandler(IncorrectStatusTitleException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectStatus(IncorrectStatusTitleException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid status", ex.getMessage()));
    }

    @ExceptionHandler(TaskTitleAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleTitleAlreadyExists(TaskTitleAlreadyExistsException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid title", ex.getMessage()));
    }

    @ExceptionHandler(UserIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserIdNotFound(UserIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Invalid id", ex.getMessage()));
    }

    @ExceptionHandler(PerformerIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePerformerIdNotFound(PerformerIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Invalid id", ex.getMessage()));
    }

    @ExceptionHandler(OwnerIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOwnerIdNotFound(OwnerIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Invalid id", ex.getMessage()));
    }

    @ExceptionHandler(TaskTitleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskTitleNotFound(TaskTitleNotFoundException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid title", ex.getMessage()));
    }

    @ExceptionHandler(TaskIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTaskIdNotFound(TaskIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Invalid id", ex.getMessage()));
    }

    @ExceptionHandler(UserLoginNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLoginNotFound(UserLoginNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Invalid login", ex.getMessage()));
    }

    @ExceptionHandler(IncorrectRoleTitleException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectRole(IncorrectRoleTitleException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid role", ex.getMessage()));
    }

    @ExceptionHandler(UserLoginAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleLoginExists(UserLoginAlreadyExistsException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid login", ex.getMessage()));
    }

    @ExceptionHandler(UserEmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailExists(UserEmailAlreadyExistsException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid email", ex.getMessage()));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid password", ex.getMessage()));
    }

    @ExceptionHandler(CommentIdNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFound(CommentIdNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Invalid id", ex.getMessage()));
    }

    @ExceptionHandler(UserEmailNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotFound(UserEmailNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, "Invalid email", ex.getMessage()));
    }

    @ExceptionHandler(IncorrectEmailFormatException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectEmailFormat(IncorrectEmailFormatException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid email", ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleIncorrectEmailFormat(BadCredentialsException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST, "Invalid email or login", ex.getMessage()));
    }
}