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
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para autenticação e registro de clientes e barbearias.
 * 
 * Endpoints de Cliente:
 * - POST /api/auth/cliente/registrar - Registrar novo cliente
 * - POST /api/auth/cliente/login - Login de cliente
 * 
 * Endpoints de Barbearia:
 * - POST /api/auth/barbearia/registrar - Registrar nova barbearia
 * 
 * @author Sua Barbearia Team
 */
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
     * Endpoint para registrar um novo cliente.
     * 
     * @param requestDto Dados do cliente a ser registrado
     * @return ClienteResponseDto com dados do cliente criado (sem senha)
     */
    @PostMapping("/cliente/registrar")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody ClienteRequestDto requestDto) {
        try {
            ClienteResponseDto clienteCriado = clienteService.registrarCliente(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao registrar cliente: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para login de cliente.
     * 
     * @param loginRequest Credenciais do cliente (email e senha)
     * @return LoginResponseDto com token JWT e informações do usuário
     */
    @PostMapping("/cliente/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            LoginResponseDto loginResponse = authService.login(loginRequest);
            return ResponseEntity.ok(loginResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Credenciais inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao realizar login: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para registrar uma nova barbearia.
     * 
     * Validações realizadas:
     * - Senhas devem conferir
     * - Email deve ser único
     * - Documento (CPF ou CNPJ) deve ser válido
     * - Documento deve ser único por tipo
     * 
     * @param requestDto Dados da barbearia a ser registrada
     * @return BarbeariaResponseDto com dados da barbearia criada (sem senha)
     */
    @PostMapping("/barbearia/registrar")
    public ResponseEntity<?> registrarBarbearia(@Valid @RequestBody BarbeariaRequestDto requestDto) {
        try {
            BarbeariaResponseDto barbeariaCriada = barbeariaService.registrarBarbearia(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(barbeariaCriada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao registrar barbearia: " + e.getMessage());
        }
    }
}
