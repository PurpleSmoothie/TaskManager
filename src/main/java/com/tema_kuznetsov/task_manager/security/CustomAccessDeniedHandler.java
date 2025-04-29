package com.tema_kuznetsov.task_manager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tema_kuznetsov.task_manager.util.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Обработчик ошибок для доступа, который запрещен из-за отсутствия прав.
 * Возвращает ошибку 403 с соответствующим сообщением и путем запроса.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    /**
     * Конструктор, который инициализирует объект ObjectMapper для преобразования в JSON.
     *
     * @param objectMapper Объект для преобразования Java-объектов в JSON.
     */
    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Этот метод вызывается при попытке доступа к ресурсу без необходимых прав.
     *
     * @param request HTTP запрос, который привел к ошибке.
     * @param response HTTP ответ, который будет отправлен клиенту с ошибкой.
     * @param accessDeniedException Исключение, вызванное проблемой с правами доступа.
     * @throws IOException Если возникает ошибка при записи ответа.
     * @throws ServletException Если возникает ошибка сервлета.
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "Доступ запрещен. У вас нет прав на выполнение этого действия.",
                request.getRequestURI()
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}