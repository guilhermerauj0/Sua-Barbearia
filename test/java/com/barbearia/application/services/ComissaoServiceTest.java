package com.barbearia.application.services;

import com.barbearia.application.dto.ComissaoFuncionarioDto;
import com.barbearia.application.dto.RelatorioComissoesDto;
import com.barbearia.domain.enums.StatusAgendamento;
import com.barbearia.domain.enums.TipoPerfil;
import com.barbearia.infrastructure.persistence.entities.*;
import com.barbearia.infrastructure.persistence.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComissaoService - Testes Unitários")
class ComissaoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private ServicoRepository servicoRepository;

    @Mock
    private BarbeariaRepository barbeariaRepository;

    @InjectMocks
    private ComissaoService comissaoService;

    private JpaBarbearia barbearia;
    private JpaFuncionario funcionarioBarbeiro;
    private JpaFuncionario funcionarioManicure;
    private JpaServico servico1;
    private JpaServico servico2;
    private JpaAgendamento agendamento1;
    private JpaAgendamento agendamento2;
    private JpaAgendamento agendamento3;

    @BeforeEach
    void setUp() {
        // Setup Barbearia
        barbearia = new JpaBarbearia();
        barbearia.setId(1L);
        barbearia.setNome("Barbearia Teste");

        // Setup Funcionário Barbeiro
        funcionarioBarbeiro = new JpaFuncionario();
        funcionarioBarbeiro.setId(1L);
        funcionarioBarbeiro.setBarbeariaId(1L);
        funcionarioBarbeiro.setNome("João Barbeiro");
        funcionarioBarbeiro.setEmail("joao@email.com");
        funcionarioBarbeiro.setTelefone("11987654321");
        funcionarioBarbeiro.setPerfilType(TipoPerfil.BARBEIRO);
        funcionarioBarbeiro.setAtivo(true);

        // Setup Funcionário Manicure
        funcionarioManicure = new JpaFuncionario();
        funcionarioManicure.setId(2L);
        funcionarioManicure.setBarbeariaId(1L);
        funcionarioManicure.setNome("Maria Manicure");
        funcionarioManicure.setEmail("maria@email.com");
        funcionarioManicure.setTelefone("11987654322");
        funcionarioManicure.setPerfilType(TipoPerfil.MANICURE);
        funcionarioManicure.setAtivo(true);

        // Setup Serviços
        servico1 = new JpaServicoCorte();
        servico1.setId(1L);
        servico1.setPreco(BigDecimal.valueOf(50.00));

        servico2 = new JpaServicoColoracao();
        servico2.setId(2L);
        servico2.setPreco(BigDecimal.valueOf(100.00));

        // Setup Agendamentos
        agendamento1 = new JpaAgendamento();
        agendamento1.setId(1L);
        agendamento1.setBarbeariaId(1L);
        agendamento1.setBarbeiroId(1L);
        agendamento1.setServicoId(1L);
        agendamento1.setDataHora(LocalDateTime.of(2025, 11, 15, 10, 0));
        agendamento1.setStatus(StatusAgendamento.CONCLUIDO);

        agendamento2 = new JpaAgendamento();
        agendamento2.setId(2L);
        agendamento2.setBarbeariaId(1L);
        agendamento2.setBarbeiroId(1L);
        agendamento2.setServicoId(2L);
        agendamento2.setDataHora(LocalDateTime.of(2025, 11, 16, 14, 0));
        agendamento2.setStatus(StatusAgendamento.CONCLUIDO);

        agendamento3 = new JpaAgendamento();
        agendamento3.setId(3L);
        agendamento3.setBarbeariaId(1L);
        agendamento3.setBarbeiroId(2L);
        agendamento3.setServicoId(1L);
        agendamento3.setDataHora(LocalDateTime.of(2025, 11, 17, 15, 0));
        agendamento3.setStatus(StatusAgendamento.CONCLUIDO);
    }

    @Test
    @DisplayName("Deve gerar relatório de comissões com sucesso")
    void deveGerarRelatorioComSucesso() {
        // Arrange
        LocalDate dataInicio = LocalDate.of(2025, 11, 1);
        LocalDate dataFim = LocalDate.of(2025, 11, 30);

        when(barbeariaRepository.findById(1L)).thenReturn(Optional.of(barbearia));
        when(agendamentoRepository.findByBarbeariaIdAndDataHoraBetweenAndStatus(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(StatusAgendamento.CONCLUIDO)))
                .thenReturn(Arrays.asList(agendamento1, agendamento2, agendamento3));
        
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionarioBarbeiro));
        when(funcionarioRepository.findById(2L)).thenReturn(Optional.of(funcionarioManicure));
        
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico1));
        when(servicoRepository.findById(2L)).thenReturn(Optional.of(servico2));

        // Act
        RelatorioComissoesDto resultado = comissaoService.gerarRelatorioComissoes(1L, dataInicio, dataFim);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.barbeariaId()).isEqualTo(1L);
        assertThat(resultado.barbeariaNome()).isEqualTo("Barbearia Teste");
        assertThat(resultado.dataInicio()).isEqualTo(dataInicio);
        assertThat(resultado.dataFim()).isEqualTo(dataFim);
        assertThat(resultado.totalAgendamentos()).isEqualTo(3);
        assertThat(resultado.comissoesPorFuncionario()).hasSize(2);

        // Verificar comissão do barbeiro (15% de 150.00 = 22.50)
        ComissaoFuncionarioDto comissaoBarbeiro = resultado.comissoesPorFuncionario().stream()
                .filter(c -> c.funcionarioId().equals(1L))
                .findFirst()
                .orElseThrow();
        
        assertThat(comissaoBarbeiro.funcionarioNome()).isEqualTo("João Barbeiro");
        assertThat(comissaoBarbeiro.perfilType()).isEqualTo(TipoPerfil.BARBEIRO);
        assertThat(comissaoBarbeiro.taxaComissao()).isEqualTo(15.0);
        assertThat(comissaoBarbeiro.totalServicos()).isEqualTo(2);
        assertThat(comissaoBarbeiro.valorTotalServicos()).isEqualByComparingTo(BigDecimal.valueOf(150.00));
        assertThat(comissaoBarbeiro.totalComissoes()).isEqualByComparingTo(BigDecimal.valueOf(22.50));

        // Verificar comissão da manicure (12% de 50.00 = 6.00)
        ComissaoFuncionarioDto comissaoManicure = resultado.comissoesPorFuncionario().stream()
                .filter(c -> c.funcionarioId().equals(2L))
                .findFirst()
                .orElseThrow();
        
        assertThat(comissaoManicure.funcionarioNome()).isEqualTo("Maria Manicure");
        assertThat(comissaoManicure.perfilType()).isEqualTo(TipoPerfil.MANICURE);
        assertThat(comissaoManicure.taxaComissao()).isEqualTo(12.0);
        assertThat(comissaoManicure.totalServicos()).isEqualTo(1);
        assertThat(comissaoManicure.valorTotalServicos()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        assertThat(comissaoManicure.totalComissoes()).isEqualByComparingTo(BigDecimal.valueOf(6.00));

        // Verificar totais gerais
        assertThat(resultado.valorTotalServicos()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
        assertThat(resultado.totalComissoes()).isEqualByComparingTo(BigDecimal.valueOf(28.50));
    }

    @Test
    @DisplayName("Deve lançar exceção quando barbearia não encontrada")
    void deveLancarExcecaoQuandoBarbeariaNaoEncontrada() {
        // Arrange
        LocalDate dataInicio = LocalDate.of(2025, 11, 1);
        LocalDate dataFim = LocalDate.of(2025, 11, 30);

        when(barbeariaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> comissaoService.gerarRelatorioComissoes(999L, dataInicio, dataFim))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Barbearia não encontrada");
    }

    @Test
    @DisplayName("Deve lançar exceção quando barbeariaId é null")
    void deveLancarExcecaoQuandoBarbeariaIdNull() {
        // Arrange
        LocalDate dataInicio = LocalDate.of(2025, 11, 1);
        LocalDate dataFim = LocalDate.of(2025, 11, 30);

        // Act & Assert
        assertThatThrownBy(() -> comissaoService.gerarRelatorioComissoes(null, dataInicio, dataFim))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ID da barbearia não pode ser nulo");
    }

    @Test
    @DisplayName("Deve retornar relatório vazio quando não há agendamentos")
    void deveRetornarRelatorioVazioQuandoNaoHaAgendamentos() {
        // Arrange
        LocalDate dataInicio = LocalDate.of(2025, 11, 1);
        LocalDate dataFim = LocalDate.of(2025, 11, 30);

        when(barbeariaRepository.findById(1L)).thenReturn(Optional.of(barbearia));
        when(agendamentoRepository.findByBarbeariaIdAndDataHoraBetweenAndStatus(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(StatusAgendamento.CONCLUIDO)))
                .thenReturn(List.of());

        // Act
        RelatorioComissoesDto resultado = comissaoService.gerarRelatorioComissoes(1L, dataInicio, dataFim);

        // Assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.totalAgendamentos()).isZero();
        assertThat(resultado.comissoesPorFuncionario()).isEmpty();
        assertThat(resultado.valorTotalServicos()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultado.totalComissoes()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve ignorar agendamentos sem funcionário associado")
    void deveIgnorarAgendamentosSemFuncionario() {
        // Arrange
        LocalDate dataInicio = LocalDate.of(2025, 11, 1);
        LocalDate dataFim = LocalDate.of(2025, 11, 30);

        JpaAgendamento agendamentoSemFuncionario = new JpaAgendamento();
        agendamentoSemFuncionario.setId(4L);
        agendamentoSemFuncionario.setBarbeariaId(1L);
        agendamentoSemFuncionario.setBarbeiroId(null); // Sem funcionário
        agendamentoSemFuncionario.setServicoId(1L);
        agendamentoSemFuncionario.setStatus(StatusAgendamento.CONCLUIDO);

        when(barbeariaRepository.findById(1L)).thenReturn(Optional.of(barbearia));
        when(agendamentoRepository.findByBarbeariaIdAndDataHoraBetweenAndStatus(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(StatusAgendamento.CONCLUIDO)))
                .thenReturn(List.of(agendamentoSemFuncionario));

        // Act
        RelatorioComissoesDto resultado = comissaoService.gerarRelatorioComissoes(1L, dataInicio, dataFim);

        // Assert
        assertThat(resultado.comissoesPorFuncionario()).isEmpty();
    }

    @Test
    @DisplayName("Deve ignorar funcionários inativos")
    void deveIgnorarFuncionariosInativos() {
        // Arrange
        LocalDate dataInicio = LocalDate.of(2025, 11, 1);
        LocalDate dataFim = LocalDate.of(2025, 11, 30);

        funcionarioBarbeiro.setAtivo(false);

        when(barbeariaRepository.findById(1L)).thenReturn(Optional.of(barbearia));
        when(agendamentoRepository.findByBarbeariaIdAndDataHoraBetweenAndStatus(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(StatusAgendamento.CONCLUIDO)))
                .thenReturn(List.of(agendamento1));
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionarioBarbeiro));

        // Act
        RelatorioComissoesDto resultado = comissaoService.gerarRelatorioComissoes(1L, dataInicio, dataFim);

        // Assert
        assertThat(resultado.comissoesPorFuncionario()).isEmpty();
    }

    @Test
    @DisplayName("Deve ignorar serviços sem preço")
    void deveIgnorarServicosSemPreco() {
        // Arrange
        LocalDate dataInicio = LocalDate.of(2025, 11, 1);
        LocalDate dataFim = LocalDate.of(2025, 11, 30);

        servico1.setPreco(null);

        when(barbeariaRepository.findById(1L)).thenReturn(Optional.of(barbearia));
        when(agendamentoRepository.findByBarbeariaIdAndDataHoraBetweenAndStatus(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(StatusAgendamento.CONCLUIDO)))
                .thenReturn(List.of(agendamento1));
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionarioBarbeiro));
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico1));

        // Act
        RelatorioComissoesDto resultado = comissaoService.gerarRelatorioComissoes(1L, dataInicio, dataFim);

        // Assert
        ComissaoFuncionarioDto comissao = resultado.comissoesPorFuncionario().get(0);
        assertThat(comissao.valorTotalServicos()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(comissao.totalComissoes()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Deve calcular comissões corretamente para diferentes perfis")
    void deveCalcularComissoesParaDiferentesPerfis() {
        // Arrange
        LocalDate dataInicio = LocalDate.of(2025, 11, 1);
        LocalDate dataFim = LocalDate.of(2025, 11, 30);

        // Criar funcionários com perfis diferentes
        JpaFuncionario esteticista = new JpaFuncionario();
        esteticista.setId(3L);
        esteticista.setBarbeariaId(1L);
        esteticista.setNome("Ana Esteticista");
        esteticista.setEmail("ana@email.com");
        esteticista.setTelefone("11987654323");
        esteticista.setPerfilType(TipoPerfil.ESTETICISTA);
        esteticista.setAtivo(true);

        JpaFuncionario colorista = new JpaFuncionario();
        colorista.setId(4L);
        colorista.setBarbeariaId(1L);
        colorista.setNome("Pedro Colorista");
        colorista.setEmail("pedro@email.com");
        colorista.setTelefone("11987654324");
        colorista.setPerfilType(TipoPerfil.COLORISTA);
        colorista.setAtivo(true);

        // Criar agendamentos para cada perfil
        JpaAgendamento agendamentoEsteticista = new JpaAgendamento();
        agendamentoEsteticista.setId(4L);
        agendamentoEsteticista.setBarbeariaId(1L);
        agendamentoEsteticista.setBarbeiroId(3L);
        agendamentoEsteticista.setServicoId(1L);
        agendamentoEsteticista.setStatus(StatusAgendamento.CONCLUIDO);

        JpaAgendamento agendamentoColorista = new JpaAgendamento();
        agendamentoColorista.setId(5L);
        agendamentoColorista.setBarbeariaId(1L);
        agendamentoColorista.setBarbeiroId(4L);
        agendamentoColorista.setServicoId(2L);
        agendamentoColorista.setStatus(StatusAgendamento.CONCLUIDO);

        when(barbeariaRepository.findById(1L)).thenReturn(Optional.of(barbearia));
        when(agendamentoRepository.findByBarbeariaIdAndDataHoraBetweenAndStatus(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), eq(StatusAgendamento.CONCLUIDO)))
                .thenReturn(Arrays.asList(agendamento1, agendamento3, agendamentoEsteticista, agendamentoColorista));
        
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionarioBarbeiro));
        when(funcionarioRepository.findById(2L)).thenReturn(Optional.of(funcionarioManicure));
        when(funcionarioRepository.findById(3L)).thenReturn(Optional.of(esteticista));
        when(funcionarioRepository.findById(4L)).thenReturn(Optional.of(colorista));
        
        when(servicoRepository.findById(1L)).thenReturn(Optional.of(servico1));
        when(servicoRepository.findById(2L)).thenReturn(Optional.of(servico2));

        // Act
        RelatorioComissoesDto resultado = comissaoService.gerarRelatorioComissoes(1L, dataInicio, dataFim);

        // Assert
        assertThat(resultado.comissoesPorFuncionario()).hasSize(4);

        // BARBEIRO: 15% de 50.00 = 7.50
        ComissaoFuncionarioDto barbeiro = resultado.comissoesPorFuncionario().stream()
                .filter(c -> c.perfilType() == TipoPerfil.BARBEIRO)
                .findFirst().orElseThrow();
        assertThat(barbeiro.taxaComissao()).isEqualTo(15.0);
        assertThat(barbeiro.totalComissoes()).isEqualByComparingTo(BigDecimal.valueOf(7.50));

        // MANICURE: 12% de 50.00 = 6.00
        ComissaoFuncionarioDto manicure = resultado.comissoesPorFuncionario().stream()
                .filter(c -> c.perfilType() == TipoPerfil.MANICURE)
                .findFirst().orElseThrow();
        assertThat(manicure.taxaComissao()).isEqualTo(12.0);
        assertThat(manicure.totalComissoes()).isEqualByComparingTo(BigDecimal.valueOf(6.00));

        // ESTETICISTA: 13% de 50.00 = 6.50
        ComissaoFuncionarioDto estetic = resultado.comissoesPorFuncionario().stream()
                .filter(c -> c.perfilType() == TipoPerfil.ESTETICISTA)
                .findFirst().orElseThrow();
        assertThat(estetic.taxaComissao()).isEqualTo(13.0);
        assertThat(estetic.totalComissoes()).isEqualByComparingTo(BigDecimal.valueOf(6.50));

        // COLORISTA: 18% de 100.00 = 18.00
        ComissaoFuncionarioDto color = resultado.comissoesPorFuncionario().stream()
                .filter(c -> c.perfilType() == TipoPerfil.COLORISTA)
                .findFirst().orElseThrow();
        assertThat(color.taxaComissao()).isEqualTo(18.0);
        assertThat(color.totalComissoes()).isEqualByComparingTo(BigDecimal.valueOf(18.00));
    }
}
