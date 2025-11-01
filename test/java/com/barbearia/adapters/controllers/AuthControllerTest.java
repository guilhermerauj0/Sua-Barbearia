package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ClienteRequestDto;
import com.barbearia.application.dto.ClienteResponseDto;
import com.barbearia.application.dto.LoginRequestDto;
import com.barbearia.application.dto.LoginResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.AuthService;
import com.barbearia.application.services.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para AuthController usando MockMvc.
 * Testa o endpoint HTTP sem subir o servidor completo.
 */
@SuppressWarnings("null")
@WebMvcTest(AuthController.class)
@DisplayName("AuthController - Testes de Integração")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    private ClienteRequestDto clienteRequestDto;
    private ClienteResponseDto clienteResponseDto;
    private LoginRequestDto loginRequestDto;
    private LoginResponseDto loginResponseDto;

    @BeforeEach
    void setUp() {
        clienteRequestDto = new ClienteRequestDto(
                "João Silva",
                "joao@email.com",
                "Senha@123",
                "Senha@123",
                "(11) 98765-4321"
        );

        clienteResponseDto = new ClienteResponseDto(
                1L,
                "João Silva",
                "joao@email.com",
                "11987654321",
                "CLIENTE",
                true,
                LocalDateTime.now()
        );

        loginRequestDto = new LoginRequestDto("joao@email.com", "Senha@123");

        loginResponseDto = new LoginResponseDto(
                "fake-jwt-token",
                1L,
                "João Silva",
                "joao@email.com",
                "CLIENTE",
                3600000L
        );
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/registrar - Deve registrar cliente com sucesso")
    void deveRegistrarClienteComSucesso() throws Exception {
        // Arrange
        when(clienteService.registrarCliente(any(ClienteRequestDto.class)))
                .thenReturn(clienteResponseDto);

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.telefone").value("11987654321"))
                .andExpect(jsonPath("$.ativo").value(true))
                .andExpect(jsonPath("$.senha").doesNotExist()); // Não deve expor senha
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/registrar - Deve retornar 400 quando nome vazio")
    void deveRetornar400QuandoNomeVazio() throws Exception {
        // Arrange
        ClienteRequestDto dtoInvalido = new ClienteRequestDto(
                "",
                "joao@email.com",
                "Senha@123",
                "Senha@123",
                "(11) 98765-4321"
        );

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/registrar - Deve retornar 400 quando email inválido")
    void deveRetornar400QuandoEmailInvalido() throws Exception {
        // Arrange
        ClienteRequestDto dtoInvalido = new ClienteRequestDto(
                "João Silva",
                "email-invalido",
                "Senha@123",
                "Senha@123",
                "(11) 98765-4321"
        );

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/registrar - Deve retornar 400 quando senha fraca")
    void deveRetornar400QuandoSenhaFraca() throws Exception {
        // Arrange
        ClienteRequestDto dtoInvalido = new ClienteRequestDto(
                "João Silva",
                "joao@email.com",
                "123",
                "123",
                "(11) 98765-4321"
        );

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/registrar - Deve retornar 400 quando senhas não conferem")
    void deveRetornar400QuandoSenhasNaoConferem() throws Exception {
        // Arrange
        ClienteRequestDto dtoInvalido = new ClienteRequestDto(
                "João Silva",
                "joao@email.com",
                "Senha@123",
                "Senha@456",
                "(11) 98765-4321"
        );

        when(clienteService.registrarCliente(any(ClienteRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Senha e confirmação de senha não coincidem"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Senha e confirmação de senha não coincidem"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/registrar - Deve retornar 400 quando email já cadastrado")
    void deveRetornar400QuandoEmailJaCadastrado() throws Exception {
        // Arrange
        when(clienteService.registrarCliente(any(ClienteRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Email já cadastrado no sistema"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email já cadastrado no sistema"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/registrar - Deve retornar 400 quando telefone inválido")
    void deveRetornar400QuandoTelefoneInvalido() throws Exception {
        // Arrange
        ClienteRequestDto dtoInvalido = new ClienteRequestDto(
                "João Silva",
                "joao@email.com",
                "Senha@123",
                "Senha@123",
                "123" // telefone muito curto
        );

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/registrar - Deve aceitar diferentes formatos de telefone")
    void deveAceitarDiferentesFormatosDeTelefone() throws Exception {
        // Arrange
        when(clienteService.registrarCliente(any(ClienteRequestDto.class)))
                .thenReturn(clienteResponseDto);

        ClienteRequestDto dtoComTelefoneFormatado = new ClienteRequestDto(
                "João Silva",
                "joao@email.com",
                "Senha@123",
                "Senha@123",
                "11987654321" // sem formatação
        );

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoComTelefoneFormatado)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/registrar - Deve retornar 500 quando erro inesperado")
    void deveRetornar500QuandoErroInesperado() throws Exception {
        // Arrange
        when(clienteService.registrarCliente(any(ClienteRequestDto.class)))
                .thenThrow(new RuntimeException("Erro inesperado no banco de dados"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteRequestDto)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro ao registrar cliente: Erro inesperado no banco de dados"));
    }

    // ==========================================
    // TESTES DE LOGIN
    // ==========================================

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/login - Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequestDto.class)))
                .thenReturn(loginResponseDto);

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.nome").value("João Silva"))
                .andExpect(jsonPath("$.email").value("joao@email.com"))
                .andExpect(jsonPath("$.role").value("CLIENTE"))
                .andExpect(jsonPath("$.expiresIn").value(3600000));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/login - Deve retornar 401 quando credenciais inválidas")
    void deveRetornar401QuandoCredenciaisInvalidas() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Credenciais inválidas"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciais inválidas"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/login - Deve retornar 400 quando email vazio")
    void deveRetornar400QuandoEmailVazioNoLogin() throws Exception {
        // Arrange
        LoginRequestDto dtoInvalido = new LoginRequestDto("", "Senha@123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/login - Deve retornar 400 quando senha vazia")
    void deveRetornar400QuandoSenhaVaziaNoLogin() throws Exception {
        // Arrange
        LoginRequestDto dtoInvalido = new LoginRequestDto("joao@email.com", "");

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/cliente/login - Deve retornar 400 quando email inválido")
    void deveRetornar400QuandoEmailInvalidoNoLogin() throws Exception {
        // Arrange
        LoginRequestDto dtoInvalido = new LoginRequestDto("email-invalido", "Senha@123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/cliente/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
