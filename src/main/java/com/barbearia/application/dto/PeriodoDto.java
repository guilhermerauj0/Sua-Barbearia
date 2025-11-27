package com.barbearia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * DTO para representar um período.
 */
@Schema(description = "Período de consulta")
public record PeriodoDto(
        @Schema(description = "Data inicial", example = "2025-10-28") LocalDate inicio,

        @Schema(description = "Data final", example = "2025-11-27") LocalDate fim) {
}
