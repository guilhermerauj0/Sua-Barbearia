package com.barbearia.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barbearia.application.dto.RelatorioFinanceiroDto;
import com.barbearia.application.dto.RelatorioFinanceiroDto.ServicoRentabilidadeDto;
import com.barbearia.domain.enums.PeriodoRelatorio;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.DespesaRepository;
import com.barbearia.infrastructure.persistence.repositories.ReceitaRepository;

/**
 * Testes unitários para FinanceiroService.
 * 
 * Valida:
 * - Geração de relatórios por diferentes períodos
 * - Cálculos de métricas financeiras (ticket médio, faturamento por dia)
 * - Ordenação de serviços mais rentáveis
 * - Cálculo de percentuais de faturamento
 * - Tratamento de casos sem dados
 * - Validação de parâmetros
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FinanceiroService - Testes de Gestão Financeira")
class FinanceiroServiceTest {

        @Mock
        private AgendamentoRepository agendamentoRepository;

        @Mock
        private ReceitaRepository receitaRepository;

        @Mock
        private DespesaRepository despesaRepository;

        @InjectMocks
        private FinanceiroService financeiroService;

        private Long barbeariaId;

        @BeforeEach
        void setUp() {
                barbeariaId = 1L;
        }

        @Test
        @DisplayName("Deve gerar relatório financeiro diário com sucesso")
        void deveGerarRelatorioFinanceiroDiario() {
                // Arrange
                PeriodoRelatorio periodo = PeriodoRelatorio.DIA;
                BigDecimal faturamentoTotal = new BigDecimal("500.00");
                Long totalAgendamentos = 10L;

                when(agendamentoRepository.calcularFaturamentoPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(faturamentoTotal);

                when(agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(totalAgendamentos);

                when(agendamentoRepository.buscarServicosMaisRentaveis(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(criarListaServicosMock(faturamentoTotal));

                // Act
                RelatorioFinanceiroDto relatorio = financeiroService.gerarRelatorioFinanceiro(
                                barbeariaId, periodo);

                // Assert
                assertNotNull(relatorio);
                assertEquals(periodo, relatorio.periodo());
                assertEquals(faturamentoTotal, relatorio.faturamentoTotal());
                assertEquals(totalAgendamentos, relatorio.totalAgendamentos());
                assertEquals(new BigDecimal("50.00"), relatorio.ticketMedio()); // 500 / 10
                assertEquals(faturamentoTotal, relatorio.faturamentoPorDia()); // 500 / 1 dia
                assertNotNull(relatorio.servicosMaisRentaveis());
        }

        @Test
        @DisplayName("Deve gerar relatório financeiro semanal com sucesso")
        void deveGerarRelatorioFinanceiroSemanal() {
                // Arrange
                PeriodoRelatorio periodo = PeriodoRelatorio.SEMANA;
                BigDecimal faturamentoTotal = new BigDecimal("3500.00");
                Long totalAgendamentos = 70L;

                when(agendamentoRepository.calcularFaturamentoPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(faturamentoTotal);

                when(agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(totalAgendamentos);

                when(agendamentoRepository.buscarServicosMaisRentaveis(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(criarListaServicosMock(faturamentoTotal));

                // Act
                RelatorioFinanceiroDto relatorio = financeiroService.gerarRelatorioFinanceiro(
                                barbeariaId, periodo);

                // Assert
                assertNotNull(relatorio);
                assertEquals(periodo, relatorio.periodo());
                assertEquals(faturamentoTotal, relatorio.faturamentoTotal());
                assertEquals(totalAgendamentos, relatorio.totalAgendamentos());
                assertEquals(new BigDecimal("50.00"), relatorio.ticketMedio()); // 3500 / 70
                assertEquals(new BigDecimal("500.00"), relatorio.faturamentoPorDia()); // 3500 / 7
        }

        @Test
        @DisplayName("Deve gerar relatório financeiro mensal com sucesso")
        void deveGerarRelatorioFinanceiroMensal() {
                // Arrange
                PeriodoRelatorio periodo = PeriodoRelatorio.MES;
                BigDecimal faturamentoTotal = new BigDecimal("15000.00");
                Long totalAgendamentos = 300L;

                when(agendamentoRepository.calcularFaturamentoPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(faturamentoTotal);

                when(agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(totalAgendamentos);

                when(agendamentoRepository.buscarServicosMaisRentaveis(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(criarListaServicosMock(faturamentoTotal));

                // Act
                RelatorioFinanceiroDto relatorio = financeiroService.gerarRelatorioFinanceiro(
                                barbeariaId, periodo);

                // Assert
                assertNotNull(relatorio);
                assertEquals(periodo, relatorio.periodo());
                assertEquals(faturamentoTotal, relatorio.faturamentoTotal());
                assertEquals(totalAgendamentos, relatorio.totalAgendamentos());
                assertEquals(new BigDecimal("50.00"), relatorio.ticketMedio()); // 15000 / 300
                assertEquals(new BigDecimal("500.00"), relatorio.faturamentoPorDia()); // 15000 / 30
        }

        @Test
        @DisplayName("Deve retornar ticket médio zero quando não houver agendamentos")
        void deveRetornarTicketMedioZeroSemAgendamentos() {
                // Arrange
                when(agendamentoRepository.calcularFaturamentoPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(BigDecimal.ZERO);

                when(agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(0L);

                when(agendamentoRepository.buscarServicosMaisRentaveis(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(new ArrayList<>());

                // Act
                RelatorioFinanceiroDto relatorio = financeiroService.gerarRelatorioFinanceiro(
                                barbeariaId, PeriodoRelatorio.DIA);

                // Assert
                assertEquals(BigDecimal.ZERO, relatorio.faturamentoTotal());
                assertEquals(0L, relatorio.totalAgendamentos());
                assertEquals(BigDecimal.ZERO, relatorio.ticketMedio());
                assertTrue(relatorio.servicosMaisRentaveis().isEmpty());
        }

        @Test
        @DisplayName("Deve calcular percentuais corretos dos serviços mais rentáveis")
        void deveCalcularPercentuaisCorretos() {
                // Arrange
                BigDecimal faturamentoTotal = new BigDecimal("1000.00");

                when(agendamentoRepository.calcularFaturamentoPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(faturamentoTotal);

                when(agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(20L);

                // Serviço 1: R$ 600 (60%)
                // Serviço 2: R$ 300 (30%)
                // Serviço 3: R$ 100 (10%)
                List<Object[]> servicosMock = List.of(
                                new Object[] { 1L, "Corte Premium", 12L, new BigDecimal("600.00") },
                                new Object[] { 2L, "Barba", 10L, new BigDecimal("300.00") },
                                new Object[] { 3L, "Sombrancelha", 5L, new BigDecimal("100.00") });

                when(agendamentoRepository.buscarServicosMaisRentaveis(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(servicosMock);

                // Act
                RelatorioFinanceiroDto relatorio = financeiroService.gerarRelatorioFinanceiro(
                                barbeariaId, PeriodoRelatorio.DIA);

                // Assert
                List<ServicoRentabilidadeDto> servicos = relatorio.servicosMaisRentaveis();
                assertEquals(3, servicos.size());

                // Verificar percentuais
                assertEquals(new BigDecimal("60.00"), servicos.get(0).percentualFaturamento());
                assertEquals(new BigDecimal("30.00"), servicos.get(1).percentualFaturamento());
                assertEquals(new BigDecimal("10.00"), servicos.get(2).percentualFaturamento());
        }

        @Test
        @DisplayName("Deve ordenar serviços por faturamento decrescente")
        void deveOrdenarServicosPorFaturamento() {
                // Arrange
                BigDecimal faturamentoTotal = new BigDecimal("1000.00");

                when(agendamentoRepository.calcularFaturamentoPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(faturamentoTotal);

                when(agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(20L);

                // Query já retorna ordenado do banco
                List<Object[]> servicosMock = List.of(
                                new Object[] { 1L, "Corte Premium", 12L, new BigDecimal("600.00") },
                                new Object[] { 2L, "Barba", 10L, new BigDecimal("300.00") },
                                new Object[] { 3L, "Sombrancelha", 5L, new BigDecimal("100.00") });

                when(agendamentoRepository.buscarServicosMaisRentaveis(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(servicosMock);

                // Act
                RelatorioFinanceiroDto relatorio = financeiroService.gerarRelatorioFinanceiro(
                                barbeariaId, PeriodoRelatorio.DIA);

                // Assert
                List<ServicoRentabilidadeDto> servicos = relatorio.servicosMaisRentaveis();
                assertTrue(servicos.get(0).faturamentoTotal().compareTo(servicos.get(1).faturamentoTotal()) >= 0);
                assertTrue(servicos.get(1).faturamentoTotal().compareTo(servicos.get(2).faturamentoTotal()) >= 0);
        }

        @Test
        @DisplayName("Deve lançar exceção quando barbeariaId for nulo")
        void deveLancarExcecaoQuandoBarbeariaIdNulo() {
                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> {
                        financeiroService.gerarRelatorioFinanceiro(null, PeriodoRelatorio.DIA);
                });
        }

        @Test
        @DisplayName("Deve lançar exceção quando período for nulo")
        void deveLancarExcecaoQuandoPeriodoNulo() {
                // Act & Assert
                assertThrows(IllegalArgumentException.class, () -> {
                        financeiroService.gerarRelatorioFinanceiro(barbeariaId, null);
                });
        }

        @Test
        @DisplayName("Deve arredondar valores corretamente para 2 casas decimais")
        void deveArredondarValoresCorretamente() {
                // Arrange
                BigDecimal faturamentoTotal = new BigDecimal("100.00");
                Long totalAgendamentos = 3L; // 100 / 3 = 33.33...

                when(agendamentoRepository.calcularFaturamentoPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(faturamentoTotal);

                when(agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(totalAgendamentos);

                when(agendamentoRepository.buscarServicosMaisRentaveis(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(new ArrayList<>());

                // Act
                RelatorioFinanceiroDto relatorio = financeiroService.gerarRelatorioFinanceiro(
                                barbeariaId, PeriodoRelatorio.DIA);

                // Assert
                BigDecimal ticketEsperado = new BigDecimal("100.00")
                                .divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
                assertEquals(ticketEsperado, relatorio.ticketMedio());
                assertEquals(2, relatorio.ticketMedio().scale());
        }

        @Test
        @DisplayName("Deve incluir datas de início e fim no relatório")
        void deveIncluirDatasNoRelatorio() {
                // Arrange
                when(agendamentoRepository.calcularFaturamentoPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(BigDecimal.ZERO);

                when(agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(0L);

                when(agendamentoRepository.buscarServicosMaisRentaveis(
                                eq(barbeariaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(new ArrayList<>());

                // Act
                RelatorioFinanceiroDto relatorio = financeiroService.gerarRelatorioFinanceiro(
                                barbeariaId, PeriodoRelatorio.SEMANA);

                // Assert
                assertNotNull(relatorio.dataInicio());
                assertNotNull(relatorio.dataFim());
                assertTrue(relatorio.dataInicio().isBefore(relatorio.dataFim()));
        }

        // Métodos auxiliares

        private List<Object[]> criarListaServicosMock(BigDecimal faturamentoTotal) {
                List<Object[]> servicos = new ArrayList<>();
                servicos.add(new Object[] {
                                1L,
                                "Corte Masculino",
                                50L,
                                faturamentoTotal.multiply(new BigDecimal("0.6"))
                });
                servicos.add(new Object[] {
                                2L,
                                "Barba",
                                30L,
                                faturamentoTotal.multiply(new BigDecimal("0.3"))
                });
                servicos.add(new Object[] {
                                3L,
                                "Sombrancelha",
                                10L,
                                faturamentoTotal.multiply(new BigDecimal("0.1"))
                });
                return servicos;
        }
}
