package com.barbearia.application.services;

import com.barbearia.application.dto.HorarioDisponivelDto;
import com.barbearia.infrastructure.persistence.entities.*;
import com.barbearia.infrastructure.persistence.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes para HorarioService - lógica de cálculo de horários disponíveis.
 * 
 * Cenários testados:
 * - Validação de parâmetros de entrada (null, data no passado)
 * - Serviço não encontrado
 * - Barbearia fechada no dia
 * - Sem profissionais qualificados
 * - Retorno com sucesso de horários disponíveis
 * 
 * @author Sua Barbearia Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para HorarioService - Cálculo de horários disponíveis")
public class HorarioServiceTest {

        @Mock
        private FuncionarioRepository funcionarioRepository;

        @Mock
        private ProfissionalServicoRepository profissionalServicoRepository;

        @Mock
        private HorarioFuncionamentoRepository horarioFuncionamentoRepository;

        @Mock
        private AgendamentoRepository agendamentoRepository;

        @Mock
        private ServicoRepository servicoRepository;

        private HorarioService horarioService;

        @Mock
        private HorarioBloqueioService horarioBloqueioService;

        @Mock
        private HorarioExcecaoRepository horarioExcecaoRepository;

        private static final Long BARBEARIA_ID = 1L;
        private static final Long SERVICO_ID = 1L;
        private static final Long FUNCIONARIO_ID = 1L;
        private LocalDate dataManhã;

