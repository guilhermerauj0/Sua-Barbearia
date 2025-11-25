package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.HorarioExcecaoRequestDto;
import com.barbearia.application.dto.HorarioExcecaoResponseDto;
import com.barbearia.application.services.HorarioBloqueioService;
import com.barbearia.application.services.HorarioGestaoService;
import com.barbearia.application.services.ProfissionalLinkService;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProfissionalDashboardController - Testes")
class ProfissionalDashboardControllerTest {

    @Mock
    private ProfissionalLinkService profissionalLinkService;

    @Mock
    private HorarioBloqueioService horarioBloqueioService;

    @Mock
    private HorarioGestaoService horarioGestaoService;

    @InjectMocks
    private ProfissionalDashboardController controller;

    private JpaFuncionario funcionario;
    private String validToken = "valid-token-123";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        funcionario = new JpaFuncionario();
        funcionario.setId(1L);
        funcionario.setNome("João");
        funcionario.setEmail("joao@teste.com");
        funcionario.setPerfilType(com.barbearia.domain.enums.TipoPerfil.BARBEIRO);
    }

    @Test
    @DisplayName("GET /dashboard - Deve retornar dados do dashboard com sucesso")
    void deveRetornarDashboardComSucesso() throws Exception {
        // Arrange
        when(profissionalLinkService.validarToken(validToken)).thenReturn(funcionario);

        // Act & Assert
        mockMvc.perform(get("/api/profissional/" + validToken))
                .andExpect(status().isOk());

        verify(profissionalLinkService, times(1)).validarToken(validToken);
    }

    @Test
    @DisplayName("GET /dashboard - Deve retornar 401 se token inválido")
    void deveRetornar401SeTokenInvalido() throws Exception {
        // Arrange
        when(profissionalLinkService.validarToken("invalid")).thenThrow(new IllegalArgumentException("Token inválido"));

        // Act & Assert
        mockMvc.perform(get("/api/profissional/invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /excecoes - Deve listar exceções com sucesso")
    void deveListarExcecoesComSucesso() {
        // Arrange
        when(profissionalLinkService.validarToken(validToken)).thenReturn(funcionario);
        when(horarioGestaoService.listarExcecoesPorPeriodo(eq(1L), any(), any()))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<?> response = controller.listarExcecoes(validToken, "2024-01-01", "2024-02-01");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("POST /excecoes - Deve criar exceção com sucesso")
    void deveCriarExcecaoComSucesso() {
        // Arrange
        HorarioExcecaoRequestDto dto = new HorarioExcecaoRequestDto();
        dto.setData(LocalDate.now().plusDays(1));
        dto.setHoraAbertura(LocalTime.of(9, 0));
        dto.setHoraFechamento(LocalTime.of(18, 0));

        HorarioExcecaoResponseDto responseDto = new HorarioExcecaoResponseDto();
        responseDto.setId(10L);

        when(profissionalLinkService.validarToken(validToken)).thenReturn(funcionario);
        when(horarioGestaoService.criarExcecao(eq(1L), any(), eq("PROFISSIONAL")))
                .thenReturn(responseDto);

        // Act
        ResponseEntity<?> response = controller.criarExcecao(validToken, dto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("DELETE /excecoes/{id} - Deve remover exceção com sucesso")
    void deveRemoverExcecaoComSucesso() {
        // Arrange
        when(profissionalLinkService.validarToken(validToken)).thenReturn(funcionario);

        // Act
        ResponseEntity<?> response = controller.removerExcecao(validToken, 10L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(horarioGestaoService).removerExcecao(10L, 1L, "PROFISSIONAL");
    }
}
