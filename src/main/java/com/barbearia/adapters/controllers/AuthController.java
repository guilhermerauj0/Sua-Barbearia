package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ClienteRequestDto;
import com.barbearia.application.dto.ClienteResponseDto;
import com.barbearia.application.dto.LoginRequestDto;
import com.barbearia.application.dto.LoginResponseDto;
import com.barbearia.application.services.AuthService;
import com.barbearia.application.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para autenticação e registro de clientes.
 * 
 * Endpoints:
 * - POST /api/auth/cliente/registro - Registrar novo cliente
 * - POST /api/auth/cliente/login - Login de cliente
 * 
 * @author Sua Barbearia Team
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final ClienteService clienteService;
    private final AuthService authService;
    
    public AuthController(ClienteService clienteService, AuthService authService) {
        this.clienteService = clienteService;
        this.authService = authService;
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
}
