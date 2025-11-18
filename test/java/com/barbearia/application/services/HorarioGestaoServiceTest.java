package com.barbearia.application.services;

import com.barbearia.application.dto.FeriadoExcecaoRequestDto;
import com.barbearia.application.dto.FeriadoExcecaoResponseDto;
import com.barbearia.domain.enums.TipoExcecao;
import com.barbearia.infrastructure.persistence.entities.FeriadoExcecaoEntity;
import com.barbearia.infrastructure.persistence.repositories.FeriadoExcecaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para HorarioGestaoService (T17).
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"null", "nullness"})
class HorarioGestaoServiceTest {
    
    @Mock
    private FeriadoExcecaoRepository feriadoExcecaoRepository;
    
    @InjectMocks
    private HorarioGestaoService horarioGestaoService;
    
    private Long barbeariaId;
    private FeriadoExcecaoEntity excecaoFechado;
    private FeriadoExcecaoEntity excecaoHorarioEspecial;
    
    @BeforeEach
    void setUp() {
        barbeariaId = 1L;
        
        // Exceção de fechamento (Natal)
        excecaoFechado = new FeriadoExcecaoEntity();
        excecaoFechado.setId(1L);
        excecaoFechado.setBarbeariaId(barbeariaId);
        excecaoFechado.setData(LocalDate.of(2024, 12, 25));
        excecaoFechado.setTipo(TipoExcecao.FECHADO);
        excecaoFechado.setDescricao("Natal - Fechado");
        excecaoFechado.setAtivo(true);
        
        // Exceção de horário especial (véspera de Ano Novo)
        excecaoHorarioEspecial = new FeriadoExcecaoEntity();
        excecaoHorarioEspecial.setId(2L);
        excecaoHorarioEspecial.setBarbeariaId(barbeariaId);
        excecaoHorarioEspecial.setData(LocalDate.of(2024, 12, 31));
        excecaoHorarioEspecial.setTipo(TipoExcecao.HORARIO_ESPECIAL);
        excecaoHorarioEspecial.setHorarioAbertura(LocalTime.of(9, 0));
        excecaoHorarioEspecial.setHorarioFechamento(LocalTime.of(14, 0));
        excecaoHorarioEspecial.setDescricao("Véspera de Ano Novo");
        excecaoHorarioEspecial.setAtivo(true);
    }
    
