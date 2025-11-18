package com.barbearia.application.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.barbearia.application.dto.RelatorioFinanceiroDto;
import com.barbearia.application.dto.RelatorioFinanceiroDto.ServicoRentabilidadeDto;
import com.barbearia.domain.enums.PeriodoRelatorio;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;

/**
 * Serviço responsável pela gestão financeira da barbearia.
 * 
 * <p>Funcionalidades principais:</p>
 * <ul>
 *   <li>Geração de relatórios financeiros por período</li>
 *   <li>Cálculo de faturamento e métricas de negócio</li>
 *   <li>Análise de serviços mais rentáveis</li>
 *   <li>Cache de relatórios frequentemente consultados</li>
 * </ul>
 * 
 * <p>Regras de negócio:</p>
 * <ul>
 *   <li>Apenas agendamentos com status CONCLUIDO são considerados no faturamento</li>
 *   <li>Apenas a própria barbearia pode acessar seus dados financeiros</li>
 *   <li>Períodos suportados: DIA (24h), SEMANA (7 dias), MES (30 dias)</li>
 * </ul>
 * 
 * @author Sua Barbearia Team
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
public class FinanceiroService {
    
    private static final Logger logger = LoggerFactory.getLogger(FinanceiroService.class);
    
    private final AgendamentoRepository agendamentoRepository;
    
    public FinanceiroService(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }
    
    /**
     * Gera relatório financeiro completo para a barbearia no período especificado.
     * 
     * <p>O relatório inclui:</p>
     * <ul>
     *   <li>Faturamento total do período</li>
     *   <li>Total de agendamentos concluídos</li>
     *   <li>Ticket médio (faturamento / total de agendamentos)</li>
     *   <li>Faturamento médio por dia</li>
     *   <li>Top 5 serviços mais rentáveis</li>
     * </ul>
     * 
     * <p>Cache habilitado: relatórios são cacheados por barbearia e período para melhor performance.</p>
     * 
     * @param barbeariaId ID da barbearia
     * @param periodo Período do relatório (DIA, SEMANA, MES)
     * @return DTO com o relatório financeiro completo
     * @throws IllegalArgumentException se barbeariaId ou periodo forem nulos
     */
    @Cacheable(value = "relatorios-financeiros", key = "#barbeariaId + '_' + #periodo")
    public RelatorioFinanceiroDto gerarRelatorioFinanceiro(Long barbeariaId, PeriodoRelatorio periodo) {
        logger.info("Gerando relatório financeiro para barbearia {} - período: {}", barbeariaId, periodo);
        
        if (barbeariaId == null) {
            throw new IllegalArgumentException("ID da barbearia não pode ser nulo");
        }
        if (periodo == null) {
            throw new IllegalArgumentException("Período do relatório não pode ser nulo");
        }
        
        // Calcular datas do período
        LocalDateTime dataFim = LocalDateTime.now();
        LocalDateTime dataInicio = dataFim.minusDays(periodo.getDias());
        
        logger.debug("Período: {} até {}", dataInicio, dataFim);
        
        // Buscar dados financeiros do banco
        BigDecimal faturamentoTotal = agendamentoRepository.calcularFaturamentoPorPeriodo(
                barbeariaId, dataInicio, dataFim);
        
        Long totalAgendamentos = agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(
                barbeariaId, dataInicio, dataFim);
        
        logger.debug("Faturamento: R$ {}, Total agendamentos: {}", faturamentoTotal, totalAgendamentos);
        
        // Calcular métricas
        BigDecimal ticketMedio = calcularTicketMedio(faturamentoTotal, totalAgendamentos);
        BigDecimal faturamentoPorDia = calcularFaturamentoPorDia(faturamentoTotal, periodo.getDias());
        
        // Buscar serviços mais rentáveis
        List<ServicoRentabilidadeDto> servicosMaisRentaveis = buscarServicosMaisRentaveis(
                barbeariaId, dataInicio, dataFim, faturamentoTotal);
        
        logger.info("Relatório gerado com sucesso - Faturamento total: R$ {}", faturamentoTotal);
        
        return new RelatorioFinanceiroDto(
                periodo,
                dataInicio,
                dataFim,
                faturamentoTotal,
                totalAgendamentos,
                ticketMedio,
                faturamentoPorDia,
                servicosMaisRentaveis
        );
    }
    
    /**
     * Calcula o ticket médio (valor médio por agendamento).
     * 
     * @param faturamentoTotal Faturamento total do período
     * @param totalAgendamentos Quantidade de agendamentos concluídos
     * @return Ticket médio arredondado para 2 casas decimais, ou 0 se não houver agendamentos
     */
    private BigDecimal calcularTicketMedio(BigDecimal faturamentoTotal, Long totalAgendamentos) {
        if (totalAgendamentos == null || totalAgendamentos == 0) {
            return BigDecimal.ZERO;
        }
        
        return faturamentoTotal
                .divide(BigDecimal.valueOf(totalAgendamentos), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calcula o faturamento médio por dia do período.
     * 
     * @param faturamentoTotal Faturamento total do período
     * @param dias Quantidade de dias do período
     * @return Faturamento médio por dia arredondado para 2 casas decimais
     */
    private BigDecimal calcularFaturamentoPorDia(BigDecimal faturamentoTotal, int dias) {
        return faturamentoTotal
                .divide(BigDecimal.valueOf(dias), 2, RoundingMode.HALF_UP);
    }
    
    /**
     * Busca os 5 serviços mais rentáveis do período e calcula percentuais.
     * 
     * @param barbeariaId ID da barbearia
     * @param dataInicio Data inicial do período
     * @param dataFim Data final do período
     * @param faturamentoTotalPeriodo Faturamento total do período (para cálculo de %)
     * @return Lista de DTOs com dados de rentabilidade dos serviços
     */
    private List<ServicoRentabilidadeDto> buscarServicosMaisRentaveis(
            Long barbeariaId, 
            LocalDateTime dataInicio, 
            LocalDateTime dataFim,
            BigDecimal faturamentoTotalPeriodo) {
        
        List<Object[]> resultados = agendamentoRepository.buscarServicosMaisRentaveis(
                barbeariaId, dataInicio, dataFim);
        
        List<ServicoRentabilidadeDto> servicosRentaveis = new ArrayList<>();
        
        for (Object[] resultado : resultados) {
            Long servicoId = ((Number) resultado[0]).longValue();
            String servicoNome = (String) resultado[1];
            Long totalRealizacoes = ((Number) resultado[2]).longValue();
            BigDecimal faturamentoServico = (BigDecimal) resultado[3];
            
            // Calcular percentual do faturamento total
            BigDecimal percentual = calcularPercentual(faturamentoServico, faturamentoTotalPeriodo);
            
            servicosRentaveis.add(new ServicoRentabilidadeDto(
                    servicoId,
                    servicoNome,
                    totalRealizacoes,
                    faturamentoServico,
                    percentual
            ));
        }
        
        logger.debug("Encontrados {} serviços rentáveis", servicosRentaveis.size());
        
        return servicosRentaveis;
    }
    
    /**
     * Calcula o percentual de um valor em relação ao total.
     * 
     * @param valor Valor parcial
     * @param total Valor total
     * @return Percentual arredondado para 2 casas decimais, ou 0 se total for zero
     */
    private BigDecimal calcularPercentual(BigDecimal valor, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return valor
                .multiply(BigDecimal.valueOf(100))
                .divide(total, 2, RoundingMode.HALF_UP);
    }
}
