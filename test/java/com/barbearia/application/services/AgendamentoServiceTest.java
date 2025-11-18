package com.barbearia.application.services;

import com.barbearia.application.dto.AgendamentoBriefDto;
import com.barbearia.application.dto.AgendamentoResponseDto;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import com.barbearia.domain.exceptions.AgendamentoNaoEncontradoException;
import com.barbearia.infrastructure.persistence.entities.JpaAgendamento;
import com.barbearia.infrastructure.persistence.repositories.AgendamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para AgendamentoService.
 * Valida a lógica de negócio relacionada aos agendamentos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AgendamentoService - Testes Unitários")
@SuppressWarnings("null")
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;
    
    @Mock
    private com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository funcionarioRepository;
    
    @Mock
    private com.barbearia.infrastructure.persistence.repositories.ServicoRepository servicoRepository;
    
    @Mock
    private com.barbearia.infrastructure.persistence.repositories.ProfissionalServicoRepository profissionalServicoRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Long clienteId;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        clienteId = 1L;
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("Deve listar histórico de agendamentos passados do cliente")
    void deveListarHistoricoDeAgendamentosPassados() {
        // Arrange
        JpaAgendamento agendamento1 = criarJpaAgendamento(1L, now.minusDays(2), StatusAgendamento.CONCLUIDO);
        JpaAgendamento agendamento2 = criarJpaAgendamento(2L, now.minusDays(7), StatusAgendamento.CONCLUIDO);
        JpaAgendamento agendamento3 = criarJpaAgendamento(3L, now.minusDays(30), StatusAgendamento.CANCELADO);

        List<JpaAgendamento> agendamentosPassados = Arrays.asList(agendamento1, agendamento2, agendamento3);

        when(agendamentoRepository.findHistoricoByClienteId(eq(clienteId), any(LocalDateTime.class)))
                .thenReturn(agendamentosPassados);

        // Act
        List<AgendamentoBriefDto> resultado = agendamentoService.listarHistoricoCliente(clienteId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(3);

        // Verifica ordenação DESC (mais recente primeiro)
        assertThat(resultado.get(0).id()).isEqualTo(1L); // -2 dias
        assertThat(resultado.get(1).id()).isEqualTo(2L); // -7 dias
        assertThat(resultado.get(2).id()).isEqualTo(3L); // -30 dias

        // Verifica que o status é preservado
        assertThat(resultado.get(0).status()).isEqualTo(StatusAgendamento.CONCLUIDO);
        assertThat(resultado.get(2).status()).isEqualTo(StatusAgendamento.CANCELADO);

        // Verifica que o repository foi chamado com parâmetros corretos
        verify(agendamentoRepository, times(1))
                .findHistoricoByClienteId(eq(clienteId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando cliente não tem histórico")
    void deveRetornarListaVaziaQuandoClienteNaoTemHistorico() {
        // Arrange
        when(agendamentoRepository.findHistoricoByClienteId(eq(clienteId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        List<AgendamentoBriefDto> resultado = agendamentoService.listarHistoricoCliente(clienteId);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();

        verify(agendamentoRepository, times(1))
                .findHistoricoByClienteId(eq(clienteId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve filtrar apenas agendamentos passados, não futuros")
    void deveFiltrarApenasAgendamentosPassados() {
        // Arrange
        JpaAgendamento passado1 = criarJpaAgendamento(1L, now.minusDays(5), StatusAgendamento.CONCLUIDO);
        JpaAgendamento passado2 = criarJpaAgendamento(2L, now.minusDays(10), StatusAgendamento.CONCLUIDO);
        // Agendamentos futuros não devem ser retornados pelo repository

        List<JpaAgendamento> apenasPassados = Arrays.asList(passado1, passado2);

        when(agendamentoRepository.findHistoricoByClienteId(eq(clienteId), any(LocalDateTime.class)))
                .thenReturn(apenasPassados);

        // Act
        List<AgendamentoBriefDto> resultado = agendamentoService.listarHistoricoCliente(clienteId);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).dataHora()).isBefore(LocalDateTime.now());
        assertThat(resultado.get(1).dataHora()).isBefore(LocalDateTime.now());

        verify(agendamentoRepository, times(1))
                .findHistoricoByClienteId(eq(clienteId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve isolar agendamentos por cliente (não retornar de outros clientes)")
    void deveIsolarAgendamentosPorCliente() {
        // Arrange
        Long outroClienteId = 99L;
        JpaAgendamento agendamentoCliente1 = criarJpaAgendamento(1L, now.minusDays(3), StatusAgendamento.CONCLUIDO);

        when(agendamentoRepository.findHistoricoByClienteId(eq(clienteId), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(agendamentoCliente1));

        when(agendamentoRepository.findHistoricoByClienteId(eq(outroClienteId), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        List<AgendamentoBriefDto> resultadoCliente1 = agendamentoService.listarHistoricoCliente(clienteId);
        List<AgendamentoBriefDto> resultadoOutroCliente = agendamentoService.listarHistoricoCliente(outroClienteId);

        // Assert
        assertThat(resultadoCliente1).hasSize(1);
        assertThat(resultadoOutroCliente).isEmpty();

        // Cada cliente teve sua própria consulta
        verify(agendamentoRepository, times(1))
                .findHistoricoByClienteId(eq(clienteId), any(LocalDateTime.class));
        verify(agendamentoRepository, times(1))
                .findHistoricoByClienteId(eq(outroClienteId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando clienteId for nulo")
    void deveLancarExcecaoQuandoClienteIdForNulo() {
        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.listarHistoricoCliente(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID do cliente não pode ser nulo");

        // Verifica que o repository nunca foi chamado
        verify(agendamentoRepository, never())
                .findHistoricoByClienteId(any(), any());
    }

    @Test
    @DisplayName("Deve retornar DTOs com todos os campos preenchidos")
    void deveRetornarDtosComTodosCamposPreenchidos() {
        // Arrange
        JpaAgendamento agendamento = criarJpaAgendamento(10L, now.minusDays(1), StatusAgendamento.CONCLUIDO);
        agendamento.setObservacoes("Cliente chegou 10 minutos antes");

        when(agendamentoRepository.findHistoricoByClienteId(eq(clienteId), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(agendamento));

        // Act
        List<AgendamentoBriefDto> resultado = agendamentoService.listarHistoricoCliente(clienteId);

        // Assert
        assertThat(resultado).hasSize(1);
        AgendamentoBriefDto dto = resultado.get(0);

        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.dataHora()).isNotNull();
        assertThat(dto.status()).isEqualTo(StatusAgendamento.CONCLUIDO);
        assertThat(dto.nomeBarbearia()).isNotNull();
        assertThat(dto.nomeServico()).isNotNull();
        assertThat(dto.observacoes()).isEqualTo("Cliente chegou 10 minutos antes");
    }

    @Test
    @DisplayName("Deve listar agendamentos futuros do cliente")
    void deveListarAgendamentosFuturos() {
        // Arrange
        JpaAgendamento futuro1 = criarJpaAgendamento(20L, now.plusDays(3), StatusAgendamento.CONFIRMADO);
        JpaAgendamento futuro2 = criarJpaAgendamento(21L, now.plusDays(7), StatusAgendamento.PENDENTE);

        when(agendamentoRepository.findAgendamentosFuturosByClienteId(eq(clienteId), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(futuro1, futuro2));

        // Act
        List<AgendamentoBriefDto> resultado = agendamentoService.listarAgendamentosFuturos(clienteId);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).status()).isEqualTo(StatusAgendamento.CONFIRMADO);
        assertThat(resultado.get(1).status()).isEqualTo(StatusAgendamento.PENDENTE);

        verify(agendamentoRepository, times(1))
                .findAgendamentosFuturosByClienteId(eq(clienteId), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Deve listar todos os agendamentos do cliente (passados e futuros)")
    void deveListarTodosAgendamentosDoCliente() {
        // Arrange
        JpaAgendamento passado = criarJpaAgendamento(30L, now.minusDays(5), StatusAgendamento.CONCLUIDO);
        JpaAgendamento futuro = criarJpaAgendamento(31L, now.plusDays(5), StatusAgendamento.CONFIRMADO);

        when(agendamentoRepository.findByClienteIdOrderByDataHoraDesc(clienteId))
                .thenReturn(Arrays.asList(futuro, passado)); // Ordenado DESC

        // Act
        List<AgendamentoBriefDto> resultado = agendamentoService.listarTodosAgendamentosCliente(clienteId);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).id()).isEqualTo(31L); // Futuro primeiro (DESC)
        assertThat(resultado.get(1).id()).isEqualTo(30L); // Passado depois

        verify(agendamentoRepository, times(1))
                .findByClienteIdOrderByDataHoraDesc(clienteId);
    }

    // ==================== TESTES PARA BUSCAR AGENDAMENTO POR ID ====================

    @Test
    @DisplayName("Deve buscar agendamento por ID com sucesso quando cliente tem permissão")
    void deveBuscarAgendamentoPorIdComSucesso() {
        // Arrange
        Long agendamentoId = 1L;
        JpaAgendamento agendamento = criarJpaAgendamento(agendamentoId, now.plusDays(5), StatusAgendamento.CONFIRMADO);
        
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.of(agendamento));

        // Act
        AgendamentoResponseDto resultado = agendamentoService.buscarAgendamentoPorId(agendamentoId, clienteId, "CLIENTE");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(agendamentoId);
        assertThat(resultado.clienteId()).isEqualTo(clienteId);
        assertThat(resultado.status()).isEqualTo(StatusAgendamento.CONFIRMADO);
        
        verify(agendamentoRepository, times(1)).findById(agendamentoId);
    }

    @Test
    @DisplayName("Deve retornar 404 quando agendamento não existe")
    void deveRetornar404QuandoAgendamentoNaoExiste() {
        // Arrange
        Long agendamentoId = 999L;
        
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.buscarAgendamentoPorId(agendamentoId, clienteId, "CLIENTE"))
                .isInstanceOf(AgendamentoNaoEncontradoException.class)
                .hasMessageContaining("não existe");

        verify(agendamentoRepository, times(1)).findById(agendamentoId);
    }

    @Test
    @DisplayName("Deve retornar 403 quando cliente tenta acessar agendamento de outro cliente")
    void deveRetornar403QuandoClienteNaoTemPermissao() {
        // Arrange
        Long agendamentoId = 1L;
        Long outroClienteId = 999L;
        JpaAgendamento agendamento = criarJpaAgendamento(agendamentoId, now.plusDays(5), StatusAgendamento.CONFIRMADO);
        // Agendamento pertence a clienteId, mas estamos tentando acessar com outro ID
        
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.of(agendamento));

        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.buscarAgendamentoPorId(agendamentoId, outroClienteId, "CLIENTE"))
                .isInstanceOf(AcessoNegadoException.class)
                .hasMessageContaining("permissão");

        verify(agendamentoRepository, times(1)).findById(agendamentoId);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando agendamentoId é nulo")
    void deveLancarExcecaoQuandoAgendamentoIdEhNulo() {
        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.buscarAgendamentoPorId(null, clienteId, "CLIENTE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("não pode ser nulo");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando usuarioId é nulo")
    void deveLancarExcecaoQuandoUsuarioIdEhNulo() {
        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.buscarAgendamentoPorId(1L, null, "CLIENTE"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("usuário");
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException quando tipoUsuario é nulo")
    void deveLancarExcecaoQuandoTipoUsuarioEhNulo() {
        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.buscarAgendamentoPorId(1L, clienteId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo do usuário");
    }

    @Test
    @DisplayName("Deve autorizar barbearia a acessar agendamentos da sua barbearia")
    void deveAutorizarBarbeariaacessarAgendamentos() {
        // Arrange
        Long agendamentoId = 1L;
        Long barbeariaId = 10L;
        JpaAgendamento agendamento = criarJpaAgendamento(agendamentoId, now.plusDays(5), StatusAgendamento.CONFIRMADO);
        agendamento.setBarbeariaId(barbeariaId); // Agendamento pertence à barbearia
        
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.of(agendamento));

        // Act
        AgendamentoResponseDto resultado = agendamentoService.buscarAgendamentoPorId(agendamentoId, barbeariaId, "BARBEARIA");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.barbeariaId()).isEqualTo(barbeariaId);
        
        verify(agendamentoRepository, times(1)).findById(agendamentoId);
    }

    @Test
    @DisplayName("Deve negar acesso quando barbearia tenta acessar agendamento de outra barbearia")
    void deveNegarAcessoBarbeariaAoutrabarbearia() {
        // Arrange
        Long agendamentoId = 1L;
        Long barbeariaId = 10L;
        Long outraBarbeariaId = 20L;
        JpaAgendamento agendamento = criarJpaAgendamento(agendamentoId, now.plusDays(5), StatusAgendamento.CONFIRMADO);
        agendamento.setBarbeariaId(barbeariaId);
        
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.of(agendamento));

        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.buscarAgendamentoPorId(agendamentoId, outraBarbeariaId, "BARBEARIA"))
                .isInstanceOf(AcessoNegadoException.class);

        verify(agendamentoRepository, times(1)).findById(agendamentoId);
    }

    @Test
    @DisplayName("Deve autorizar barbeiro a acessar agendamentos onde ele é o prestador")
    void deveAutorizarBarbeiroAcessarAgendamentos() {
        // Arrange
        Long agendamentoId = 1L;
        Long barbeiroId = 20L;
        JpaAgendamento agendamento = criarJpaAgendamento(agendamentoId, now.plusDays(5), StatusAgendamento.CONFIRMADO);
        agendamento.setBarbeiroId(barbeiroId);
        
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.of(agendamento));

        // Act
        AgendamentoResponseDto resultado = agendamentoService.buscarAgendamentoPorId(agendamentoId, barbeiroId, "BARBEIRO");

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.funcionarioId()).isEqualTo(barbeiroId);
        
        verify(agendamentoRepository, times(1)).findById(agendamentoId);
    }

    @Test
    @DisplayName("Deve negar acesso quando barbeiro não é o prestador do serviço")
    void deveNegarAcessoBarbeiroNaoEhoPrestador() {
        // Arrange
        Long agendamentoId = 1L;
        Long barbeiroId = 20L;
        Long outroBarbeiroId = 30L;
        JpaAgendamento agendamento = criarJpaAgendamento(agendamentoId, now.plusDays(5), StatusAgendamento.CONFIRMADO);
        agendamento.setBarbeiroId(barbeiroId);
        
        when(agendamentoRepository.findById(agendamentoId))
                .thenReturn(Optional.of(agendamento));

        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.buscarAgendamentoPorId(agendamentoId, outroBarbeiroId, "BARBEIRO"))
                .isInstanceOf(AcessoNegadoException.class);

        verify(agendamentoRepository, times(1)).findById(agendamentoId);
    }

    // ========== TESTES PARA CRIAR AGENDAMENTO ==========

    @Test
    @DisplayName("Deve criar agendamento com sucesso")
    void deveCriarAgendamentoComSucesso() {
        // Arrange
        LocalDateTime dataFutura = now.plusDays(7);
        com.barbearia.application.dto.AgendamentoRequestDto request =
                new com.barbearia.application.dto.AgendamentoRequestDto(
                        1L, 1L, dataFutura, "Corte normal"
                );

        JpaAgendamento agendamentoSalvo = new JpaAgendamento();
        agendamentoSalvo.setId(123L);
        agendamentoSalvo.setClienteId(clienteId);
        agendamentoSalvo.setBarbeariaId(1L);
        agendamentoSalvo.setBarbeiroId(1L);
        agendamentoSalvo.setServicoId(1L);
        agendamentoSalvo.setDataHora(dataFutura);
        agendamentoSalvo.setStatus(StatusAgendamento.PENDENTE);
        agendamentoSalvo.setObservacoes("Corte normal");
        agendamentoSalvo.setDataCriacao(now);
        agendamentoSalvo.setDataAtualizacao(now);

        // Mocks necessários
        com.barbearia.infrastructure.persistence.entities.JpaServico servico = 
                mock(com.barbearia.infrastructure.persistence.entities.JpaServico.class);
        com.barbearia.infrastructure.persistence.entities.JpaFuncionario barbeiro = 
                new com.barbearia.infrastructure.persistence.entities.JpaFuncionario();
        barbeiro.setId(1L);
        barbeiro.setBarbeariaId(1L);
        barbeiro.setPerfilType(com.barbearia.domain.enums.TipoPerfil.BARBEIRO);
        
        when(servicoRepository.findById(1L))
                .thenReturn(java.util.Optional.of(servico));
        when(funcionarioRepository.findById(1L))
                .thenReturn(java.util.Optional.of(barbeiro));
        when(profissionalServicoRepository.canPrestarServico(1L, 1L))
                .thenReturn(true);
        when(agendamentoRepository.existsConflictByBarbeiroIdAndDataHora(1L, dataFutura))
                .thenReturn(false);
        when(agendamentoRepository.save(any())).thenReturn(agendamentoSalvo);

        // Act
        com.barbearia.application.dto.AgendamentoResponseDto resultado =
                agendamentoService.criarAgendamento(clienteId, request);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.id()).isEqualTo(123L);
        assertThat(resultado.clienteId()).isEqualTo(clienteId);
        assertThat(resultado.status()).isEqualTo(StatusAgendamento.PENDENTE);
        assertThat(resultado.observacoes()).isEqualTo("Corte normal");

        verify(agendamentoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando clienteId é nulo")
    void deveLancarExcecaoQuandoClienteIdEhNulo() {
        // Arrange
        LocalDateTime dataFutura = now.plusDays(7);
        com.barbearia.application.dto.AgendamentoRequestDto request =
                new com.barbearia.application.dto.AgendamentoRequestDto(1L, 1L, dataFutura);

        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.criarAgendamento(null, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID do cliente não pode ser nulo");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando requestDto é nulo")
    void deveLancarExcecaoQuandoRequestDtoEhNulo() {
        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.criarAgendamento(clienteId, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Dados do agendamento não podem ser nulos");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando data/hora é no passado")
    void deveLancarExcecaoQuandoDataEhNoPasado() {
        // Arrange
        LocalDateTime dataPasada = now.minusDays(1);
        com.barbearia.application.dto.AgendamentoRequestDto request =
                new com.barbearia.application.dto.AgendamentoRequestDto(1L, 1L, dataPasada);

        // Act & Assert
        assertThatThrownBy(() -> agendamentoService.criarAgendamento(clienteId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Data/hora do agendamento não pode ser no passado");

        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve retornar agendamento com status PENDENTE")
    void deveRetornarAgendamentoComStatusPendente() {
        // Arrange
        LocalDateTime dataFutura = now.plusDays(7);
        com.barbearia.application.dto.AgendamentoRequestDto request =
                new com.barbearia.application.dto.AgendamentoRequestDto(1L, 1L, dataFutura);

        JpaAgendamento agendamentoSalvo = new JpaAgendamento();
        agendamentoSalvo.setId(125L);
        agendamentoSalvo.setClienteId(clienteId);
        agendamentoSalvo.setBarbeariaId(1L);
        agendamentoSalvo.setBarbeiroId(1L);
        agendamentoSalvo.setServicoId(1L);
        agendamentoSalvo.setDataHora(dataFutura);
        agendamentoSalvo.setStatus(StatusAgendamento.PENDENTE);
        agendamentoSalvo.setObservacoes("");
        agendamentoSalvo.setDataCriacao(now);
        agendamentoSalvo.setDataAtualizacao(now);

        // Mocks
        com.barbearia.infrastructure.persistence.entities.JpaServico servico = 
                mock(com.barbearia.infrastructure.persistence.entities.JpaServico.class);
        com.barbearia.infrastructure.persistence.entities.JpaFuncionario barbeiro = 
                new com.barbearia.infrastructure.persistence.entities.JpaFuncionario();
        barbeiro.setId(1L);
        barbeiro.setBarbeariaId(1L);
        barbeiro.setPerfilType(com.barbearia.domain.enums.TipoPerfil.BARBEIRO);
        
        when(servicoRepository.findById(1L)).thenReturn(java.util.Optional.of(servico));
        when(funcionarioRepository.findById(1L)).thenReturn(java.util.Optional.of(barbeiro));
        when(profissionalServicoRepository.canPrestarServico(1L, 1L)).thenReturn(true);
        when(agendamentoRepository.save(any())).thenReturn(agendamentoSalvo);
        when(agendamentoRepository.existsConflictByBarbeiroIdAndDataHora(1L, dataFutura))
                .thenReturn(false);

        // Act
        com.barbearia.application.dto.AgendamentoResponseDto resultado =
                agendamentoService.criarAgendamento(clienteId, request);

        // Assert
        assertThat(resultado.status()).isEqualTo(StatusAgendamento.PENDENTE);

        verify(agendamentoRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve incluir observações no agendamento quando fornecidas")
    void deveIncluirObservacoesQuandoFornecidas() {
        // Arrange
        LocalDateTime dataFutura = now.plusDays(7);
        String observacoes = "Preferência: máquina 2, sem tesoura";
        com.barbearia.application.dto.AgendamentoRequestDto request =
                new com.barbearia.application.dto.AgendamentoRequestDto(1L, 1L, dataFutura, observacoes);

        JpaAgendamento agendamentoSalvo = new JpaAgendamento();
        agendamentoSalvo.setId(126L);
        agendamentoSalvo.setClienteId(clienteId);
        agendamentoSalvo.setBarbeariaId(1L);
        agendamentoSalvo.setBarbeiroId(1L);
        agendamentoSalvo.setServicoId(1L);
        agendamentoSalvo.setDataHora(dataFutura);
        agendamentoSalvo.setStatus(StatusAgendamento.PENDENTE);
        agendamentoSalvo.setObservacoes(observacoes);
        agendamentoSalvo.setDataCriacao(now);
        agendamentoSalvo.setDataAtualizacao(now);

        // Mocks
        com.barbearia.infrastructure.persistence.entities.JpaServico servico = 
                mock(com.barbearia.infrastructure.persistence.entities.JpaServico.class);
        com.barbearia.infrastructure.persistence.entities.JpaFuncionario barbeiro = 
                new com.barbearia.infrastructure.persistence.entities.JpaFuncionario();
        barbeiro.setId(1L);
        barbeiro.setBarbeariaId(1L);
        barbeiro.setPerfilType(com.barbearia.domain.enums.TipoPerfil.BARBEIRO);
        
        when(servicoRepository.findById(1L)).thenReturn(java.util.Optional.of(servico));
        when(funcionarioRepository.findById(1L)).thenReturn(java.util.Optional.of(barbeiro));
        when(profissionalServicoRepository.canPrestarServico(1L, 1L)).thenReturn(true);
        when(agendamentoRepository.save(any())).thenReturn(agendamentoSalvo);
        when(agendamentoRepository.existsConflictByBarbeiroIdAndDataHora(1L, dataFutura))
                .thenReturn(false);

        // Act
        com.barbearia.application.dto.AgendamentoResponseDto resultado =
                agendamentoService.criarAgendamento(clienteId, request);

        // Assert
        assertThat(resultado.observacoes()).isEqualTo(observacoes);

        verify(agendamentoRepository, times(1)).save(any());
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Cria um JpaAgendamento de teste com dados básicos.
     */
    private JpaAgendamento criarJpaAgendamento(Long id, LocalDateTime dataHora, StatusAgendamento status) {
        JpaAgendamento agendamento = new JpaAgendamento();
        agendamento.setId(id);
        agendamento.setClienteId(clienteId);
        agendamento.setBarbeariaId(1L);
        agendamento.setBarbeiroId(1L);
        agendamento.setServicoId(1L);
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(status);
        agendamento.setDataCriacao(LocalDateTime.now());
        agendamento.setDataAtualizacao(LocalDateTime.now());
        return agendamento;
    }
}
