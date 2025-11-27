package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AgendamentoBriefDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.AgendamentoService;
import com.barbearia.application.services.ClienteService;
import com.barbearia.domain.enums.StatusAgendamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para ClienteController.
 * Valida os endpoints REST relacionados aos clientes.
 */
@WebMvcTest(ClienteController.class)
@DisplayName("ClienteController - Testes de Integração")
@SuppressWarnings("null")
class ClienteControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AgendamentoService agendamentoService;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private ClienteService clienteService;

        private String validJwtToken;
        private Long clienteId;

        @BeforeEach
        void setUp() {
                validJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGUiOiJDTElFTlRFIn0.test";
                clienteId = 1L;
        }

        @Test
        @DisplayName("Deve retornar 401 quando não houver token de autenticação")
        void deveRetornar401QuandoNaoHouverToken() throws Exception {
                // Act & Assert
                // Quando não há header Authorization, o controller retorna 401
                mockMvc.perform(get("/api/clientes/meus-agendamentos/historico")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized());

                // Verifica que o service não foi chamado
                verify(agendamentoService, never()).listarHistoricoCliente(any());
        }

        @Test
        @DisplayName("Deve retornar 200 com lista de agendamentos passados quando token válido")
        @WithMockUser
        void deveRetornar200ComListaDeAgendamentosQuandoTokenValido() throws Exception {
                // Arrange
                LocalDateTime now = LocalDateTime.now();
                List<AgendamentoBriefDto> historico = Arrays.asList(
                                new AgendamentoBriefDto(
                                                1L,
                                                now.minusDays(2),
                                                StatusAgendamento.CONCLUIDO,
                                                "Barbearia Central",
                                                "João Silva",
                                                "Corte + Barba",
                                                "Cliente satisfeito",
                                                false),
                                new AgendamentoBriefDto(
                                                2L,
                                                now.minusDays(15),
                                                StatusAgendamento.CONCLUIDO,
                                                "Barbearia Central",
                                                "Pedro Santos",
                                                "Corte Simples",
                                                null,
                                                false));

                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(clienteId);
                when(agendamentoService.listarHistoricoCliente(clienteId))
                                .thenReturn(historico);

                // Act & Assert
                mockMvc.perform(get("/api/clientes/meus-agendamentos/historico")
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].id", is(1)))
                                .andExpect(jsonPath("$[0].status", is("CONCLUIDO")))
                                .andExpect(jsonPath("$[0].nomeBarbearia", is("Barbearia Central")))
                                .andExpect(jsonPath("$[0].nomeBarbeiro", is("João Silva")))
                                .andExpect(jsonPath("$[0].nomeServico", is("Corte + Barba")))
                                .andExpect(jsonPath("$[0].observacoes", is("Cliente satisfeito")))
                                .andExpect(jsonPath("$[1].id", is(2)))
                                .andExpect(jsonPath("$[1].status", is("CONCLUIDO")))
                                .andExpect(jsonPath("$[1].observacoes").doesNotExist());

                verify(jwtService, times(1)).extractClaim(eq(validJwtToken), eq("userId"));
                verify(agendamentoService, times(1)).listarHistoricoCliente(clienteId);
        }

        @Test
        @DisplayName("Deve retornar 200 com lista vazia quando cliente não tem histórico")
        @WithMockUser
        void deveRetornar200ComListaVaziaQuandoClienteNaoTemHistorico() throws Exception {
                // Arrange
                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(clienteId);
                when(agendamentoService.listarHistoricoCliente(clienteId))
                                .thenReturn(Collections.emptyList());

                // Act & Assert
                mockMvc.perform(get("/api/clientes/meus-agendamentos/historico")
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(0)));

                verify(agendamentoService, times(1)).listarHistoricoCliente(clienteId);
        }

        @Test
        @DisplayName("Deve extrair clienteId corretamente do token JWT")
        @WithMockUser
        void deveExtrairClienteIdCorretamenteDoToken() throws Exception {
                // Arrange
                Long expectedClienteId = 42L;
                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(expectedClienteId);
                when(agendamentoService.listarHistoricoCliente(expectedClienteId))
                                .thenReturn(Collections.emptyList());

                // Act
                mockMvc.perform(get("/api/clientes/meus-agendamentos/historico")
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

                // Assert
                verify(jwtService, times(1)).extractClaim(eq(validJwtToken), eq("userId"));
                verify(agendamentoService, times(1)).listarHistoricoCliente(expectedClienteId);
        }

        @Test
        @DisplayName("Deve tratar userId como Number quando retornado pelo JWT")
        @WithMockUser
        void deveTratarUserIdComoNumberQuandoRetornadoPeloJwt() throws Exception {
                // Arrange
                // JWT pode retornar userId como Integer ao invés de Long
                Integer userIdAsInteger = 5;
                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(userIdAsInteger);
                when(agendamentoService.listarHistoricoCliente(5L))
                                .thenReturn(Collections.emptyList());

                // Act
                mockMvc.perform(get("/api/clientes/meus-agendamentos/historico")
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

                // Assert
                verify(agendamentoService, times(1)).listarHistoricoCliente(5L);
        }
}
