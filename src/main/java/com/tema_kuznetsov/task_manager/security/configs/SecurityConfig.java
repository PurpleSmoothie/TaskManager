package com.tema_kuznetsov.task_manager.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tema_kuznetsov.task_manager.security.CustomAccessDeniedHandler;
import com.tema_kuznetsov.task_manager.security.JwtAuthenticationEntryPoint;
import com.tema_kuznetsov.task_manager.security.jwt.JwtAuthenticationFilter;
import com.tema_kuznetsov.task_manager.security.jwt.JwtService;
import com.tema_kuznetsov.task_manager.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация безопасности для приложения с использованием Spring Security.
 * Настроены фильтры для аутентификации через JWT, настройка доступа и обработки ошибок для аутентификации и авторизации.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig implements WebMvcConfigurer {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    /**
     * Конструктор для инъекции зависимостей.
     *
     * @param userDetailsService Сервис для работы с пользователями.
     * @param accessDeniedHandler Обработчик ошибок доступа.
     * @param jwtService Сервис для работы с JWT токенами.
     * @param objectMapper Объект для сериализации/десериализации JSON.
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          JwtService jwtService,
                          ObjectMapper objectMapper) {
        this.userDetailsService = userDetailsService;
        this.accessDeniedHandler = accessDeniedHandler;
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    /**
     * Создает бин для JwtAuthenticationEntryPoint, который используется для обработки ошибок аутентификации.
     *
     * @return Обработчик ошибок аутентификации с использованием JWT.
     */
    @Bean
    public JwtAuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint(objectMapper);
    }

    /**
     * Создает бин для JwtAuthenticationFilter, который проверяет JWT токен в запросах.
     *
     * @param authenticationEntryPoint Обработчик ошибок аутентификации.
     * @return Фильтр для аутентификации через JWT.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtAuthenticationEntryPoint authenticationEntryPoint) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService, authenticationEntryPoint);
    }

    /**
     * Создает и возвращает PasswordEncoder для хеширования паролей.
     *
     * @return PasswordEncoder с использованием алгоритма BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Конфигурирует AuthenticationManager с использованием кастомного сервиса пользователей и паролей.
     *
     * @param http HttpSecurity для настройки AuthenticationManager.
     * @return AuthenticationManager для аутентификации.
     * @throws Exception Если возникает ошибка при конфигурации.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return builder.build();
    }

    /**
     * Конфигурирует безопасность приложения, включая авторизацию запросов и обработку ошибок.
     * Включает настройку фильтров безопасности и отключение CSRF защиты.
     *
     * @param http HttpSecurity для настройки безопасности.
     * @return Конфигурированная SecurityFilterChain.
     * @throws Exception Если возникает ошибка при конфигурации безопасности.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and() // Включаем CORS
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter(authenticationEntryPoint()), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Добавляет настройку CORS.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Разрешаем доступ ко всем эндпоинтам
                .allowedOrigins("http://localhost:3000") // Разрешаем доступ только с этого фронтенда (можно добавить другие адреса)
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Разрешаем только эти методы
                .allowedHeaders("*") // Разрешаем все заголовки
                .allowCredentials(true); // Разрешаем отправку куки
    }
}