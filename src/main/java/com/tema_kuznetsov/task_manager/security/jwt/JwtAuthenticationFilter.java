package com.tema_kuznetsov.task_manager.security.jwt;

import com.tema_kuznetsov.task_manager.security.JwtAuthenticationEntryPoint;
import com.tema_kuznetsov.task_manager.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint entryPoint;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            JwtAuthenticationEntryPoint entryPoint
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.entryPoint = entryPoint;
    }

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/login",
            "/api/auth/register",
            "/error"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (PUBLIC_PATHS.stream().anyMatch(path::startsWith) || isSwaggerPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("Заголовок авторизации отсутствует или не начинается с 'Bearer '");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7).replaceAll("\\s+", "");
        logger.debug("Извлечён JWT: {}", jwt);

        try {
            userEmail = jwtService.extractUsername(jwt);
            logger.debug("Извлечено имя пользователя из токена: {}", userEmail);
        } catch (Exception e) {
            logger.warn("Ошибка при валидации JWT: {}", e.getMessage());
            entryPoint.commence(request, response, new AuthenticationException("Ошибка JWT") {
                @Override
                public String getMessage() {
                    return e.getMessage();
                }
            });
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                logger.debug("Загружены данные пользователя: {}", userDetails.getUsername());

                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    logger.debug("Токен действителен для пользователя: {}", userEmail);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("Токен недействителен для пользователя: {}", userEmail);
                }

            } catch (UsernameNotFoundException ex) {
                logger.warn("JWT ссылается на несуществующего пользователя: {}", userEmail);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isSwaggerPath(String path) {
        return path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.equals("/swagger-ui.html");
    }
}