    @Test
    void deveListarExcecoesComSucesso() {
        // Arrange
        when(feriadoExcecaoRepository.findByBarbeariaId(barbeariaId))
                .thenReturn(Arrays.asList(excecaoFechado, excecaoHorarioEspecial));
        
        // Act
        List<FeriadoExcecaoResponseDto> resultado = horarioGestaoService.listarExcecoes(barbeariaId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(feriadoExcecaoRepository, times(1)).findByBarbeariaId(barbeariaId);
    }
    
    @Test
    void deveListarExcecoesNoPeriodoComSucesso() {
        // Arrange
        LocalDate dataInicio = LocalDate.of(2024, 12, 1);
        LocalDate dataFim = LocalDate.of(2024, 12, 31);
        
        when(feriadoExcecaoRepository.findByBarbeariaIdAndDataBetweenAndAtivo(
                eq(barbeariaId), eq(dataInicio), eq(dataFim), eq(true)))
                .thenReturn(Arrays.asList(excecaoFechado, excecaoHorarioEspecial));
        
        // Act
        List<FeriadoExcecaoResponseDto> resultado = horarioGestaoService
                .listarExcecoesNoPeriodo(barbeariaId, dataInicio, dataFim);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }
    
    @Test
    void deveBuscarExcecaoPorIdComSucesso() {
        // Arrange
        Long excecaoId = 1L;
        when(feriadoExcecaoRepository.findById(excecaoId))
                .thenReturn(Optional.of(excecaoFechado));
        
        // Act
        FeriadoExcecaoResponseDto resultado = horarioGestaoService
                .buscarExcecaoPorId(excecaoId, barbeariaId);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(excecaoId, resultado.getId());
        assertEquals(TipoExcecao.FECHADO, resultado.getTipo());
    }
    
    @Test
    void deveLancarExcecaoQuandoExcecaoNaoEncontrada() {
        // Arrange
        Long excecaoId = 999L;
        when(feriadoExcecaoRepository.findById(excecaoId))
                .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            horarioGestaoService.buscarExcecaoPorId(excecaoId, barbeariaId);
        });
    }
    
    @Test
    void deveLancarExcecaoQuandoExcecaoNaoPertenceABarbearia() {
        // Arrange
        Long excecaoId = 1L;
        Long outraBarbeariaId = 2L;
        when(feriadoExcecaoRepository.findById(excecaoId))
                .thenReturn(Optional.of(excecaoFechado));
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horarioGestaoService.buscarExcecaoPorId(excecaoId, outraBarbeariaId);
        });
        
        assertTrue(exception.getMessage().contains("não pertence"));
    }
    
    @Test
    void deveCriarExcecaoFechadoComSucesso() {
        // Arrange
        FeriadoExcecaoRequestDto requestDto = new FeriadoExcecaoRequestDto();
        requestDto.setData(LocalDate.of(2024, 12, 25));
        requestDto.setTipo(TipoExcecao.FECHADO);
        requestDto.setDescricao("Natal - Fechado");
        
        when(feriadoExcecaoRepository.existsByBarbeariaIdAndData(barbeariaId, requestDto.getData()))
                .thenReturn(false);
        
        FeriadoExcecaoEntity savedEntity = excecaoFechado;
        when(feriadoExcecaoRepository.save(any(FeriadoExcecaoEntity.class)))
                .thenReturn(savedEntity);
        
        // Act
        FeriadoExcecaoResponseDto resultado = horarioGestaoService
                .criarExcecao(barbeariaId, requestDto);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(TipoExcecao.FECHADO, resultado.getTipo());
        verify(feriadoExcecaoRepository, times(1)).save(any(FeriadoExcecaoEntity.class));
    }
    
    @Test
    void deveCriarExcecaoHorarioEspecialComSucesso() {
        // Arrange
        FeriadoExcecaoRequestDto requestDto = new FeriadoExcecaoRequestDto();
        requestDto.setData(LocalDate.of(2024, 12, 31));
        requestDto.setTipo(TipoExcecao.HORARIO_ESPECIAL);
        requestDto.setHorarioAbertura(LocalTime.of(9, 0));
        requestDto.setHorarioFechamento(LocalTime.of(14, 0));
        requestDto.setDescricao("Véspera de Ano Novo");
        
        when(feriadoExcecaoRepository.existsByBarbeariaIdAndData(barbeariaId, requestDto.getData()))
                .thenReturn(false);
        when(feriadoExcecaoRepository.save(any(FeriadoExcecaoEntity.class)))
                .thenReturn(excecaoHorarioEspecial);
        
        // Act
        FeriadoExcecaoResponseDto resultado = horarioGestaoService
                .criarExcecao(barbeariaId, requestDto);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(TipoExcecao.HORARIO_ESPECIAL, resultado.getTipo());
        assertEquals(LocalTime.of(9, 0), resultado.getHorarioAbertura());
        assertEquals(LocalTime.of(14, 0), resultado.getHorarioFechamento());
    }
    
    @Test
    void deveLancarExcecaoQuandoJaExisteExcecaoParaData() {
        // Arrange
        FeriadoExcecaoRequestDto requestDto = new FeriadoExcecaoRequestDto();
        requestDto.setData(LocalDate.of(2024, 12, 25));
        requestDto.setTipo(TipoExcecao.FECHADO);
        
        when(feriadoExcecaoRepository.existsByBarbeariaIdAndData(barbeariaId, requestDto.getData()))
                .thenReturn(true);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            horarioGestaoService.criarExcecao(barbeariaId, requestDto);
        });
        
        assertTrue(exception.getMessage().contains("Já existe"));
    }
    
    @Test
    void deveLancarExcecaoQuandoHorarioEspecialSemHorarios() {
        // Arrange
        FeriadoExcecaoRequestDto requestDto = new FeriadoExcecaoRequestDto();
        requestDto.setData(LocalDate.of(2024, 12, 31));
        requestDto.setTipo(TipoExcecao.HORARIO_ESPECIAL);
        requestDto.setDescricao("Véspera de Ano Novo");
        // Sem horários definidos
        
        when(feriadoExcecaoRepository.existsByBarbeariaIdAndData(barbeariaId, requestDto.getData()))
                .thenReturn(false);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            horarioGestaoService.criarExcecao(barbeariaId, requestDto);
        });
    }
    
    @Test
    void deveAtualizarExcecaoComSucesso() {
        // Arrange
        Long excecaoId = 1L;
        FeriadoExcecaoRequestDto requestDto = new FeriadoExcecaoRequestDto();
        requestDto.setData(LocalDate.of(2024, 12, 26));
        requestDto.setTipo(TipoExcecao.FECHADO);
        requestDto.setDescricao("Feriado Atualizado");
        
        when(feriadoExcecaoRepository.findById(excecaoId))
                .thenReturn(Optional.of(excecaoFechado));
        when(feriadoExcecaoRepository.save(any(FeriadoExcecaoEntity.class)))
                .thenReturn(excecaoFechado);
        
        // Act
        FeriadoExcecaoResponseDto resultado = horarioGestaoService
                .atualizarExcecao(excecaoId, barbeariaId, requestDto);
        
        // Assert
        assertNotNull(resultado);
        verify(feriadoExcecaoRepository, times(1)).save(any(FeriadoExcecaoEntity.class));
    }
    
    @Test
    void deveRemoverExcecaoComSucesso() {
        // Arrange
        Long excecaoId = 1L;
        when(feriadoExcecaoRepository.findById(excecaoId))
                .thenReturn(Optional.of(excecaoFechado));
        
        // Act
        horarioGestaoService.removerExcecao(excecaoId, barbeariaId);
        
        // Assert
        verify(feriadoExcecaoRepository, times(1)).save(any(FeriadoExcecaoEntity.class));
    }
    
    @Test
    void deveRetornarFalsoQuandoBarbeariaFechadaPorExcecao() {
        // Arrange
        LocalDate data = LocalDate.of(2024, 12, 25);
        LocalTime horario = LocalTime.of(10, 0);
        
        when(feriadoExcecaoRepository.findByBarbeariaIdAndDataAndAtivo(barbeariaId, data, true))
                .thenReturn(Optional.of(excecaoFechado));
        
        // Act
        boolean estaAberto = horarioGestaoService.estaAberto(barbeariaId, data, horario);
        
        // Assert
        assertFalse(estaAberto);
    }
    
    @Test
    void deveRetornarTrueQuandoHorarioEspecialEDentroDoHorario() {
        // Arrange
        LocalDate data = LocalDate.of(2024, 12, 31);
        LocalTime horario = LocalTime.of(10, 0); // Dentro do horário especial (9h-14h)
        
        when(feriadoExcecaoRepository.findByBarbeariaIdAndDataAndAtivo(barbeariaId, data, true))
                .thenReturn(Optional.of(excecaoHorarioEspecial));
        
        // Act
        boolean estaAberto = horarioGestaoService.estaAberto(barbeariaId, data, horario);
        
        // Assert
        assertTrue(estaAberto);
    }
    
    @Test
    void deveRetornarFalseQuandoHorarioEspecialEForaDoHorario() {
        // Arrange
        LocalDate data = LocalDate.of(2024, 12, 31);
        LocalTime horario = LocalTime.of(15, 0); // Fora do horário especial (9h-14h)
        
        when(feriadoExcecaoRepository.findByBarbeariaIdAndDataAndAtivo(barbeariaId, data, true))
                .thenReturn(Optional.of(excecaoHorarioEspecial));
        
        // Act
        boolean estaAberto = horarioGestaoService.estaAberto(barbeariaId, data, horario);
        
        // Assert
        assertFalse(estaAberto);
    }
}
