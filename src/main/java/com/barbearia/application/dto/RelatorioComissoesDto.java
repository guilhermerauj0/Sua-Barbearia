package com.barbearia.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para relatório geral de comissões de uma barbearia.
 * 
 * @author Sua Barbearia Team
 */
public record RelatorioComissoesDto(
    Long barbeariaId,
    String barbeariaNome,
    LocalDate dataInicio,
    LocalDate dataFim,
    List<ComissaoFuncionarioDto> comissoesPorFuncionario,
    Integer totalAgendamentos,
    BigDecimal valorTotalServicos,
    BigDecimal totalComissoes
) {
}
