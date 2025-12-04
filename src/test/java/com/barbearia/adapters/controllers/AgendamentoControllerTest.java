package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AgendamentoRequestDto;
import com.barbearia.application.dto.AgendamentoResponseDto;
import com.barbearia.application.services.AgendamentoService;
import com.barbearia.application.security.JwtService;
import com.barbearia.domain.enums.StatusAgendamento;
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

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para AgendamentoController.
 * Valida os endpoints REST relacionados aos agendamentos.
 */
@WebMvcTest(AgendamentoController.class)
@DisplayName("AgendamentoController - Testes de Integração")
@SuppressWarnings("null")
class AgendamentoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private AgendamentoService agendamentoService;

        @MockitoBean
        private JwtService jwtService;

        private String validJwtToken;
        private Long clienteId;
        private Long servicoId;
        private Long funcionarioId;
        private LocalDateTime dataHora;

        @BeforeEach
        void setUp() {
                validJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGUiOiJDTElFTlRFIn0.test";
                clienteId = 1L;
                servicoId = 1L;
                funcionarioId = 1L;
                dataHora = LocalDateTime.now().plusDays(7).withHour(14).withMinute(30);
        }

        // ========== TESTES POST /api/agendamentos ==========

        @Test
        @DisplayName("Deve retornar 401 quando não houver token de autenticação")
        @WithMockUser
        void deveRetornar401QuandoNaoHouverToken() throws Exception {
                // Arrange
                AgendamentoRequestDto request = new AgendamentoRequestDto(servicoId, funcionarioId, dataHora);

                // Act & Assert
                mockMvc.perform(post("/api/agendamentos")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden());

                verify(agendamentoService, never()).criarAgendamento(any(), any());
        }

        @Test
        @DisplayName("Deve retornar 403 quando usuário não é cliente")
        @WithMockUser
        void deveRetornar403QuandoUsuarioNaoEhCliente() throws Exception {
                // Arrange
                AgendamentoRequestDto request = new AgendamentoRequestDto(servicoId, funcionarioId, dataHora);
                String barbeariaToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInJvbGUiOiJCQVJCRUFSSUEifQ.test";

                when(jwtService.extractClaim(eq(barbeariaToken), eq("userId")))
                                .thenReturn(clienteId);
                when(jwtService.extractClaim(eq(barbeariaToken), eq("role")))
                                .thenReturn("BARBEARIA");

                // Act & Assert
                mockMvc.perform(post("/api/agendamentos")
                                .with(csrf())
                                .header("Authorization", "Bearer " + barbeariaToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isForbidden())
                                .andExpect(jsonPath("$.message", is("Apenas clientes podem criar agendamentos")));

                verify(agendamentoService, never()).criarAgendamento(any(), any());
        }

        @Test
        @DisplayName("Deve criar agendamento com sucesso e retornar 201")
        @WithMockUser
        void deveCriarAgendamentoComSucessoERetornar201() throws Exception {
                // Arrange
                AgendamentoRequestDto request = new AgendamentoRequestDto(
                                servicoId,
                                funcionarioId,
                                dataHora,
                                "Corte normal com máquina 2");

                AgendamentoResponseDto resposta = new AgendamentoResponseDto(
                                123L,
                                clienteId,
                                1L, // barbeariaId
                                servicoId,
                                funcionarioId,
                                dataHora,
                                StatusAgendamento.PENDENTE,
                                "Corte normal com máquina 2",
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                false);

                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(clienteId);
                when(jwtService.extractClaim(eq(validJwtToken), eq("role")))
                                .thenReturn("CLIENTE");
                when(agendamentoService.criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class)))
                                .thenReturn(resposta);

                // Act & Assert
                mockMvc.perform(post("/api/agendamentos")
                                .with(csrf())
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(123)))
                                .andExpect(jsonPath("$.clienteId", is(1)))
                                .andExpect(jsonPath("$.servicoId", is(1)))
                                .andExpect(jsonPath("$.funcionarioId", is(1)))
                                .andExpect(jsonPath("$.status", is("PENDENTE")))
                                .andExpect(jsonPath("$.observacoes", is("Corte normal com máquina 2")));

                verify(agendamentoService, times(1))
                                .criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar 400 quando serviço não existe")
        @WithMockUser
        void deveRetornar400QuandoServicoNaoExiste() throws Exception {
                // Arrange
                AgendamentoRequestDto request = new AgendamentoRequestDto(999L, funcionarioId, dataHora);

                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(clienteId);
                when(jwtService.extractClaim(eq(validJwtToken), eq("role")))
                                .thenReturn("CLIENTE");
                when(agendamentoService.criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class)))
                                .thenThrow(new IllegalArgumentException("Serviço com ID 999 não existe"));

                // Act & Assert
                mockMvc.perform(post("/api/agendamentos")
                                .with(csrf())
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", is("Serviço com ID 999 não existe")));

                verify(agendamentoService, times(1))
                                .criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar 400 quando funcionário não existe")
        @WithMockUser
        void deveRetornar400QuandoFuncionarioNaoExiste() throws Exception {
                // Arrange
                AgendamentoRequestDto request = new AgendamentoRequestDto(servicoId, 999L, dataHora);

                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(clienteId);
                when(jwtService.extractClaim(eq(validJwtToken), eq("role")))
                                .thenReturn("CLIENTE");
                when(agendamentoService.criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class)))
                                .thenThrow(new IllegalArgumentException("Funcionário com ID 999 não existe"));

                // Act & Assert
                mockMvc.perform(post("/api/agendamentos")
                                .with(csrf())
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", is("Funcionário com ID 999 não existe")));

                verify(agendamentoService, times(1))
                                .criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar 422 quando horário não está disponível (conflito)")
        @WithMockUser
        void deveRetornar422QuandoHorarioNaoEstaDisponivel() throws Exception {
                // Arrange
                AgendamentoRequestDto request = new AgendamentoRequestDto(servicoId, funcionarioId, dataHora);

                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(clienteId);
                when(jwtService.extractClaim(eq(validJwtToken), eq("role")))
                                .thenReturn("CLIENTE");
                when(agendamentoService.criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class)))
                                .thenThrow(new IllegalArgumentException(
                                                "Horário não disponível para este funcionário"));

                // Act & Assert
                mockMvc.perform(post("/api/agendamentos")
                                .with(csrf())
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", is("Horário não disponível para este funcionário")));

                verify(agendamentoService, times(1))
                                .criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar 422 quando funcionário não executa o serviço")
        @WithMockUser
        void deveRetornar422QuandoFuncionarioNaoExecutaServico() throws Exception {
                // Arrange
                AgendamentoRequestDto request = new AgendamentoRequestDto(servicoId, funcionarioId, dataHora);

                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(clienteId);
                when(jwtService.extractClaim(eq(validJwtToken), eq("role")))
                                .thenReturn("CLIENTE");
                when(agendamentoService.criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class)))
                                .thenThrow(new IllegalArgumentException("Funcionário não executa este serviço"));

                // Act & Assert
                mockMvc.perform(post("/api/agendamentos")
                                .with(csrf())
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message", is("Funcionário não executa este serviço")));

                verify(agendamentoService, times(1))
                                .criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar 422 quando data/hora é no passado")
        @WithMockUser
        void deveRetornar422QuandoDataEhNoPasado() throws Exception {
                // Arrange
                LocalDateTime dataPasada = LocalDateTime.now().minusDays(1);
                AgendamentoRequestDto request = new AgendamentoRequestDto(servicoId, funcionarioId, dataPasada);

                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(clienteId);
                when(jwtService.extractClaim(eq(validJwtToken), eq("role")))
                                .thenReturn("CLIENTE");
                when(agendamentoService.criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class)))
                                .thenThrow(new IllegalArgumentException(
                                                "Data/hora do agendamento não pode ser no passado"));

                // Act & Assert
                mockMvc.perform(post("/api/agendamentos")
                                .with(csrf())
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message",
                                                is("Data/hora do agendamento não pode ser no passado")));

                verify(agendamentoService, times(1))
                                .criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class));
        }

        @Test
        @DisplayName("Deve criar agendamento sem observações (campo opcional)")
        @WithMockUser
        void deveCriarAgendamentoSemObservacoes() throws Exception {
                // Arrange
                AgendamentoRequestDto request = new AgendamentoRequestDto(servicoId, funcionarioId, dataHora);

                AgendamentoResponseDto resposta = new AgendamentoResponseDto(
                                124L,
                                clienteId,
                                1L,
                                servicoId,
                                funcionarioId,
                                dataHora,
                                StatusAgendamento.PENDENTE,
                                "",
                                LocalDateTime.now(),
                                LocalDateTime.now(),
                                false);

                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(clienteId);
                when(jwtService.extractClaim(eq(validJwtToken), eq("role")))
                                .thenReturn("CLIENTE");
                when(agendamentoService.criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class)))
                                .thenReturn(resposta);

                // Act & Assert
                mockMvc.perform(post("/api/agendamentos")
                                .with(csrf())
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id", is(124)))
                                .andExpect(jsonPath("$.status", is("PENDENTE")));

                verify(agendamentoService, times(1))
                                .criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class));
        }

        @Test
        @DisplayName("Deve retornar 500 em caso de erro interno do servidor")
        @WithMockUser
        void deveRetornar500EmCasoDeErroInterno() throws Exception {
                // Arrange
                AgendamentoRequestDto request = new AgendamentoRequestDto(servicoId, funcionarioId, dataHora);

                when(jwtService.extractClaim(eq(validJwtToken), eq("userId")))
                                .thenReturn(clienteId);
                when(jwtService.extractClaim(eq(validJwtToken), eq("role")))
                                .thenReturn("CLIENTE");
                when(agendamentoService.criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class)))
                                .thenThrow(new RuntimeException("Erro inesperado no banco de dados"));

                // Act & Assert
                mockMvc.perform(post("/api/agendamentos")
                                .with(csrf())
                                .header("Authorization", "Bearer " + validJwtToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.message",
                                                is("Ocorreu um erro inesperado: Erro inesperado no banco de dados")));

                verify(agendamentoService, times(1))
                                .criarAgendamento(eq(clienteId), any(AgendamentoRequestDto.class));
        }
}
