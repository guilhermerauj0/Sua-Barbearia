package com.barbearia.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.barbearia.domain.enums.PeriodoRelatorio;

/**
 * DTO que representa um relatório financeiro completo da barbearia.
 * 
 * <p>Contém métricas agregadas de faturamento, serviços realizados e análises de rentabilidade.</p>
 * 
 * @param periodo Período do relatório (DIA, SEMANA, MES)
 * @param dataInicio Data/hora de início do período analisado
 * @param dataFim Data/hora de fim do período analisado
 * @param faturamentoTotal Faturamento total do período (soma de todos os serviços concluídos)
 * @param totalAgendamentos Total de agendamentos concluídos no período
 * @param ticketMedio Valor médio por agendamento (faturamentoTotal / totalAgendamentos)
 * @param faturamentoPorDia Faturamento médio por dia do período
 * @param servicosMaisRentaveis Lista dos serviços mais rentáveis ordenados por faturamento
 * 
 * @author Sua Barbearia Team
 * @since 1.0
 */
public record RelatorioFinanceiroDto(
    PeriodoRelatorio periodo,
    LocalDateTime dataInicio,
    LocalDateTime dataFim,
    BigDecimal faturamentoTotal,
    Long totalAgendamentos,
    BigDecimal ticketMedio,
    BigDecimal faturamentoPorDia,
    List<ServicoRentabilidadeDto> servicosMaisRentaveis
) {
    
    /**
     * DTO interno que representa a rentabilidade de um serviço específico.
     * 
     * @param servicoId ID do serviço
     * @param servicoNome Nome do serviço
     * @param totalRealizacoes Quantidade de vezes que o serviço foi realizado
     * @param faturamentoTotal Faturamento total gerado pelo serviço
     * @param percentualFaturamento Percentual do faturamento total da barbearia
     */
    public record ServicoRentabilidadeDto(
        Long servicoId,
        String servicoNome,
        Long totalRealizacoes,
        BigDecimal faturamentoTotal,
        BigDecimal percentualFaturamento
    ) {}
}
