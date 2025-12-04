package com.barbearia.application.services;

import com.barbearia.application.dto.HorarioExcecaoRequestDto;
import com.barbearia.application.dto.HorarioExcecaoResponseDto;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.entities.JpaHorarioExcecao;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import com.barbearia.infrastructure.persistence.repositories.HorarioExcecaoRepository;
import com.barbearia.infrastructure.persistence.repositories.HorarioFuncionamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("HorarioGestaoService - Testes de Gestão de Exceções")
class HorarioGestaoServiceTest {

    @Mock
    private HorarioFuncionamentoRepository horarioFuncionamentoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private HorarioExcecaoRepository horarioExcecaoRepository;

    @InjectMocks
    private HorarioGestaoService horarioGestaoService;

    private JpaFuncionario funcionario;
    private HorarioExcecaoRequestDto requestDto;
    private JpaHorarioExcecao excecao;

    @BeforeEach
    void setUp() {
        funcionario = new JpaFuncionario();
        funcionario.setId(1L);
        funcionario.setNome("João Barbeiro");
        funcionario.setBarbeariaId(1L);

        requestDto = new HorarioExcecaoRequestDto();
        requestDto.setData(LocalDate.now().plusDays(1));
        requestDto.setHoraAbertura(LocalTime.of(9, 0));
        requestDto.setHoraFechamento(LocalTime.of(18, 0));
        requestDto.setMotivo("Trabalho extra");

        excecao = new JpaHorarioExcecao(
                1L,
                requestDto.getData(),
                requestDto.getHoraAbertura(),
                requestDto.getHoraFechamento(),
                requestDto.getMotivo(),
                "BARBEARIA");
        excecao.setId(10L);
    }

    @Test
    @DisplayName("Deve criar exceção com sucesso")
    void deveCriarExcecaoComSucesso() {
        // Arrange
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(horarioExcecaoRepository.existsByFuncionarioIdAndData(anyLong(), any())).thenReturn(false);
        when(horarioExcecaoRepository.save(any(JpaHorarioExcecao.class))).thenReturn(excecao);

        // Act
        HorarioExcecaoResponseDto response = horarioGestaoService.criarExcecao(1L, requestDto, "BARBEARIA");

        // Assert
        assertNotNull(response);
        assertEquals(excecao.getId(), response.getId());
        assertEquals("BARBEARIA", response.getCriadoPor());
        verify(horarioExcecaoRepository).save(any(JpaHorarioExcecao.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao criar exceção se funcionário não existe")
    void deveLancarErroSeFuncionarioNaoExiste() {
        // Arrange
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> horarioGestaoService.criarExcecao(1L, requestDto, "BARBEARIA"));
    }

    @Test
    @DisplayName("Deve lançar erro se já existe exceção na data")
    void deveLancarErroSeJaExisteExcecao() {
        // Arrange
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(horarioExcecaoRepository.existsByFuncionarioIdAndData(anyLong(), any())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> horarioGestaoService.criarExcecao(1L, requestDto, "BARBEARIA"));
    }

    @Test
    @DisplayName("Deve listar exceções por período")
    void deveListarExcecoesPorPeriodo() {
        // Arrange
        when(funcionarioRepository.findById(1L)).thenReturn(Optional.of(funcionario));
        when(horarioExcecaoRepository.findByFuncionarioIdAndPeriodo(anyLong(), any(), any()))
                .thenReturn(List.of(excecao));

        // Act
        List<HorarioExcecaoResponseDto> lista = horarioGestaoService.listarExcecoesPorPeriodo(
                1L, LocalDate.now(), LocalDate.now().plusDays(10));

        // Assert
        assertFalse(lista.isEmpty());
        assertEquals(1, lista.size());
        assertEquals(excecao.getId(), lista.get(0).getId());
    }

    @Test
    @DisplayName("Deve remover exceção com sucesso (Barbearia remove qualquer um)")
    void deveRemoverExcecaoBarbearia() {
        // Arrange
        excecao.setCriadoPor("PROFISSIONAL"); // Criado por profissional
        when(horarioExcecaoRepository.findById(10L)).thenReturn(Optional.of(excecao));

        // Act
        horarioGestaoService.removerExcecao(10L, 1L, "BARBEARIA");

        // Assert
        verify(horarioExcecaoRepository).delete(excecao);
    }

    @Test
    @DisplayName("Deve remover exceção com sucesso (Profissional remove o seu)")
    void deveRemoverExcecaoProfissional() {
        // Arrange
        excecao.setCriadoPor("PROFISSIONAL");
        when(horarioExcecaoRepository.findById(10L)).thenReturn(Optional.of(excecao));

        // Act
        horarioGestaoService.removerExcecao(10L, 1L, "PROFISSIONAL");

        // Assert
        verify(horarioExcecaoRepository).delete(excecao);
    }

    @Test
    @DisplayName("Deve impedir profissional de remover exceção da barbearia")
    void deveImpedirProfissionalRemoverExcecaoBarbearia() {
        // Arrange
        excecao.setCriadoPor("BARBEARIA");
        when(horarioExcecaoRepository.findById(10L)).thenReturn(Optional.of(excecao));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> horarioGestaoService.removerExcecao(10L, 1L, "PROFISSIONAL"));
    }

    @Test
    @DisplayName("Deve impedir remoção de exceção de outro funcionário")
    void deveImpedirRemoverExcecaoOutroFuncionario() {
        // Arrange
        excecao.setFuncionarioId(99L); // Outro ID
        when(horarioExcecaoRepository.findById(10L)).thenReturn(Optional.of(excecao));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> horarioGestaoService.removerExcecao(10L, 1L, "BARBEARIA"));
    }
}
