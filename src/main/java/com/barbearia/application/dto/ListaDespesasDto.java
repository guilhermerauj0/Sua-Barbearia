package com.barbearia.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para lista de despesas com resumo.
 */
@Schema(description = "Lista de despesas com totalizações")
public record ListaDespesasDto(
                @Schema(description = "Total de despesas no período", example = "5420.00") BigDecimal total,

                @Schema(description = "Quantidade de despesas") Long quantidade,

                @Schema(description = "Período consultado") PeriodoDto periodo,

                @Schema(description = "Lista de despesas") List<TransacaoFinanceiraDto> despesas,

                @Schema(description = "Resumo por categoria") List<CategoriaResumoDto> resumoPorCategoria) {
}
