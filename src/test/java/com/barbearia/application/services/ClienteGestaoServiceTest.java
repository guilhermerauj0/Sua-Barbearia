package com.barbearia.application.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.barbearia.application.dto.ClienteAtendidoDto;
import com.barbearia.application.dto.ClienteDetalhesDto;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import com.barbearia.domain.exceptions.ClienteNaoEncontradoException;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.entities.JpaCliente;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import com.barbearia.infrastructure.persistence.repositories.ClienteRepository;

/**
 * Testes unitários para ClienteGestaoService.
 * 
 * Cobertura de testes:
 * - Listagem de clientes atendidos
 * - Busca de detalhes de cliente
 * - Anonimização LGPD
 * - Validações de segurança
 * - Tratamento de erros
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteGestaoService - Gestão de Clientes (LGPD)")
class ClienteGestaoServiceTest {
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @Mock
    private AgendamentoRepository agendamentoRepository;
    
    @InjectMocks
    private ClienteGestaoService clienteGestaoService;
    
    private JpaCliente clienteMock;
    private JpaAgendamento agendamentoMock;
    private Long barbeariaId;
    private Long clienteId;
    
    @BeforeEach
    void setUp() {
        barbeariaId = 1L;
        clienteId = 10L;
        
        clienteMock = new JpaCliente();
        clienteMock.setId(clienteId);
        clienteMock.setNome("João Silva");
        clienteMock.setEmail("joao@email.com");
        clienteMock.setTelefone("11987654321");
        clienteMock.setSenha("senha_hash");
        clienteMock.setAtivo(true);
        clienteMock.setAnonimizado(false);
        
        agendamentoMock = new JpaAgendamento();
        agendamentoMock.setId(1L);
        agendamentoMock.setClienteId(clienteId);
        agendamentoMock.setBarbeariaId(barbeariaId);
        agendamentoMock.setDataHora(LocalDateTime.now());
        agendamentoMock.setStatus(StatusAgendamento.CONCLUIDO);
    }
    
    @Test
    @DisplayName("Deve listar clientes atendidos pela barbearia")
    void deveListarClientesAtendidos() {
        // Arrange
        when(clienteRepository.findClientesAtendidosPorBarbearia(barbeariaId))
                .thenReturn(Arrays.asList(clienteMock));
        when(agendamentoRepository.findByClienteIdOrderByDataHoraDesc(clienteId))
                .thenReturn(Arrays.asList(agendamentoMock));
        
        // Act
        List<ClienteAtendidoDto> resultado = clienteGestaoService.listarClientesAtendidos(barbeariaId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        
        ClienteAtendidoDto cliente = resultado.get(0);
        assertEquals(clienteId, cliente.id());
        assertEquals("João Silva", cliente.nome());
        assertEquals("joao@email.com", cliente.email());
        assertEquals("11987654321", cliente.telefone());
        assertEquals(1L, cliente.totalAgendamentos());
        assertFalse(cliente.anonimizado());
        assertTrue(cliente.ativo());
        
        verify(clienteRepository).findClientesAtendidosPorBarbearia(barbeariaId);
        verify(agendamentoRepository).findByClienteIdOrderByDataHoraDesc(clienteId);
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia quando não há clientes atendidos")
    void deveRetornarListaVaziaQuandoNaoHaClientes() {
        // Arrange
        when(clienteRepository.findClientesAtendidosPorBarbearia(barbeariaId))
                .thenReturn(Collections.emptyList());
        
        // Act
        List<ClienteAtendidoDto> resultado = clienteGestaoService.listarClientesAtendidos(barbeariaId);
        
        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        
        verify(clienteRepository).findClientesAtendidosPorBarbearia(barbeariaId);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao listar clientes com barbeariaId nulo")
    void deveLancarExcecaoAoListarComBarbeariaIdNulo() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> clienteGestaoService.listarClientesAtendidos(null));
        
        verifyNoInteractions(clienteRepository);
    }
    
    @Test
    @DisplayName("Deve buscar detalhes de um cliente atendido")
    void deveBuscarDetalhesCliente() {
        // Arrange
        when(clienteRepository.findClienteAtendidoPorBarbearia(clienteId, barbeariaId))
                .thenReturn(Optional.of(clienteMock));
        when(agendamentoRepository.findByClienteIdOrderByDataHoraDesc(clienteId))
                .thenReturn(Arrays.asList(agendamentoMock));
        
        // Act
        ClienteDetalhesDto resultado = clienteGestaoService.buscarDetalhesCliente(clienteId, barbeariaId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(clienteId, resultado.id());
        assertEquals("João Silva", resultado.nome());
        assertEquals("joao@email.com", resultado.email());
        assertEquals("11987654321", resultado.telefone());
        assertEquals(1L, resultado.totalAgendamentos());
        assertEquals(1L, resultado.agendamentosConcluidos());
        assertEquals(0L, resultado.agendamentosCancelados());
        assertFalse(resultado.anonimizado());
        assertTrue(resultado.ativo());
        assertNotNull(resultado.agendamentos());
        assertEquals(1, resultado.agendamentos().size());
        
        verify(clienteRepository).findClienteAtendidoPorBarbearia(clienteId, barbeariaId);
        verify(agendamentoRepository).findByClienteIdOrderByDataHoraDesc(clienteId);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente não atendido pela barbearia")
    void deveLancarExcecaoAoBuscarClienteNaoAtendido() {
        // Arrange
        when(clienteRepository.findClienteAtendidoPorBarbearia(clienteId, barbeariaId))
                .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ClienteNaoEncontradoException.class,
                () -> clienteGestaoService.buscarDetalhesCliente(clienteId, barbeariaId));
        
        verify(clienteRepository).findClienteAtendidoPorBarbearia(clienteId, barbeariaId);
        verifyNoInteractions(agendamentoRepository);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao buscar detalhes com parâmetros nulos")
    void deveLancarExcecaoAoBuscarDetalhesComParametrosNulos() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> clienteGestaoService.buscarDetalhesCliente(null, barbeariaId));
        
        assertThrows(IllegalArgumentException.class,
                () -> clienteGestaoService.buscarDetalhesCliente(clienteId, null));
        
        verifyNoInteractions(clienteRepository);
    }
    
    @Test
    @DisplayName("Deve anonimizar cliente corretamente (LGPD)")
    @SuppressWarnings("null")
    void deveAnonimizarClienteCorretamente() {
        // Arrange
        when(clienteRepository.findClienteAtendidoPorBarbearia(clienteId, barbeariaId))
                .thenReturn(Optional.of(clienteMock));
        when(clienteRepository.save(any(JpaCliente.class)))
                .thenReturn(clienteMock);
        
        // Act
        clienteGestaoService.anonimizarCliente(clienteId, barbeariaId);
        
        // Assert
        verify(clienteRepository).findClienteAtendidoPorBarbearia(clienteId, barbeariaId);
        verify(clienteRepository).save(argThat(cliente -> {
            assertTrue(cliente.isAnonimizado(), "Cliente deve estar marcado como anonimizado");
            assertFalse(cliente.isAtivo(), "Cliente deve estar inativo");
            assertNotNull(cliente.getDeletedAt(), "Data de exclusão deve estar preenchida");
            assertTrue(cliente.getNome().startsWith("Cliente Anonimizado"), 
                    "Nome deve ser anonimizado");
            assertTrue(cliente.getEmail().contains("@anonimizado.lgpd"), 
                    "Email deve ser anonimizado");
            assertTrue(cliente.getSenha().startsWith("SENHA_ANONIMIZADA"), 
                    "Senha deve ser anonimizada");
            return true;
        }));
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao tentar anonimizar cliente já anonimizado")
    @SuppressWarnings("null")
    void deveLancarExcecaoAoAnonimizarClienteJaAnonimizado() {
        // Arrange
        clienteMock.setAnonimizado(true);
        when(clienteRepository.findClienteAtendidoPorBarbearia(clienteId, barbeariaId))
                .thenReturn(Optional.of(clienteMock));
        
        // Act & Assert
        assertThrows(AcessoNegadoException.class,
                () -> clienteGestaoService.anonimizarCliente(clienteId, barbeariaId));
        
        verify(clienteRepository).findClienteAtendidoPorBarbearia(clienteId, barbeariaId);
        verify(clienteRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao anonimizar cliente não atendido pela barbearia")
    @SuppressWarnings("null")
    void deveLancarExcecaoAoAnonimizarClienteNaoAtendido() {
        // Arrange
        when(clienteRepository.findClienteAtendidoPorBarbearia(clienteId, barbeariaId))
                .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(ClienteNaoEncontradoException.class,
                () -> clienteGestaoService.anonimizarCliente(clienteId, barbeariaId));
        
        verify(clienteRepository).findClienteAtendidoPorBarbearia(clienteId, barbeariaId);
        verify(clienteRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao anonimizar com parâmetros nulos")
    void deveLancarExcecaoAoAnonimizarComParametrosNulos() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> clienteGestaoService.anonimizarCliente(null, barbeariaId));
        
        assertThrows(IllegalArgumentException.class,
                () -> clienteGestaoService.anonimizarCliente(clienteId, null));
        
        verifyNoInteractions(clienteRepository);
    }
    
    @Test
    @DisplayName("Deve calcular estatísticas de agendamentos corretamente")
    void deveCalcularEstatisticasCorretamente() {
        // Arrange
        JpaAgendamento agendamento2 = new JpaAgendamento();
        agendamento2.setId(2L);
        agendamento2.setClienteId(clienteId);
        agendamento2.setBarbeariaId(barbeariaId);
        agendamento2.setDataHora(LocalDateTime.now().minusDays(1));
        agendamento2.setStatus(StatusAgendamento.CANCELADO);
        
        when(clienteRepository.findClienteAtendidoPorBarbearia(clienteId, barbeariaId))
                .thenReturn(Optional.of(clienteMock));
        when(agendamentoRepository.findByClienteIdOrderByDataHoraDesc(clienteId))
                .thenReturn(Arrays.asList(agendamentoMock, agendamento2));
        
        // Act
        ClienteDetalhesDto resultado = clienteGestaoService.buscarDetalhesCliente(clienteId, barbeariaId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(2L, resultado.totalAgendamentos());
        assertEquals(1L, resultado.agendamentosConcluidos());
        assertEquals(1L, resultado.agendamentosCancelados());
        
        verify(clienteRepository).findClienteAtendidoPorBarbearia(clienteId, barbeariaId);
        verify(agendamentoRepository).findByClienteIdOrderByDataHoraDesc(clienteId);
    }
}
