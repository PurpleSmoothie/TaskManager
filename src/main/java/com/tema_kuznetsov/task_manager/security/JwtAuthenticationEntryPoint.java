package com.tema_kuznetsov.task_manager.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tema_kuznetsov.task_manager.util.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Обработчик ошибок для несанкционированного доступа с отсутствующим или неверным JWT токеном.
 * Возвращает ошибку 401 с соответствующим сообщением и путем запроса.
 */
@RequiredArgsConstructor
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    /**
     * Этот метод вызывается при несанкционированном доступе (например, если JWT токен отсутствует или невалиден).
     *
     * @param request HTTP запрос, который привел к ошибке.
     * @param response HTTP ответ, который будет отправлен клиенту с ошибкой.
     * @param authException Исключение, вызванное проблемой с аутентификацией.
     * @throws IOException Если возникает ошибка при записи ответа.
     * @throws ServletException Если возникает ошибка сервлета.
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Невалидный или отсутствующий JWT токен",
                request.getRequestURI()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}