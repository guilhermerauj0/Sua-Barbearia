package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.BarbeariaRequestDto;
import com.barbearia.application.dto.BarbeariaResponseDto;
import com.barbearia.application.dto.ClienteRequestDto;
import com.barbearia.application.dto.ClienteResponseDto;
import com.barbearia.application.dto.LoginRequestDto;
import com.barbearia.application.dto.LoginResponseDto;
import com.barbearia.application.services.AuthService;
import com.barbearia.application.services.BarbeariaService;
import com.barbearia.application.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Autenticação e Registro de Clientes e Barbearias.
 */
@Tag(name = "Autenticação", description = "Registro e login de clientes e barbearias")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final ClienteService clienteService;
    private final AuthService authService;
    private final BarbeariaService barbeariaService;

    public AuthController(ClienteService clienteService, AuthService authService,
            BarbeariaService barbeariaService) {
        this.clienteService = clienteService;
        this.authService = authService;
        this.barbeariaService = barbeariaService;
    }

    /**
     * Registra novo cliente no sistema.
     */
    @Operation(summary = "Registrar cliente", description = "Cadastra novo cliente. Email deve ser único. Use os MESMOS dados no login para testar.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Cliente Teste", value = """
            {
              "nome": "Pedro Santos",
              "email": "pedro.santos@teste.com",
              "senha": "Teste@123",
              "confirmarSenha": "Teste@123",
              "telefone": "11987654321"
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cliente registrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Email já cadastrado ou dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Email já cadastrado",
                      "path": "/api/auth/cliente/registrar"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @PostMapping("/cliente/registrar")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody ClienteRequestDto requestDto) {
        ClienteResponseDto clienteCriado = clienteService.registrarCliente(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
    }

    /**
     * Login de cliente.
     */
    @Operation(summary = "Login cliente", description = "Autentica cliente e retorna token JWT. Use o MESMO email/senha do registro.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Login Teste", value = """
            {
              "email": "pedro.santos@teste.com",
              "senha": "Teste@123"
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:30:00",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Credenciais inválidas",
                      "path": "/api/auth/cliente/login"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @PostMapping("/cliente/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Registra nova barbearia no sistema.
     */
    @Operation(summary = "Registrar barbearia", description = "Cadastra nova barbearia. Aceita CPF ou CNPJ. Use os MESMOS dados no login.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "Barbearia com CPF", value = """
                    {
                      "nome": "Ana Costa",
                      "email": "ana.costa@teste.com",
                      "senha": "Barbearia@123",
                      "confirmarSenha": "Barbearia@123",
                      "telefone": "11976543210",
                      "nomeFantasia": "Barbearia Elegance",
                      "tipoDocumento": "CPF",
                      "documento": "12345678909",
                      "endereco": "Rua das Flores, 123 - São Paulo/SP"
                    }
                    """),
            @ExampleObject(name = "Barbearia com CNPJ", value = """
                    {
                      "nome": "Carlos Oliveira",
                      "email": "carlos.oliveira@teste.com",
                      "senha": "Empresa@456",
                      "confirmarSenha": "Empresa@456",
                      "telefone": "21998765432",
                      "nomeFantasia": "Barbearia Premium",
                      "tipoDocumento": "CNPJ",
                      "documento": "11222333000181",
                      "endereco": "Av. Principal, 500 - Rio de Janeiro/RJ"
                    }
                    """)
    })))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Barbearia registrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BarbeariaResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "CPF/CNPJ inválido ou email duplicado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "CPF inválido",
                      "path": "/api/auth/barbearia/registrar"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @PostMapping("/barbearia/registrar")
    public ResponseEntity<?> registrarBarbearia(@Valid @RequestBody BarbeariaRequestDto requestDto) {
        BarbeariaResponseDto barbeariaCriada = barbeariaService.registrarBarbearia(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(barbeariaCriada);
    }

    /**
     * Login de barbearia.
     */
    @Operation(summary = "Login barbearia", description = "Autentica barbearia e retorna token JWT. Use o MESMO email/senha do registro.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Login Teste", value = """
            {
              "email": "carlos.oliveira@teste.com",
              "senha": "Empresa@456"
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:30:00",
                      "status": 401,
                      "error": "Unauthorized",
                      "message": "Credenciais inválidas",
                      "path": "/api/auth/barbearia/login"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @PostMapping("/barbearia/login")
    public ResponseEntity<?> loginBarbearia(@Valid @RequestBody LoginRequestDto loginRequest) {
        LoginResponseDto loginResponse = authService.loginBarbearia(loginRequest);
        return ResponseEntity.ok(loginResponse);
    }
}