        @BeforeEach
        void setUp() {
                horarioService = new HorarioService(
                                funcionarioRepository,
                                profissionalServicoRepository,
                                horarioFuncionamentoRepository,
                                agendamentoRepository,
                                servicoRepository,
                                horarioBloqueioService,
                                horarioExcecaoRepository);
                dataManhã = LocalDate.now().plusDays(1);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando barbeariaId é null")
        void testObterHorariosDisponiveisComBarbeariaIdNull() {
                // Act
                List<HorarioDisponivelDto> resultado = horarioService.obterHorariosDisponiveis(
                                null, SERVICO_ID, dataManhã);

                // Assert
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando servicoId é null")
        void testObterHorariosDisponiveisComServicoIdNull() {
                // Act
                List<HorarioDisponivelDto> resultado = horarioService.obterHorariosDisponiveis(
                                BARBEARIA_ID, null, dataManhã);

                // Assert
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando data é null")
        void testObterHorariosDisponiveisComDataNull() {
                // Act
                List<HorarioDisponivelDto> resultado = horarioService.obterHorariosDisponiveis(
                                BARBEARIA_ID, SERVICO_ID, null);

                // Assert
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando data é no passado")
        void testObterHorariosDisponiveisComDataNoPast() {
                // Arrange
                LocalDate dataPassada = LocalDate.now().minusDays(1);

                // Act
                List<HorarioDisponivelDto> resultado = horarioService.obterHorariosDisponiveis(
                                BARBEARIA_ID, SERVICO_ID, dataPassada);

                // Assert
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando serviço não encontrado")
        void testObterHorariosDisponiveisComServicoNaoEncontrado() {
                // Arrange
                when(servicoRepository.findById(anyLong())).thenReturn(Optional.empty());

                // Act
                List<HorarioDisponivelDto> resultado = horarioService.obterHorariosDisponiveis(
                                BARBEARIA_ID, SERVICO_ID, dataManhã);

                // Assert
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
                verify(servicoRepository, times(1)).findById(anyLong());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando barbearia está fechada no dia")
        void testObterHorariosDisponiveisComBarbeariaFechada() {
                // Arrange
                JpaServicoCorte servico = criarServicoMock();
                int diaSemana = dataManhã.getDayOfWeek().getValue();

                when(servicoRepository.findById(anyLong())).thenReturn(Optional.of(servico));
                when(profissionalServicoRepository.findFuncionariosByServicoIdAtivo(SERVICO_ID))
                                .thenReturn(Collections.emptyList()); // Simula sem profissionais ou sem horários

                // Note: The original test logic was checking for empty list when barbearia is
                // closed.
                // Now we test "barbearia fechada" via repository (not exception).
                // we need a professional, but no schedule found.

                JpaFuncionario funcionario = criarFuncionarioMock();
                JpaProfissionalServico ps = criarProfissionalServicoMock();

                when(profissionalServicoRepository.findFuncionariosByServicoIdAtivo(SERVICO_ID))
                                .thenReturn(List.of(ps));
                when(funcionarioRepository.findByIdAtivo(FUNCIONARIO_ID))
                                .thenReturn(Optional.of(funcionario));
                when(horarioFuncionamentoRepository.findByFuncionarioIdAndDiaSemanaAtivo(FUNCIONARIO_ID, diaSemana))
                                .thenReturn(Optional.empty());
                when(horarioFuncionamentoRepository.findByBarbeariaIdAndDiaSemanaAtivo(BARBEARIA_ID, diaSemana))
                                .thenReturn(Optional.empty());

                // Act
                List<HorarioDisponivelDto> resultado = horarioService.obterHorariosDisponiveis(
                                BARBEARIA_ID, SERVICO_ID, dataManhã);

                // Assert
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há profissionais qualificados")
        void testObterHorariosDisponiveisComProfissionaisNaoQualificados() {
                // Arrange
                JpaServicoCorte servico = criarServicoMock();

                when(servicoRepository.findById(anyLong())).thenReturn(Optional.of(servico));
                when(profissionalServicoRepository.findFuncionariosByServicoIdAtivo(SERVICO_ID))
                                .thenReturn(Collections.emptyList());

                // Act
                List<HorarioDisponivelDto> resultado = horarioService.obterHorariosDisponiveis(
                                BARBEARIA_ID, SERVICO_ID, dataManhã);

                // Assert
                assertNotNull(resultado);
                assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("Deve retornar horários disponíveis quando tudo está configurado corretamente")
        void testObterHorariosDisponiveisComSucesso() {
                // Arrange
                JpaServicoCorte servico = criarServicoMock();
                JpaHorarioFuncionamento horario = criarHorarioFuncionamentoMock();
                JpaFuncionario funcionario = criarFuncionarioMock();
                JpaProfissionalServico profissionalServico = criarProfissionalServicoMock();
                int diaSemana = dataManhã.getDayOfWeek().getValue();

                when(servicoRepository.findById(anyLong())).thenReturn(Optional.of(servico));
                when(horarioFuncionamentoRepository.findByBarbeariaIdAndDiaSemanaAtivo(BARBEARIA_ID, diaSemana))
                                .thenReturn(Optional.of(horario));
                when(profissionalServicoRepository.findFuncionariosByServicoIdAtivo(SERVICO_ID))
                                .thenReturn(List.of(profissionalServico));
                when(funcionarioRepository.findByIdAtivo(FUNCIONARIO_ID))
                                .thenReturn(Optional.of(funcionario));
                when(agendamentoRepository.findByBarbeariaIdAndPeriodo(
                                eq(BARBEARIA_ID), any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(Collections.emptyList());

                // Act
                List<HorarioDisponivelDto> resultado = horarioService.obterHorariosDisponiveis(
                                BARBEARIA_ID, SERVICO_ID, dataManhã);

                // Assert
                assertNotNull(resultado);
                assertFalse(resultado.isEmpty());
                assertTrue(resultado.stream().allMatch(h -> "João".equals(h.getFuncionarioNome())));
                assertTrue(resultado.stream().allMatch(h -> "BARBEIRO".equals(h.getProfissao())));
        }

        // ==================== Métodos auxiliares para criar mocks ====================

        private JpaServicoCorte criarServicoMock() {
                JpaServicoCorte servico = new JpaServicoCorte();
                servico.setId(SERVICO_ID);
                servico.setNome("Corte de Cabelo");
                servico.setDuracao(30);
                servico.setPreco(new BigDecimal("50.00"));
                servico.setAtivo(true);
                return servico;
        }

        private JpaHorarioFuncionamento criarHorarioFuncionamentoMock() {
                return new JpaHorarioFuncionamento(
                                BARBEARIA_ID,
                                dataManhã.getDayOfWeek().getValue(),
                                LocalTime.of(9, 0),
                                LocalTime.of(17, 0));
        }

        private JpaFuncionario criarFuncionarioMock() {
                JpaFuncionario barbeiro = new JpaFuncionario();
                barbeiro.setId(FUNCIONARIO_ID);
                barbeiro.setNome("João");
                barbeiro.setEmail("joao@teste.com");
                barbeiro.setTelefone("123456789");
                barbeiro.setBarbeariaId(BARBEARIA_ID);
                barbeiro.setPerfilType(com.barbearia.domain.enums.TipoPerfil.BARBEIRO);
                barbeiro.setAtivo(true);
                return barbeiro;
        }

        private JpaProfissionalServico criarProfissionalServicoMock() {
                JpaProfissionalServico ps = new JpaProfissionalServico(FUNCIONARIO_ID, SERVICO_ID);
                ps.setId(1L);
                return ps;
        }
}
