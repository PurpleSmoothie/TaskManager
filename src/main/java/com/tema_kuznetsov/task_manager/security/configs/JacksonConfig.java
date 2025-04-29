package com.tema_kuznetsov.task_manager.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация для настройки сериализации и десериализации объектов с использованием библиотеки Jackson.
 * Конфигурирует ObjectMapper с поддержкой сериализации объектов времени Java и выключением записи дат как timestamp.
 */
@Configuration
public class JacksonConfig {

    /**
     * Создает и настраивает ObjectMapper для работы с датами и временем.
     * Регистрация модуля для работы с Java 8 Date/Time API и отключение сериализации дат как timestamp.
     *
     * @return Настроенный объект ObjectMapper.
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}