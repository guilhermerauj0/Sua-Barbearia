package com.barbearia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * DTO para resumo por categoria.
 */
@Schema(description = "Resumo financeiro por categoria")
public record CategoriaResumoDto(
        @Schema(description = "Nome da categoria", example = "ALUGUEL") String categoria,

        @Schema(description = "Total da categoria", example = "2500.00") BigDecimal total,

        @Schema(description = "Percentual do total", example = "46.13") BigDecimal percentual,

        @Schema(description = "Quantidade de transações") Long quantidade) {
}
