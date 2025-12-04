package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AvaliacaoRequestDto;
import com.barbearia.application.dto.AvaliacaoResponseDto;
import com.barbearia.application.dto.EstatisticasAvaliacoesDto;
import com.barbearia.application.services.AvaliacaoService;
import com.barbearia.application.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvaliacaoController.class)
@DisplayName("AvaliacaoController - Testes de Integração")
@SuppressWarnings("null")
class AvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AvaliacaoService avaliacaoService;

    @MockitoBean
    private JwtService jwtService;

    private String validJwtToken;
    private Long clienteId;
    private AvaliacaoRequestDto requestDto;
    private AvaliacaoResponseDto responseDto;

    @BeforeEach
    void setUp() {
        validJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGUiOiJDTElFTlRFIn0.test";
        clienteId = 1L;

        requestDto = new AvaliacaoRequestDto();
        requestDto.setAgendamentoId(1L);
        requestDto.setBarbeariaId(1L);
        requestDto.setNotaServico(5);
        requestDto.setNotaAmbiente(5);
        requestDto.setNotaLimpeza(5);
        requestDto.setNotaAtendimento(5);
        requestDto.setComentario("Excelente!");

        responseDto = new AvaliacaoResponseDto(
                1L, 1L, 1L, "João Silva",
                5, 5, 5, 5, new BigDecimal("5.00"),
                "Excelente!", LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve criar avaliação com sucesso")
    @WithMockUser
    void deveCriarAvaliacaoComSucesso() throws Exception {
        // Arrange
        when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                .thenReturn(clienteId);
        when(jwtService.extractClaim(eq(validJwtToken), eq("role")))
                .thenReturn("CLIENTE");
        when(avaliacaoService.criarAvaliacao(eq(clienteId), any(AvaliacaoRequestDto.class)))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/avaliacoes")
                .with(csrf())
                .header("Authorization", "Bearer " + validJwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.clienteNome", is("João Silva")))
                .andExpect(jsonPath("$.notaGeral", is(5.00)));
    }

    @Test
    @DisplayName("Deve retornar 400 quando token inválido")
    @WithMockUser
    void deveRetornar400QuandoTokenInvalido() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/avaliacoes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token JWT não fornecido"));
    }

    @Test
    @DisplayName("Deve listar avaliações de uma barbearia")
    @WithMockUser
    void deveListarAvaliacoes() throws Exception {
        // Arrange
        when(avaliacaoService.buscarAvaliacoesPorBarbearia(1L))
                .thenReturn(List.of(responseDto));

        // Act & Assert
        mockMvc.perform(get("/api/barbearias/1/avaliacoes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].clienteNome", is("João Silva")));
    }

    @Test
    @DisplayName("Deve retornar estatísticas de uma barbearia")
    @WithMockUser
    void deveRetornarEstatisticas() throws Exception {
        // Arrange
        EstatisticasAvaliacoesDto stats = new EstatisticasAvaliacoesDto(
                1L, 4.5, 4.0, 5.0, 4.5, 4.5, 10L);
        when(avaliacaoService.calcularEstatisticas(1L)).thenReturn(stats);

        // Act & Assert
        mockMvc.perform(get("/api/barbearias/1/estatisticas-avaliacoes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mediaGeral", is(4.5)))
                .andExpect(jsonPath("$.totalAvaliacoes", is(10)));
    }
}
