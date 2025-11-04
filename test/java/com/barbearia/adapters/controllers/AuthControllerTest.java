package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.BarbeariaRequestDto;
import com.barbearia.application.dto.BarbeariaResponseDto;
import com.barbearia.application.dto.ClienteRequestDto;
import com.barbearia.application.dto.ClienteResponseDto;
import com.barbearia.application.dto.LoginRequestDto;
import com.barbearia.application.dto.LoginResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.AuthService;
import com.barbearia.application.services.BarbeariaService;
import com.barbearia.application.services.ClienteService;
import com.barbearia.domain.enums.TipoDocumento;
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
    private BarbeariaService barbeariaService;

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

    // ==================== TESTES DE REGISTRO DE BARBEARIA ====================

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/registrar - Deve registrar barbearia com CPF com sucesso")
    void deveRegistrarBarbeariaComCPFComSucesso() throws Exception {
        // Arrange
        BarbeariaRequestDto requestDto = new BarbeariaRequestDto();
        requestDto.setNome("Maria Santos");
        requestDto.setEmail("maria.santos@email.com");
        requestDto.setSenha("SenhaForte@123");
        requestDto.setConfirmarSenha("SenhaForte@123");
        requestDto.setTelefone("(11) 98765-4321");
        requestDto.setNomeFantasia("Barbearia Elegance");
        requestDto.setTipoDocumento(TipoDocumento.CPF);
        requestDto.setDocumento("123.456.789-09");
        requestDto.setEndereco("Rua das Flores, 123 - São Paulo/SP");

        BarbeariaResponseDto responseDto = new BarbeariaResponseDto(
                1L,
                "Maria Santos",
                "maria.santos@email.com",
                "11987654321",
                "Barbearia Elegance",
                TipoDocumento.CPF,
                "12345678909",
                "Rua das Flores, 123 - São Paulo/SP",
                "BARBEARIA",
                true,
                LocalDateTime.now()
        );

        when(barbeariaService.registrarBarbearia(any(BarbeariaRequestDto.class)))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Maria Santos"))
                .andExpect(jsonPath("$.email").value("maria.santos@email.com"))
                .andExpect(jsonPath("$.nomeFantasia").value("Barbearia Elegance"))
                .andExpect(jsonPath("$.tipoDocumento").value("CPF"))
                .andExpect(jsonPath("$.documento").value("12345678909"))
                .andExpect(jsonPath("$.role").value("BARBEARIA"))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/registrar - Deve registrar barbearia com CNPJ com sucesso")
    void deveRegistrarBarbeariaComCNPJComSucesso() throws Exception {
        // Arrange
        BarbeariaRequestDto requestDto = new BarbeariaRequestDto();
        requestDto.setNome("Carlos Oliveira");
        requestDto.setEmail("carlos.oliveira@email.com");
        requestDto.setSenha("SenhaForte@456");
        requestDto.setConfirmarSenha("SenhaForte@456");
        requestDto.setTelefone("(21) 99876-5432");
        requestDto.setNomeFantasia("Barbearia Premium");
        requestDto.setTipoDocumento(TipoDocumento.CNPJ);
        requestDto.setDocumento("11.222.333/0001-81");
        requestDto.setEndereco("Av. Principal, 500 - Rio de Janeiro/RJ");

        BarbeariaResponseDto responseDto = new BarbeariaResponseDto(
                2L,
                "Carlos Oliveira",
                "carlos.oliveira@email.com",
                "21998765432",
                "Barbearia Premium",
                TipoDocumento.CNPJ,
                "11222333000181",
                "Av. Principal, 500 - Rio de Janeiro/RJ",
                "BARBEARIA",
                true,
                LocalDateTime.now()
        );

        when(barbeariaService.registrarBarbearia(any(BarbeariaRequestDto.class)))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nomeFantasia").value("Barbearia Premium"))
                .andExpect(jsonPath("$.tipoDocumento").value("CNPJ"))
                .andExpect(jsonPath("$.documento").value("11222333000181"));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/registrar - Deve retornar 400 quando senhas não conferem")
    void deveRetornar400QuandoSenhasNaoConferemBarbearia() throws Exception {
        // Arrange
        BarbeariaRequestDto requestDto = new BarbeariaRequestDto();
        requestDto.setNome("Maria Santos");
        requestDto.setEmail("maria.santos@email.com");
        requestDto.setSenha("SenhaForte@123");
        requestDto.setConfirmarSenha("SenhaDiferente@123");
        requestDto.setTelefone("(11) 98765-4321");
        requestDto.setNomeFantasia("Barbearia Elegance");
        requestDto.setTipoDocumento(TipoDocumento.CPF);
        requestDto.setDocumento("123.456.789-09");
        requestDto.setEndereco("Rua das Flores, 123 - São Paulo/SP");

        when(barbeariaService.registrarBarbearia(any(BarbeariaRequestDto.class)))
                .thenThrow(new IllegalArgumentException("As senhas não conferem"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/registrar - Deve retornar 400 quando CPF é inválido")
    void deveRetornar400QuandoCPFInvalido() throws Exception {
        // Arrange
        BarbeariaRequestDto requestDto = new BarbeariaRequestDto();
        requestDto.setNome("Maria Santos");
        requestDto.setEmail("maria.santos@email.com");
        requestDto.setSenha("SenhaForte@123");
        requestDto.setConfirmarSenha("SenhaForte@123");
        requestDto.setTelefone("(11) 98765-4321");
        requestDto.setNomeFantasia("Barbearia Elegance");
        requestDto.setTipoDocumento(TipoDocumento.CPF);
        requestDto.setDocumento("111.111.111-11");
        requestDto.setEndereco("Rua das Flores, 123 - São Paulo/SP");

        when(barbeariaService.registrarBarbearia(any(BarbeariaRequestDto.class)))
                .thenThrow(new IllegalArgumentException("CPF inválido. Verifique o número informado."));

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/registrar - Deve retornar 400 quando CNPJ é inválido")
    void deveRetornar400QuandoCNPJInvalido() throws Exception {
        // Arrange
        BarbeariaRequestDto requestDto = new BarbeariaRequestDto();
        requestDto.setNome("Carlos Oliveira");
        requestDto.setEmail("carlos.oliveira@email.com");
        requestDto.setSenha("SenhaForte@456");
        requestDto.setConfirmarSenha("SenhaForte@456");
        requestDto.setTelefone("(21) 99876-5432");
        requestDto.setNomeFantasia("Barbearia Premium");
        requestDto.setTipoDocumento(TipoDocumento.CNPJ);
        requestDto.setDocumento("00.000.000/0000-00");
        requestDto.setEndereco("Av. Principal, 500 - Rio de Janeiro/RJ");

        when(barbeariaService.registrarBarbearia(any(BarbeariaRequestDto.class)))
                .thenThrow(new IllegalArgumentException("CNPJ inválido. Verifique o número informado."));

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/registrar - Deve retornar 400 quando email já cadastrado")
    void deveRetornar400QuandoEmailJaCadastradoBarbearia() throws Exception {
        // Arrange
        BarbeariaRequestDto requestDto = new BarbeariaRequestDto();
        requestDto.setNome("Maria Santos");
        requestDto.setEmail("maria.santos@email.com");
        requestDto.setSenha("SenhaForte@123");
        requestDto.setConfirmarSenha("SenhaForte@123");
        requestDto.setTelefone("(11) 98765-4321");
        requestDto.setNomeFantasia("Barbearia Elegance");
        requestDto.setTipoDocumento(TipoDocumento.CPF);
        requestDto.setDocumento("123.456.789-09");
        requestDto.setEndereco("Rua das Flores, 123 - São Paulo/SP");

        when(barbeariaService.registrarBarbearia(any(BarbeariaRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Email já cadastrado no sistema"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/registrar - Deve retornar 400 quando documento já cadastrado")
    void deveRetornar400QuandoDocumentoJaCadastradoBarbearia() throws Exception {
        // Arrange
        BarbeariaRequestDto requestDto = new BarbeariaRequestDto();
        requestDto.setNome("Maria Santos");
        requestDto.setEmail("maria.santos@email.com");
        requestDto.setSenha("SenhaForte@123");
        requestDto.setConfirmarSenha("SenhaForte@123");
        requestDto.setTelefone("(11) 98765-4321");
        requestDto.setNomeFantasia("Barbearia Elegance");
        requestDto.setTipoDocumento(TipoDocumento.CPF);
        requestDto.setDocumento("123.456.789-09");
        requestDto.setEndereco("Rua das Flores, 123 - São Paulo/SP");

        when(barbeariaService.registrarBarbearia(any(BarbeariaRequestDto.class)))
                .thenThrow(new IllegalArgumentException("CPF já cadastrado no sistema"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/registrar")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    // ==================== TESTES DE LOGIN DE BARBEARIA ====================

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/login - Deve realizar login com sucesso")
    void deveRealizarLoginBarbeariaComSucesso() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto("maria@email.com", "SenhaForte@123");
        LoginResponseDto loginResponse = new LoginResponseDto(
                "fake-jwt-token-barbearia",
                5L,
                "Barbearia do Zé",
                "maria@email.com",
                "BARBEARIA",
                3600000L
        );

        when(authService.loginBarbearia(any(LoginRequestDto.class))).thenReturn(loginResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token-barbearia"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.userId").value(5))
                .andExpect(jsonPath("$.nome").value("Barbearia do Zé"))
                .andExpect(jsonPath("$.email").value("maria@email.com"))
                .andExpect(jsonPath("$.role").value("BARBEARIA"))
                .andExpect(jsonPath("$.expiresIn").value(3600000));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/login - Deve retornar 401 quando credenciais inválidas")
    void deveRetornar401QuandoCredenciaisInvalidasBarbearia() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto("maria@email.com", "SenhaErrada");

        when(authService.loginBarbearia(any(LoginRequestDto.class)))
                .thenThrow(new IllegalArgumentException("Credenciais inválidas"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/login - Deve retornar 400 quando email vazio")
    void deveRetornar400QuandoEmailVazioBarbearia() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto("", "SenhaForte@123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/auth/barbearia/login - Deve retornar 400 quando senha vazia")
    void deveRetornar400QuandoSenhaVaziaBarbearia() throws Exception {
        // Arrange
        LoginRequestDto loginRequest = new LoginRequestDto("maria@email.com", "");

        // Act & Assert
        mockMvc.perform(post("/api/auth/barbearia/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
