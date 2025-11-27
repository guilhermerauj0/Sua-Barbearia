package com.barbearia.application.dto;

import com.barbearia.domain.enums.TipoTransacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de resposta para transação financeira.
 */
@Schema(description = "Dados de uma transação financeira")
public record TransacaoFinanceiraDto(
        @Schema(description = "ID da transação") Long id,

        @Schema(description = "ID da barbearia") Long barbeariaId,

        @Schema(description = "Tipo de transação") TipoTransacao tipoTransacao,

        @Schema(description = "Valor da transação", example = "2500.00") BigDecimal valor,

        @Schema(description = "Categoria", example = "ALUGUEL") String categoria,

        @Schema(description = "Descrição detalhada") String descricao,

        @Schema(description = "Data da transação", example = "2025-11-05") LocalDate dataTransacao,

        @Schema(description = "Data de criação do registro") LocalDateTime dataCriacao,

        @Schema(description = "Data da última atualização") LocalDateTime dataAtualizacao) {
}
