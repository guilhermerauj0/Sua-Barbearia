package com.barbearia.application.services;

import com.barbearia.application.dto.DashboardMetricasDto;
import com.barbearia.application.dto.HorarioPicoDto;
import com.barbearia.application.dto.ServicoPopularDto;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaBarbearia;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.BarbeariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RelatorioService - Testes")
class RelatorioServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private BarbeariaRepository barbeariaRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    private JpaBarbearia barbearia;

    @BeforeEach
    void setUp() {
        barbearia = new JpaBarbearia();
        barbearia.setId(1L);
        barbearia.setNome("Barbearia Teste");
    }

    @Test
    @DisplayName("Deve obter métricas do dashboard com sucesso")
    void deveObterMetricasDashboard() {
        // Arrange
        when(barbeariaRepository.findById(1L)).thenReturn(Optional.of(barbearia));

        List<JpaAgendamento> agendamentos = new ArrayList<>();
        JpaAgendamento a1 = new JpaAgendamento();
        a1.setClienteId(10L);
        a1.setStatus(StatusAgendamento.CONCLUIDO);
        agendamentos.add(a1);

        JpaAgendamento a2 = new JpaAgendamento();
        a2.setClienteId(11L);
        a2.setStatus(StatusAgendamento.CANCELADO);
        agendamentos.add(a2);

        when(agendamentoRepository.findByFuncionarioBarbeariaIdAndDataBetween(anyLong(), any(), any()))
                .thenReturn(agendamentos);

        when(agendamentoRepository.calcularFaturamentoPorPeriodo(anyLong(), any(), any()))
                .thenReturn(new BigDecimal("100.00"));

        when(agendamentoRepository.contarAgendamentosConcluidosPorPeriodo(anyLong(), any(), any()))
                .thenReturn(1L);

        // Act
        DashboardMetricasDto metricas = relatorioService.obterMetricasDashboard(1L);

        // Assert
        assertNotNull(metricas);
        assertEquals(2, metricas.getAgendamentosMes());
        assertEquals(2, metricas.getTotalClientes());
        assertEquals(new BigDecimal("100.00"), metricas.getReceitaMedia());
        assertEquals(50.0, metricas.getTaxaCancelamento());
    }

    @Test
    @DisplayName("Deve obter serviços populares com sucesso")
    void deveObterServicosPopulares() {
        // Arrange
        when(barbeariaRepository.findById(1L)).thenReturn(Optional.of(barbearia));

        List<Object[]> resultados = new ArrayList<>();
        resultados.add(new Object[] { 1L, "Corte", 10, new BigDecimal("500.00") });

        when(agendamentoRepository.buscarServicosMaisRentaveis(anyLong(), any(), any()))
                .thenReturn(resultados);

        // Act
        List<ServicoPopularDto> populares = relatorioService.obterServicosPopulares(1L, "MES");

        // Assert
        assertFalse(populares.isEmpty());
        assertEquals(1, populares.size());
        assertEquals("Corte", populares.get(0).getServicoNome());
    }

    @Test
    @DisplayName("Deve obter horários de pico com sucesso")
    void deveObterHorariosPico() {
        // Arrange
        when(barbeariaRepository.findById(1L)).thenReturn(Optional.of(barbearia));

        List<JpaAgendamento> agendamentos = new ArrayList<>();
        JpaAgendamento a1 = new JpaAgendamento();
        a1.setDataHora(LocalDateTime.of(2024, 1, 1, 10, 0));
        agendamentos.add(a1);

        when(agendamentoRepository.findByFuncionarioBarbeariaIdAndDataBetween(anyLong(), any(), any()))
                .thenReturn(agendamentos);

        // Act
        List<HorarioPicoDto> picos = relatorioService.obterHorariosPico(1L);

        // Assert
        assertFalse(picos.isEmpty());
        assertEquals(1, picos.size());
        assertEquals("10:00-11:00", picos.get(0).getFaixaHorario());
        assertEquals(100.0, picos.get(0).getPercentual());
    }
}
