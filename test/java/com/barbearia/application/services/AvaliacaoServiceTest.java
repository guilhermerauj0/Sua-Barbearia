package com.barbearia.application.services;

import com.barbearia.application.dto.AvaliacaoRequestDto;
import com.barbearia.application.dto.AvaliacaoResponseDto;
import com.barbearia.application.dto.EstatisticasAvaliacoesDto;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaAvaliacao;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.AvaliacaoRepository;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AvaliacaoService - Testes Unitários")
@SuppressWarnings("null")
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private AvaliacaoService avaliacaoService;

    private JpaAgendamento agendamento;
    private JpaCliente cliente;
    private AvaliacaoRequestDto requestDto;

    @BeforeEach
    void setUp() {
        cliente = new JpaCliente();
        cliente.setId(1L);
        cliente.setNome("João Silva");

        agendamento = new JpaAgendamento();
        agendamento.setId(1L);
        agendamento.setClienteId(1L);
        agendamento.setBarbeariaId(1L);
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        agendamento.setDataHora(LocalDateTime.now().minusDays(1));

        requestDto = new AvaliacaoRequestDto();
        requestDto.setAgendamentoId(1L);
        requestDto.setBarbeariaId(1L);
        requestDto.setNotaServico(5);
        requestDto.setNotaAmbiente(4);
        requestDto.setNotaLimpeza(5);
        requestDto.setNotaAtendimento(5);
        requestDto.setComentario("Ótimo serviço!");
    }

    @Test
    @DisplayName("Deve criar avaliação com sucesso quando agendamento concluído")
    void deveCriarAvaliacaoComSucesso() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(avaliacaoRepository.existsByAgendamentoId(1L)).thenReturn(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        JpaAvaliacao avaliacaoSalva = new JpaAvaliacao(
                1L, 1L, 1L, 5, 4, 5, 5, "Ótimo serviço!");
        avaliacaoSalva.setId(1L);
        avaliacaoSalva.setDataCriacao(LocalDateTime.now());
        // Simula o cálculo do @PrePersist
        avaliacaoSalva.setNotaGeral(new BigDecimal("4.75"));

        when(avaliacaoRepository.save(any(JpaAvaliacao.class))).thenReturn(avaliacaoSalva);

        // Act
        AvaliacaoResponseDto response = avaliacaoService.criarAvaliacao(1L, requestDto);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("João Silva", response.getClienteNome());
        assertEquals(new BigDecimal("4.75"), response.getNotaGeral());
        verify(avaliacaoRepository).save(any(JpaAvaliacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não encontrado")
    void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> avaliacaoService.criarAvaliacao(1L, requestDto));
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não pertence ao cliente")
    void deveLancarExcecaoQuandoAgendamentoNaoPertenceAoCliente() {
        // Arrange
        agendamento.setClienteId(2L); // Outro cliente
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> avaliacaoService.criarAvaliacao(1L, requestDto));
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não está concluído")
    void deveLancarExcecaoQuandoAgendamentoNaoConcluido() {
        // Arrange
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> avaliacaoService.criarAvaliacao(1L, requestDto));
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento já foi avaliado")
    void deveLancarExcecaoQuandoAgendamentoJaAvaliado() {
        // Arrange
        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(avaliacaoRepository.existsByAgendamentoId(1L)).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> avaliacaoService.criarAvaliacao(1L, requestDto));
    }

    @Test
    @DisplayName("Deve listar avaliações por barbearia")
    void deveListarAvaliacoesPorBarbearia() {
        // Arrange
        JpaAvaliacao avaliacao = new JpaAvaliacao(1L, 1L, 1L, 5, 5, 5, 5, "Top");
        avaliacao.setId(1L);
        avaliacao.setNotaGeral(new BigDecimal("5.00"));
        avaliacao.setDataCriacao(LocalDateTime.now());

        when(avaliacaoRepository.findByBarbeariaIdOrderByDataCriacaoDesc(1L))
                .thenReturn(List.of(avaliacao));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Act
        List<AvaliacaoResponseDto> lista = avaliacaoService.buscarAvaliacoesPorBarbearia(1L);

        // Assert
        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals("João Silva", lista.get(0).getClienteNome());
    }

    @Test
    @DisplayName("Deve calcular estatísticas corretamente")
    void deveCalcularEstatisticas() {
        // Arrange
        when(avaliacaoRepository.calcularMediaGeral(1L)).thenReturn(4.5);
        when(avaliacaoRepository.calcularMediaServico(1L)).thenReturn(4.0);
        when(avaliacaoRepository.calcularMediaAmbiente(1L)).thenReturn(5.0);
        when(avaliacaoRepository.calcularMediaLimpeza(1L)).thenReturn(4.5);
        when(avaliacaoRepository.calcularMediaAtendimento(1L)).thenReturn(4.5);
        when(avaliacaoRepository.countByBarbeariaId(1L)).thenReturn(10L);

        when(avaliacaoRepository.countByBarbeariaIdAndNotaGeral(1L, 5)).thenReturn(5L);
        when(avaliacaoRepository.countByBarbeariaIdAndNotaGeral(1L, 4)).thenReturn(3L);
        when(avaliacaoRepository.countByBarbeariaIdAndNotaGeral(1L, 3)).thenReturn(2L);
        when(avaliacaoRepository.countByBarbeariaIdAndNotaGeral(1L, 2)).thenReturn(0L);
        when(avaliacaoRepository.countByBarbeariaIdAndNotaGeral(1L, 1)).thenReturn(0L);

        // Act
        EstatisticasAvaliacoesDto stats = avaliacaoService.calcularEstatisticas(1L);

        // Assert
        assertNotNull(stats);
        assertEquals(4.5, stats.getMediaGeral());
        assertEquals(10L, stats.getTotalAvaliacoes());
        assertEquals(5L, stats.getAvaliacoes5Estrelas());
    }
}
