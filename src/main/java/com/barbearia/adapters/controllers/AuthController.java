package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ClienteRequestDto;
import com.barbearia.application.dto.ClienteResponseDto;
import com.barbearia.application.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para registro de clientes.
 * 
 * @author Sua Barbearia Team
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final ClienteService clienteService;
    
    public AuthController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
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
}
