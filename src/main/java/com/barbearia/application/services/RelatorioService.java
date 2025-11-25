package com.barbearia.application.services;

import com.barbearia.application.dto.DashboardMetricasDto;
import com.barbearia.application.dto.HorarioPicoDto;
import com.barbearia.application.dto.ServicoPopularDto;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.BarbeariaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service para geração de relatórios e métricas de dashboard.
 * 
 * Responsabilidades:
 * - Calcular métricas gerais do dashboard
 * - Gerar ranking de serviços mais populares
 * - Identificar horários de pico
 * 
 * @author Sua Barbearia Team
 */
@Service
public class RelatorioService {

        private final AgendamentoRepository agendamentoRepository;
        private final BarbeariaRepository barbeariaRepository;

        public RelatorioService(AgendamentoRepository agendamentoRepository,
                        BarbeariaRepository barbeariaRepository) {
                this.agendamentoRepository = agendamentoRepository;
                this.barbeariaRepository = barbeariaRepository;
        }

        /**
         * Obtém métricas gerais do dashboard da barbearia.
         * 
         * @param barbeariaId ID da barbearia
         * @return DTO com métricas do dashboard
         */
        public DashboardMetricasDto obterMetricasDashboard(Long barbeariaId) {
                // Valida se barbearia existe
                @SuppressWarnings({ "unused", "null" })
                var barbearia = barbeariaRepository.findById(barbeariaId)
                                .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada"));

                // Define início e fim do mês atual
                YearMonth mesAtual = YearMonth.now();
                LocalDate inicioDoMes = mesAtual.atDay(1);
                LocalDate fimDoMes = mesAtual.atEndOfMonth();
                LocalDateTime inicioMesDateTime = inicioDoMes.atStartOfDay();
                LocalDateTime fimMesDateTime = fimDoMes.atTime(23, 59, 59);

                // Busca agendamentos do mês
                List<JpaAgendamento> agendamentosMes = agendamentoRepository
                                .findByFuncionarioBarbeariaIdAndDataBetween(
                                                barbeariaId,
                                                inicioMesDateTime,
                                                fimMesDateTime);

                // Total de clientes únicos
                Set<Long> clientesUnicos = agendamentosMes.stream()
                                .map(JpaAgendamento::getClienteId)
                                .filter(Objects::nonNull)
                                .collect(Collectors.toSet());

                // Agendamentos do mês (total)
                int totalAgendamentosMes = agendamentosMes.size();

                // Receita média usando a query nativa que já existe
                BigDecimal faturamentoTotal = agendamentoRepository.calcularFaturamentoPorPeriodo(
                                barbeariaId,
                                inicioMesDateTime,
                                fimMesDateTime);

                Long totalConcluidos = agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(
                                barbeariaId,
                                inicioMesDateTime,
                                fimMesDateTime);

                BigDecimal receitaMedia = BigDecimal.ZERO;
                if (totalConcluidos != null && totalConcluidos > 0) {
                        receitaMedia = faturamentoTotal.divide(
                                        BigDecimal.valueOf(totalConcluidos),
                                        2,
                                        RoundingMode.HALF_UP);
                }

                // Taxa de cancelamento
                long totalCancelados = agendamentosMes.stream()
                                .filter(a -> a.getStatus() == StatusAgendamento.CANCELADO)
                                .count();

                double taxaCancelamento = 0.0;
                if (totalAgendamentosMes > 0) {
                        taxaCancelamento = (totalCancelados * 100.0) / totalAgendamentosMes;
                        taxaCancelamento = Math.round(taxaCancelamento * 100.0) / 100.0;
                }

                return new DashboardMetricasDto(
                                clientesUnicos.size(),
                                totalAgendamentosMes,
                                receitaMedia,
                                taxaCancelamento);
        }

        /**
         * Obtém ranking de serviços mais populares.
         * 
         * @param barbeariaId ID da barbearia
         * @param periodo     Período para análise ("MES", "TRIMESTRE", "ANO")
         * @return Lista de serviços ordenada por popularidade (top 10)
         */
        public List<ServicoPopularDto> obterServicosPopulares(Long barbeariaId, String periodo) {
                // Valida se barbearia existe
                @SuppressWarnings({ "unused", "null" })
                var barbearia = barbeariaRepository.findById(barbeariaId)
                                .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada"));

                // Define período de análise
                LocalDateTime dataInicio = calcularDataInicio(periodo);
                LocalDateTime dataFim = LocalDateTime.now();

                // Usa a query nativa que já existe no repository
                List<Object[]> resultados = agendamentoRepository.buscarServicosMaisRentaveis(
                                barbeariaId,
                                dataInicio,
                                dataFim);

                // Converte resultados para DTOs
                return resultados.stream()
                                .map(r -> {
                                        Long servicoId = ((Number) r[0]).longValue();
                                        String servicoNome = (String) r[1];
                                        Integer totalAgendamentos = ((Number) r[2]).intValue();
                                        BigDecimal receitaTotal = (BigDecimal) r[3];

                                        return new ServicoPopularDto(servicoId, servicoNome, totalAgendamentos,
                                                        receitaTotal);
                                })
                                .limit(10)
                                .collect(Collectors.toList());
        }

        /**
         * Obtém horários de pico (distribuição de agendamentos por hora).
         * 
         * @param barbeariaId ID da barbearia
         * @return Lista de faixas horárias com estatísticas
         */
        public List<HorarioPicoDto> obterHorariosPico(Long barbeariaId) {
                // Valida se barbearia existe
                @SuppressWarnings({ "unused", "null" })
                var barbearia = barbeariaRepository.findById(barbeariaId)
                                .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada"));

                // Busca agendamentos dos últimos 30 dias
                LocalDateTime dataInicio = LocalDateTime.now().minusDays(30);
                LocalDateTime dataFim = LocalDateTime.now();

                List<JpaAgendamento> agendamentos = agendamentoRepository
                                .findByFuncionarioBarbeariaIdAndDataBetween(
                                                barbeariaId,
                                                dataInicio,
                                                dataFim);

                // Agrupa por faixa horária (hora)
                Map<Integer, Integer> distribuicaoPorHora = new TreeMap<>();

                for (JpaAgendamento agendamento : agendamentos) {
                        int hora = agendamento.getDataHora().getHour();
                        distribuicaoPorHora.put(hora, distribuicaoPorHora.getOrDefault(hora, 0) + 1);
                }

                int totalAgendamentos = agendamentos.size();

                // Converte para DTOs
                List<HorarioPicoDto> resultado = new ArrayList<>();

                for (Map.Entry<Integer, Integer> entry : distribuicaoPorHora.entrySet()) {
                        int hora = entry.getKey();
                        int total = entry.getValue();

                        String faixaHorario = String.format("%02d:00-%02d:00", hora, hora + 1);

                        double percentual = 0.0;
                        if (totalAgendamentos > 0) {
                                percentual = (total * 100.0) / totalAgendamentos;
                                percentual = Math.round(percentual * 100.0) / 100.0;
                        }

                        resultado.add(new HorarioPicoDto(faixaHorario, total, percentual));
                }

                return resultado;
        }

        /**
         * Calcula data de início baseado no período.
         */
        private LocalDateTime calcularDataInicio(String periodo) {
                LocalDateTime agora = LocalDateTime.now();

                return switch (periodo != null ? periodo.toUpperCase() : "MES") {
                        case "TRIMESTRE" -> agora.minusMonths(3);
                        case "ANO" -> agora.minusYears(1);
                        default -> agora.minusMonths(1); // MES
                };
        }
}
