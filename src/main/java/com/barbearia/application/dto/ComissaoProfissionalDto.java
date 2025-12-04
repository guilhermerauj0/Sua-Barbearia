package com.barbearia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * DTO para comissões do profissional.
 */
@Schema(description = "Informações de comissões do profissional")
public record ComissaoProfissionalDto(
        @Schema(description = "Taxa de comissão do profissional (%)", example = "15.0") Double taxaComissao,

        @Schema(description = "Total de serviços concluídos", example = "125") Long totalServicos,

        @Schema(description = "Valor total em comissões", example = "3750.00") BigDecimal totalComissoes,

        @Schema(description = "Período de cálculo") PeriodoDto periodo) {
}
