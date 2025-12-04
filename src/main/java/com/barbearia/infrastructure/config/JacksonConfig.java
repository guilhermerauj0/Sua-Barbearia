package com.barbearia.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.format.DateTimeFormatter;

/**
 * Configuração do Jackson para serialização de datas e horas.
 * 
 * Responsabilidades:
 * - Configurar formato ISO-8601 com sufixo 'Z' para LocalDateTime
 * - Desabilitar timestamps numéricos para usar formato de string
 * - Configurar fuso horário padrão (America/Sao_Paulo)
 * 
 * @author Sua Barbearia Team
 */
@Configuration
public class JacksonConfig {

    /**
     * Formato ISO-8601 para LocalDateTime com sufixo 'Z'.
     * Exemplo: "2025-11-26T16:00:00Z"
     */
    private static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    /**
     * Configura ObjectMapper global com serialização customizada de datas.
     * 
     * @return ObjectMapper configurado
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Registra módulo JavaTime para suporte a LocalDateTime, LocalDate, etc
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // Configura serialização customizada de LocalDateTime com sufixo 'Z'
        javaTimeModule.addSerializer(
                java.time.LocalDateTime.class,
                new LocalDateTimeSerializer(ISO_DATETIME_FORMATTER));

        mapper.registerModule(javaTimeModule);

        // Desabilita timestamps numéricos - usa strings ISO-8601
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Configura formatação de data
        mapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

        return mapper;
    }
}
