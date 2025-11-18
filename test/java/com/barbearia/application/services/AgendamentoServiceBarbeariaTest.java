package com.barbearia.application.services;

import com.barbearia.application.dto.AgendamentoBarbeariaDto;
import com.barbearia.application.dto.AgendamentoResponseDto;
import com.barbearia.application.dto.AgendamentoUpdateDto;
import com.barbearia.application.observers.AgendamentoObserver;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import com.barbearia.domain.exceptions.AgendamentoNaoEncontradoException;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionarioBarbeiro;
import com.barbearia.infrastructure.persistence.entities.JpaServico;
import com.barbearia.infrastructure.persistence.entities.JpaServicoCorte;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import com.barbearia.infrastructure.persistence.repositories.ProfissionalServicoRepository;
import com.barbearia.infrastructure.persistence.repositories.ServicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para os novos métodos da T13 - Gestão de Agendamentos da Barbearia.
 * 
 * Testa:
 * - Listagem de agendamentos da barbearia (com e sem filtro de data)
 * - Atualização de status de agendamento
 * - Validações de propriedade
 * - Validações de transições de status
 * - Notificações via observers
 */
@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("AgendamentoService - Gestão Barbearia (T13)")
class AgendamentoServiceBarbeariaTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProfissionalServicoRepository profissionalServicoRepository;

    @Mock
    private AgendamentoObserver observer;

    private AgendamentoService agendamentoService;
    
    private Long barbeariaId;
    private JpaAgendamento agendamento;
    private JpaCliente cliente;
    private JpaServico servico;
    private JpaFuncionario funcionario;

    @BeforeEach
    void setUp() {
        List<AgendamentoObserver> observers = Arrays.asList(observer);
        agendamentoService = new AgendamentoService(
            agendamentoRepository,
            funcionarioRepository,
            servicoRepository,
            clienteRepository,
            profissionalServicoRepository,
            observers
        );

        barbeariaId = 1L;

        // Cliente
        cliente = new JpaCliente();
        cliente.setId(10L);
        cliente.setNome("João Silva");
        cliente.setTelefone("(11) 98765-4321");

        // Serviço
        servico = new JpaServicoCorte();
        servico.setId(5L);
        servico.setNome("Corte Degradê");
        servico.setTipoServico("CORTE");
        servico.setPreco(BigDecimal.valueOf(50.00));
        servico.setDuracao(30);

        // Funcionário
        funcionario = new JpaFuncionarioBarbeiro();
        funcionario.setId(3L);
        funcionario.setNome("Carlos Barbeiro");
        funcionario.setBarbeariaId(barbeariaId);

        // Agendamento
        agendamento = new JpaAgendamento();
        agendamento.setId(100L);
        agendamento.setClienteId(cliente.getId());
        agendamento.setBarbeariaId(barbeariaId);
        agendamento.setServicoId(servico.getId());
        agendamento.setBarbeiroId(funcionario.getId());
        agendamento.setDataHora(LocalDateTime.of(2025, 12, 1, 14, 0));
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        agendamento.setObservacoes("Corte degradê");
        agendamento.setDataCriacao(LocalDateTime.now());
        agendamento.setDataAtualizacao(LocalDateTime.now());
    }

    // ==================== TESTES DE LISTAGEM ====================

    @Test
    @DisplayName("Deve listar agendamentos da barbearia com filtro de data")
    void deveListarAgendamentosComFiltroData() {
        // Arrange
        LocalDate data = LocalDate.of(2025, 12, 1);
        LocalDateTime inicioDia = data.atStartOfDay();
        LocalDateTime fimDia = data.atTime(LocalTime.MAX);

        when(agendamentoRepository.findByBarbeariaIdAndDataHoraBetweenOrderByDataHoraAsc(
            eq(barbeariaId), eq(inicioDia), eq(fimDia)))
            .thenReturn(Arrays.asList(agendamento));
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(servicoRepository.findById(servico.getId())).thenReturn(Optional.of(servico));
        when(funcionarioRepository.findById(funcionario.getId())).thenReturn(Optional.of(funcionario));

        // Act
        List<AgendamentoBarbeariaDto> resultado = 
            agendamentoService.listarAgendamentosBarbearia(barbeariaId, data);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        
        AgendamentoBarbeariaDto dto = resultado.get(0);
        assertEquals(agendamento.getId(), dto.id());
        assertEquals(cliente.getNome(), dto.clienteNome());
        assertEquals(servico.getNome(), dto.servicoNome());
        assertEquals(funcionario.getNome(), dto.funcionarioNome());
        assertEquals(StatusAgendamento.PENDENTE, dto.status());

        verify(agendamentoRepository).findByBarbeariaIdAndDataHoraBetweenOrderByDataHoraAsc(
            eq(barbeariaId), eq(inicioDia), eq(fimDia));
    }

    @Test
    @DisplayName("Deve listar todos os agendamentos da barbearia sem filtro")
    void deveListarTodosAgendamentosSemFiltro() {
        // Arrange
        when(agendamentoRepository.findByBarbeariaIdOrderByDataHoraDesc(barbeariaId))
            .thenReturn(Arrays.asList(agendamento));
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));
        when(servicoRepository.findById(servico.getId())).thenReturn(Optional.of(servico));
        when(funcionarioRepository.findById(funcionario.getId())).thenReturn(Optional.of(funcionario));

        // Act
        List<AgendamentoBarbeariaDto> resultado = 
            agendamentoService.listarAgendamentosBarbearia(barbeariaId, null);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(agendamentoRepository).findByBarbeariaIdOrderByDataHoraDesc(barbeariaId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há agendamentos")
    void deveRetornarListaVaziaQuandoSemAgendamentos() {
        // Arrange
        when(agendamentoRepository.findByBarbeariaIdOrderByDataHoraDesc(barbeariaId))
            .thenReturn(Collections.emptyList());

        // Act
        List<AgendamentoBarbeariaDto> resultado = 
            agendamentoService.listarAgendamentosBarbearia(barbeariaId, null);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // ==================== TESTES DE ATUALIZAÇÃO DE STATUS ====================

    @Test
    @DisplayName("Deve atualizar status do agendamento com sucesso")
    void deveAtualizarStatusComSucesso() {
        // Arrange
        AgendamentoUpdateDto updateDto = new AgendamentoUpdateDto(StatusAgendamento.CONFIRMADO);
        
        when(agendamentoRepository.findById(agendamento.getId()))
            .thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(JpaAgendamento.class)))
            .thenReturn(agendamento);

        // Act
        AgendamentoResponseDto resultado = agendamentoService.atualizarStatusAgendamento(
            agendamento.getId(), barbeariaId, updateDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(StatusAgendamento.CONFIRMADO, agendamento.getStatus());
        verify(agendamentoRepository).save(agendamento);
        verify(observer).onStatusChanged(
            eq(agendamento.getId()),
            eq(StatusAgendamento.PENDENTE),
            eq(StatusAgendamento.CONFIRMADO),
            eq(cliente.getId()),
            eq(barbeariaId)
        );
    }

    @Test
    @DisplayName("Deve ser idempotente ao atualizar para o mesmo status")
    void deveSerIdempotenteMesmoStatus() {
        // Arrange
        AgendamentoUpdateDto updateDto = new AgendamentoUpdateDto(StatusAgendamento.PENDENTE);
        
        when(agendamentoRepository.findById(agendamento.getId()))
            .thenReturn(Optional.of(agendamento));

        // Act
        AgendamentoResponseDto resultado = agendamentoService.atualizarStatusAgendamento(
            agendamento.getId(), barbeariaId, updateDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(StatusAgendamento.PENDENTE, resultado.status());
        verify(agendamentoRepository, never()).save(any());
        verify(observer, never()).onStatusChanged(any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não existe")
    void deveLancarExcecaoQuandoAgendamentoNaoExiste() {
        // Arrange
        AgendamentoUpdateDto updateDto = new AgendamentoUpdateDto(StatusAgendamento.CONFIRMADO);
        
        when(agendamentoRepository.findById(999L))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AgendamentoNaoEncontradoException.class, () ->
            agendamentoService.atualizarStatusAgendamento(999L, barbeariaId, updateDto)
        );
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não pertence à barbearia")
    void deveLancarExcecaoQuandoAgendamentoNaoPertenceABarbearia() {
        // Arrange
        AgendamentoUpdateDto updateDto = new AgendamentoUpdateDto(StatusAgendamento.CONFIRMADO);
        Long outraBarbeariaId = 999L;
        
        when(agendamentoRepository.findById(agendamento.getId()))
            .thenReturn(Optional.of(agendamento));

        // Act & Assert
        assertThrows(AcessoNegadoException.class, () ->
            agendamentoService.atualizarStatusAgendamento(
                agendamento.getId(), outraBarbeariaId, updateDto)
        );
        verify(agendamentoRepository, never()).save(any());
    }

    // ==================== TESTES DE VALIDAÇÃO DE TRANSIÇÕES ====================

    @Test
    @DisplayName("Deve impedir confirmar agendamento cancelado")
    void deveImpedirConfirmarAgendamentoCancelado() {
        // Arrange
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        AgendamentoUpdateDto updateDto = new AgendamentoUpdateDto(StatusAgendamento.CONFIRMADO);
        
        when(agendamentoRepository.findById(agendamento.getId()))
            .thenReturn(Optional.of(agendamento));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            agendamentoService.atualizarStatusAgendamento(
                agendamento.getId(), barbeariaId, updateDto)
        );
        
        assertTrue(exception.getMessage().contains("cancelado"));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve impedir cancelar agendamento concluído")
    void deveImpedirCancelarAgendamentoConcluido() {
        // Arrange
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        AgendamentoUpdateDto updateDto = new AgendamentoUpdateDto(StatusAgendamento.CANCELADO);
        
        when(agendamentoRepository.findById(agendamento.getId()))
            .thenReturn(Optional.of(agendamento));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            agendamentoService.atualizarStatusAgendamento(
                agendamento.getId(), barbeariaId, updateDto)
        );
        
        assertTrue(exception.getMessage().contains("concluído"));
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve permitir marcar como concluído independente do status anterior")
    void devePermitirConcluirIndependenteStatus() {
        // Arrange
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        AgendamentoUpdateDto updateDto = new AgendamentoUpdateDto(StatusAgendamento.CONCLUIDO);
        
        when(agendamentoRepository.findById(agendamento.getId()))
            .thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(JpaAgendamento.class)))
            .thenReturn(agendamento);

        // Act
        AgendamentoResponseDto resultado = agendamentoService.atualizarStatusAgendamento(
            agendamento.getId(), barbeariaId, updateDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(StatusAgendamento.CONCLUIDO, agendamento.getStatus());
        verify(agendamentoRepository).save(agendamento);
    }

    @Test
    @DisplayName("Deve permitir cancelar agendamento pendente")
    void devePermitirCancelarAgendamentoPendente() {
        // Arrange
        AgendamentoUpdateDto updateDto = new AgendamentoUpdateDto(StatusAgendamento.CANCELADO);
        
        when(agendamentoRepository.findById(agendamento.getId()))
            .thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(JpaAgendamento.class)))
            .thenReturn(agendamento);

        // Act
        AgendamentoResponseDto resultado = agendamentoService.atualizarStatusAgendamento(
            agendamento.getId(), barbeariaId, updateDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(StatusAgendamento.CANCELADO, agendamento.getStatus());
        verify(agendamentoRepository).save(agendamento);
    }

    // ==================== TESTES DE NOTIFICAÇÃO ====================

    @Test
    @DisplayName("Deve notificar observers quando status muda")
    void deveNotificarObserversQuandoStatusMuda() {
        // Arrange
        AgendamentoUpdateDto updateDto = new AgendamentoUpdateDto(StatusAgendamento.CONFIRMADO);
        
        when(agendamentoRepository.findById(agendamento.getId()))
            .thenReturn(Optional.of(agendamento));
        when(agendamentoRepository.save(any(JpaAgendamento.class)))
            .thenReturn(agendamento);

        // Act
        agendamentoService.atualizarStatusAgendamento(
            agendamento.getId(), barbeariaId, updateDto);

        // Assert
        verify(observer).onStatusChanged(
            eq(agendamento.getId()),
            eq(StatusAgendamento.PENDENTE),
            eq(StatusAgendamento.CONFIRMADO),
            eq(cliente.getId()),
            eq(barbeariaId)
        );
    }

    @Test
    @DisplayName("Não deve notificar observers quando status não muda (idempotência)")
    void naoDeveNotificarQuandoStatusNaoMuda() {
        // Arrange
        AgendamentoUpdateDto updateDto = new AgendamentoUpdateDto(StatusAgendamento.PENDENTE);
        
        when(agendamentoRepository.findById(agendamento.getId()))
            .thenReturn(Optional.of(agendamento));

        // Act
        agendamentoService.atualizarStatusAgendamento(
            agendamento.getId(), barbeariaId, updateDto);

        // Assert
        verify(observer, never()).onStatusChanged(any(), any(), any(), any(), any());
    }
}
