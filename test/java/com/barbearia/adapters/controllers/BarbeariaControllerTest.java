package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.BarbeariaListItemDto;
import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.services.BarbeariaService;
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
import static org.mockito.ArgumentMatchers.anyLong;
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

    @InjectMocks
    private BarbeariaController barbeariaController;

    @BeforeEach
    void setUp() {
        // Inicialização comum para cada teste
    }

    @Test
    @DisplayName("GET /api/barbearias - Deve listar barbearias ativas com sucesso")
    void deveListarBarbeariaComSucesso() {
        // Arrange
        BarbeariaListItemDto barbearia1 = new BarbeariaListItemDto();
        barbearia1.setId(1L);
        barbearia1.setNome("Barbearia Premium");
        barbearia1.setNomeFantasia("Premium Cuts");
        barbearia1.setTelefone("(11) 98765-4321");

        BarbeariaListItemDto barbearia2 = new BarbeariaListItemDto();
        barbearia2.setId(2L);
        barbearia2.setNome("Barbearia Elegance");
        barbearia2.setNomeFantasia("Elegance Barber");
        barbearia2.setTelefone("(11) 99876-5432");

        when(barbeariaService.listarBarbearias())
                .thenReturn(Arrays.asList(barbearia1, barbearia2));

        // Act
        ResponseEntity<?> response = barbeariaController.listarBarbearias();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        List<BarbeariaListItemDto> resultado = (List<BarbeariaListItemDto>) response.getBody();
        assertEquals(2, resultado.size());
        assertEquals("Barbearia Premium", resultado.get(0).getNome());
        
        verify(barbeariaService, times(1)).listarBarbearias();
    }

    @Test
    @DisplayName("GET /api/barbearias - Deve retornar lista vazia quando não há barbearias")
    void deveRetornarListaVaziaQuandoNaoHaBarbearias() {
        // Arrange
        when(barbeariaService.listarBarbearias())
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<?> response = barbeariaController.listarBarbearias();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<BarbeariaListItemDto> resultado = (List<BarbeariaListItemDto>) response.getBody();
        assertTrue(resultado.isEmpty());
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
}
