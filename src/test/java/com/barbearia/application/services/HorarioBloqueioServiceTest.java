package com.barbearia.application.services;

import com.barbearia.application.dto.HorarioBloqueadoLoteRequestDto;
import com.barbearia.application.dto.HorarioBloqueadoRequestDto;
import com.barbearia.application.dto.HorarioBloqueadoResponseDto;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.entities.JpaHorarioBloqueado;
import com.barbearia.infrastructure.persistence.entities.JpaHorarioFuncionamento;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import com.barbearia.infrastructure.persistence.repositories.HorarioBloqueadoRepository;
import com.barbearia.infrastructure.persistence.repositories.HorarioFuncionamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class HorarioBloqueioServiceTest {

    @Mock
    private HorarioBloqueadoRepository horarioBloqueadoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private HorarioFuncionamentoRepository horarioFuncionamentoRepository;

    @InjectMocks
    private HorarioBloqueioService horarioBloqueioService;

    @Test
    void deveCriarBloqueioComSucesso() {
        Long funcionarioId = 1L;
        HorarioBloqueadoRequestDto request = new HorarioBloqueadoRequestDto(
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Almoço");

        JpaFuncionario funcionario = new JpaFuncionario();
        funcionario.setId(funcionarioId);
        funcionario.setNome("João");

        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));
        when(horarioBloqueadoRepository.existsSobreposicao(any(), any(), any(), any())).thenReturn(false);
        when(horarioBloqueadoRepository.save(any(JpaHorarioBloqueado.class)))
                .thenAnswer(i -> (JpaHorarioBloqueado) i.getArgument(0));

        HorarioBloqueadoResponseDto response = horarioBloqueioService.criarBloqueio(funcionarioId, request,
                "PROFISSIONAL");

        assertNotNull(response);
        assertEquals("João", response.getFuncionarioNome());
        assertEquals("PROFISSIONAL", response.getCriadoPor());
        verify(horarioBloqueadoRepository).save(any());
    }

    @Test
    void deveLancarErroQuandoHorarioInicioDepoisDoFim() {
        Long funcionarioId = 1L;
        HorarioBloqueadoRequestDto request = new HorarioBloqueadoRequestDto(
                LocalDate.now(), LocalTime.of(12, 0), LocalTime.of(11, 0), "Erro");

        JpaFuncionario funcionario = new JpaFuncionario();
        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));

        assertThrows(IllegalArgumentException.class,
                () -> horarioBloqueioService.criarBloqueio(funcionarioId, request, "PROFISSIONAL"));
    }

    @Test
    void deveLancarErroQuandoExisteSobreposicao() {
        Long funcionarioId = 1L;
        HorarioBloqueadoRequestDto request = new HorarioBloqueadoRequestDto(
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "Almoço");

        JpaFuncionario funcionario = new JpaFuncionario();
        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));
        when(horarioBloqueadoRepository.existsSobreposicao(any(), any(), any(), any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> horarioBloqueioService.criarBloqueio(funcionarioId, request, "PROFISSIONAL"));
    }

    @Test
    void deveCriarBloqueiosEmLote() {
        Long funcionarioId = 1L;
        HorarioBloqueadoRequestDto b1 = new HorarioBloqueadoRequestDto(
                LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), "B1");
        HorarioBloqueadoRequestDto b2 = new HorarioBloqueadoRequestDto(
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0), "B2");

        HorarioBloqueadoLoteRequestDto lote = new HorarioBloqueadoLoteRequestDto();
        lote.setBloqueios(Arrays.asList(b1, b2));

        JpaFuncionario funcionario = new JpaFuncionario();
        funcionario.setId(funcionarioId);
        funcionario.setNome("João");

        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));
        when(horarioBloqueadoRepository.existsSobreposicao(any(), any(), any(), any())).thenReturn(false);
        when(horarioBloqueadoRepository.save(any(JpaHorarioBloqueado.class)))
                .thenAnswer(i -> (JpaHorarioBloqueado) i.getArgument(0));

        List<HorarioBloqueadoResponseDto> responses = horarioBloqueioService.criarBloqueiosEmLote(funcionarioId, lote,
                "PROFISSIONAL");

        assertEquals(2, responses.size());
        verify(horarioBloqueadoRepository, times(2)).save(any());
    }

    @Test
    void deveListarBloqueiosPorPeriodo() {
        Long funcionarioId = 1L;
        JpaFuncionario funcionario = new JpaFuncionario();
        funcionario.setId(funcionarioId);
        funcionario.setNome("João");

        JpaHorarioBloqueado bloqueio = new JpaHorarioBloqueado();
        bloqueio.setFuncionarioId(funcionarioId);

        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));
        when(horarioBloqueadoRepository.findByFuncionarioIdAndPeriodo(any(), any(), any()))
                .thenReturn(Collections.singletonList(bloqueio));

        List<HorarioBloqueadoResponseDto> result = horarioBloqueioService.listarBloqueiosPorPeriodo(
                funcionarioId, LocalDate.now(), LocalDate.now().plusDays(5));

        assertFalse(result.isEmpty());
    }

    @Test
    void deveRemoverBloqueioComSucesso() {
        Long bloqueioId = 10L;
        Long funcionarioId = 1L;

        JpaHorarioBloqueado bloqueio = new JpaHorarioBloqueado();
        bloqueio.setId(bloqueioId);
        bloqueio.setFuncionarioId(funcionarioId);
        bloqueio.setCriadoPor("PROFISSIONAL");

        when(horarioBloqueadoRepository.findById(bloqueioId)).thenReturn(Optional.of(bloqueio));

        horarioBloqueioService.removerBloqueio(bloqueioId, funcionarioId, "PROFISSIONAL");

        verify(horarioBloqueadoRepository).delete(bloqueio);
    }

    @Test
    void naoDevePermitirProfissionalRemoverBloqueioDaBarbearia() {
        Long bloqueioId = 10L;
        Long funcionarioId = 1L;

        JpaHorarioBloqueado bloqueio = new JpaHorarioBloqueado();
        bloqueio.setId(bloqueioId);
        bloqueio.setFuncionarioId(funcionarioId);
        bloqueio.setCriadoPor("BARBEARIA");

        when(horarioBloqueadoRepository.findById(bloqueioId)).thenReturn(Optional.of(bloqueio));

        assertThrows(IllegalArgumentException.class,
                () -> horarioBloqueioService.removerBloqueio(bloqueioId, funcionarioId, "PROFISSIONAL"));
    }

    @Test
    void deveBloquearDiaCompleto() {
        Long barbeariaId = 100L;
        Long funcionarioId = 1L;
        LocalDate data = LocalDate.of(2024, 11, 25); // Segunda-feira

        JpaFuncionario funcionario = new JpaFuncionario();
        funcionario.setId(funcionarioId);
        funcionario.setBarbeariaId(barbeariaId);
        funcionario.setNome("João");

        JpaHorarioFuncionamento horario = new JpaHorarioFuncionamento();
        horario.setAtivo(true);
        horario.setHoraAbertura(LocalTime.of(9, 0));
        horario.setHoraFechamento(LocalTime.of(18, 0));

        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));
        when(horarioFuncionamentoRepository.findByBarbeariaIdAndFuncionarioIdAndDiaSemana(
                eq(barbeariaId), eq(funcionarioId), eq(DayOfWeek.MONDAY.getValue())))
                .thenReturn(Collections.singletonList(horario));
        when(horarioBloqueadoRepository.existsSobreposicao(any(), any(), any(), any())).thenReturn(false);
        when(horarioBloqueadoRepository.save(any(JpaHorarioBloqueado.class)))
                .thenAnswer(i -> (JpaHorarioBloqueado) i.getArgument(0));

        HorarioBloqueadoResponseDto response = horarioBloqueioService.bloquearDiaCompleto(barbeariaId, funcionarioId,
                data, "Feriado");

        assertNotNull(response);
        assertEquals(LocalTime.of(9, 0), response.getHorarioInicio());
        assertEquals(LocalTime.of(18, 0), response.getHorarioFim());
        assertEquals("BARBEARIA", response.getCriadoPor());
    }

    @Test
    void deveLancarErroSeProfissionalNaoTrabalhaNoDiaAoBloquearDiaCompleto() {
        Long barbeariaId = 100L;
        Long funcionarioId = 1L;
        LocalDate data = LocalDate.of(2024, 11, 25);

        JpaFuncionario funcionario = new JpaFuncionario();
        funcionario.setId(funcionarioId);
        funcionario.setBarbeariaId(barbeariaId);

        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));
        when(horarioFuncionamentoRepository.findByBarbeariaIdAndFuncionarioIdAndDiaSemana(any(), any(), anyInt()))
                .thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class,
                () -> horarioBloqueioService.bloquearDiaCompleto(barbeariaId, funcionarioId, data, "Feriado"));
    }
}
