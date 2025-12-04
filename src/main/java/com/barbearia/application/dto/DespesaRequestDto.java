package com.barbearia.application.dto;

import com.barbearia.domain.enums.CategoriaDespesa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para criação de despesa.
 */
@Schema(description = "Dados para criar uma despesa")
public record DespesaRequestDto(
        @NotNull(message = "Valor é obrigatório") @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero") @Schema(description = "Valor da despesa", example = "2500.00") BigDecimal valor,

        @NotNull(message = "Categoria é obrigatória") @Schema(description = "Categoria da despesa", example = "ALUGUEL") CategoriaDespesa categoria,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres") @Schema(description = "Descrição detalhada da despesa", example = "Pagamento aluguel novembro 2025") String descricao,

        @NotNull(message = "Data da transação é obrigatória") @Schema(description = "Data em que a despesa ocorreu", example = "2025-11-05") LocalDate dataTransacao) {
}
