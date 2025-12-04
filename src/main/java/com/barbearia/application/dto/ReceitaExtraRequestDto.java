package com.barbearia.application.dto;

import com.barbearia.domain.enums.CategoriaReceitaExtra;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para criação de receita extra.
 */
@Schema(description = "Dados para criar uma receita extra")
public record ReceitaExtraRequestDto(
        @NotNull(message = "Valor é obrigatório") @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero") @Schema(description = "Valor da receita", example = "500.00") BigDecimal valor,

        @NotNull(message = "Categoria é obrigatória") @Schema(description = "Categoria da receita", example = "VENDA_PRODUTO") CategoriaReceitaExtra categoria,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres") @Schema(description = "Descrição detalhada da receita", example = "Venda de pomada e shampoo") String descricao,

        @NotNull(message = "Data da transação é obrigatória") @Schema(description = "Data em que a receita ocorreu", example = "2025-11-10") LocalDate dataTransacao) {
}
