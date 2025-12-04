package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.dto.HorarioExcecaoRequestDto;
import com.barbearia.application.dto.HorarioExcecaoResponseDto;
import com.barbearia.application.services.BarbeariaService;
import com.barbearia.application.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Testes para BarbeariaController.
 * 
 * Testa os endpoints de listagem de barbearias e seus serviços.
 * Valida respostas HTTP, conversão de DTOs e tratamento de erros.
 */
@SuppressWarnings("null")
@ExtendWith(MockitoExtension.class)
@DisplayName("BarbeariaController - Testes de Endpoints")
class BarbeariaControllerTest {

    @Mock
    private BarbeariaService barbeariaService;

    @Mock
    private com.barbearia.application.services.HorarioGestaoService horarioGestaoService;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private BarbeariaController barbeariaController;

    @BeforeEach
    void setUp() {
        // Inicialização comum para cada teste
    }

    @Test
    @DisplayName("GET /api/barbearias/{id}/servicos - Deve listar serviços com sucesso")
    void deveListarServicosComSucesso() {
        // Arrange
        Long barbeariaId = 1L;

        ServicoDto servico1 = new ServicoDto();
        servico1.setId(1L);
        servico1.setNome("Corte de Cabelo");
        servico1.setPreco(BigDecimal.valueOf(50.00));
        servico1.setDuracao(30);
        servico1.setTipoServico("CORTE");

        ServicoDto servico2 = new ServicoDto();
        servico2.setId(2L);
        servico2.setNome("Barba Completa");
        servico2.setPreco(BigDecimal.valueOf(35.00));
        servico2.setDuracao(20);
        servico2.setTipoServico("BARBA");

        when(barbeariaService.listarServicosPorBarbearia(barbeariaId))
                .thenReturn(Arrays.asList(servico1, servico2));

        // Act
        ResponseEntity<?> response = barbeariaController.listarServicosPorBarbearia(barbeariaId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        List<ServicoDto> resultado = (List<ServicoDto>) response.getBody();
        assertEquals(2, resultado.size());
        assertEquals("Corte de Cabelo", resultado.get(0).getNome());
        assertEquals("CORTE", resultado.get(0).getTipoServico());
        assertEquals("BARBA", resultado.get(1).getTipoServico());

        verify(barbeariaService, times(1)).listarServicosPorBarbearia(barbeariaId);
    }

    @Test
    @DisplayName("GET /api/barbearias/{id}/servicos - Deve retornar 404 quando barbearia não existe")
    void deveRetornar404QuandoBarbeariaONaoExiste() {
        // Arrange
        Long barbeariaId = 999L;
        when(barbeariaService.listarServicosPorBarbearia(barbeariaId))
                .thenThrow(new IllegalArgumentException("Barbearia não encontrada"));

        // Act
        ResponseEntity<?> response = barbeariaController.listarServicosPorBarbearia(barbeariaId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Barbearia não encontrada"));
    }

    @Test
    @DisplayName("GET /api/barbearias/{id}/servicos - Deve retornar 400 quando barbearia está inativa")
    void deveRetornar400QuandoBarbeariaEstaInativa() {
        // Arrange
        Long barbeariaId = 1L;
        when(barbeariaService.listarServicosPorBarbearia(barbeariaId))
                .thenThrow(new IllegalArgumentException("Barbearia está inativa"));

        // Act
        ResponseEntity<?> response = barbeariaController.listarServicosPorBarbearia(barbeariaId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Barbearia está inativa"));
    }

    @Test
    @DisplayName("GET /api/barbearias/{id}/servicos - Deve retornar lista vazia quando barbearia não tem serviços")
    void deveRetornarListaVaziaQuandoBarbeariaONaoTemServicos() {
        // Arrange
        Long barbeariaId = 1L;
        when(barbeariaService.listarServicosPorBarbearia(barbeariaId))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<?> response = barbeariaController.listarServicosPorBarbearia(barbeariaId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<ServicoDto> resultado = (List<ServicoDto>) response.getBody();
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("GET /api/barbearias/{id}/servicos - Deve retornar serviços com diferentes tipos")
    void deveRetornarServicosComDiferentesTipos() {
        // Arrange
        Long barbeariaId = 1L;

        ServicoDto corte = new ServicoDto();
        corte.setNome("Corte");
        corte.setTipoServico("CORTE");

        ServicoDto barba = new ServicoDto();
        barba.setNome("Barba");
        barba.setTipoServico("BARBA");

        ServicoDto manicure = new ServicoDto();
        manicure.setNome("Manicure");
        manicure.setTipoServico("MANICURE");

        ServicoDto sobrancelha = new ServicoDto();
        sobrancelha.setNome("Sobrancelha");
        sobrancelha.setTipoServico("SOBRANCELHA");

        ServicoDto coloracao = new ServicoDto();
        coloracao.setNome("Coloração");
        coloracao.setTipoServico("COLORACAO");

        ServicoDto tratamento = new ServicoDto();
        tratamento.setNome("Tratamento Capilar");
        tratamento.setTipoServico("TRATAMENTO_CAPILAR");

        when(barbeariaService.listarServicosPorBarbearia(barbeariaId))
                .thenReturn(Arrays.asList(corte, barba, manicure, sobrancelha, coloracao, tratamento));

        // Act
        ResponseEntity<?> response = barbeariaController.listarServicosPorBarbearia(barbeariaId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<ServicoDto> resultado = (List<ServicoDto>) response.getBody();
        assertEquals(6, resultado.size());
        assertEquals("CORTE", resultado.get(0).getTipoServico());
        assertEquals("BARBA", resultado.get(1).getTipoServico());
        assertEquals("MANICURE", resultado.get(2).getTipoServico());
        assertEquals("SOBRANCELHA", resultado.get(3).getTipoServico());
        assertEquals("COLORACAO", resultado.get(4).getTipoServico());
        assertEquals("TRATAMENTO_CAPILAR", resultado.get(5).getTipoServico());
    }

    @Test
    @DisplayName("GET /api/barbearias/{id}/servicos - Deve tratar exceção genérica com status 500")
    void deveRetornar500ComExcecaoGenerica() {
        // Arrange
        Long barbeariaId = 1L;
        when(barbeariaService.listarServicosPorBarbearia(anyLong()))
                .thenThrow(new RuntimeException("Erro inesperado no banco de dados"));

        // Act
        ResponseEntity<?> response = barbeariaController.listarServicosPorBarbearia(barbeariaId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // ===== TESTES DE EXCEÇÕES DE HORÁRIO =====

    @Test
    @DisplayName("POST /funcionarios/{id}/excecoes - Deve criar exceção com sucesso")
    void deveCriarExcecaoFuncionarioComSucesso() {
        // Arrange
        Long funcionarioId = 1L;
        HorarioExcecaoRequestDto requestDto = new HorarioExcecaoRequestDto();
        HorarioExcecaoResponseDto responseDto = new HorarioExcecaoResponseDto();
        responseDto.setId(10L);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid_token");

        // Mock do JWT extraction
        when(jwtService.extractClaim(any(), any())).thenReturn(1L);
        when(horarioGestaoService.criarExcecao(eq(funcionarioId), any(), eq("BARBEARIA")))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = barbeariaController.criarExcecaoFuncionario(funcionarioId, requestDto,
                mockRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /funcionarios/{id}/excecoes - Deve listar exceções com sucesso")
    void deveListarExcecoesFuncionarioComSucesso() {
        // Arrange
        Long funcionarioId = 1L;
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid_token");

        when(jwtService.extractClaim(any(), any())).thenReturn(1L);
        when(horarioGestaoService.listarExcecoesPorPeriodo(eq(funcionarioId), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<?> response = barbeariaController.listarExcecoesFuncionario(funcionarioId, null, null,
                mockRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /funcionarios/{id}/excecoes/{excecaoId} - Deve remover exceção com sucesso")
    void deveRemoverExcecaoFuncionarioComSucesso() {
        // Arrange
        Long funcionarioId = 1L;
        Long excecaoId = 10L;
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer valid_token");

        when(jwtService.extractClaim(any(), any())).thenReturn(1L);

        // Act
        ResponseEntity<?> response = barbeariaController.removerExcecaoFuncionario(funcionarioId, excecaoId,
                mockRequest);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(horarioGestaoService).removerExcecao(excecaoId, funcionarioId, "BARBEARIA");
    }
}
