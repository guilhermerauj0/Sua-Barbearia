package com.barbearia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para lista de receitas extras com resumo.
 */
@Schema(description = "Lista de receitas extras com totalizações")
public record ListaReceitasExtrasDto(
        @Schema(description = "Total de receitas no período", example = "2350.00") BigDecimal total,

        @Schema(description = "Quantidade de receitas") Long quantidade,

        @Schema(description = "Período consultado") PeriodoDto periodo,

        @Schema(description = "Lista de receitas") List<TransacaoFinanceiraDto> receitas,

        @Schema(description = "Resumo por categoria") List<CategoriaResumoDto> resumoPorCategoria) {
}
