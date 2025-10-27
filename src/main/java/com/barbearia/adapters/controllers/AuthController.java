package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ClienteRequestDto;
import com.barbearia.application.dto.ClienteResponseDto;
import com.barbearia.application.services.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para autenticação e registro de usuários.
 * 
 * Endpoints disponíveis:
 * - POST /api/auth/cliente/registrar: registra um novo cliente
 * 
 * Anotações:
 * - @RestController: marca como controller REST
 * - @RequestMapping: define o prefixo de rota
 * - @CrossOrigin: permite CORS (acesso de outras origens)
 * 
 * @author Sua Barbearia Team
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite acesso de qualquer origem (ajustar em produção)
public class AuthController {
    
    /**
     * Service com lógica de negócio de clientes
     */
    private final ClienteService clienteService;
    
    /**
     * Construtor com injeção de dependências
     * Spring injeta automaticamente quando há apenas um construtor
     * 
     * @param clienteService Service de clientes
     */
    public AuthController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    
    /**
     * Endpoint para registro de novo cliente
     * 
     * POST /api/auth/cliente/registrar
     * 
     * Body (JSON):
     * {
     *   "nome": "João Silva",
     *   "email": "joao@email.com",
     *   "senha": "Senha@123",
     *   "confirmarSenha": "Senha@123",
     *   "telefone": "(11) 98765-4321"
     * }
     * 
     * Sucesso (201):
     * {
     *   "id": 1,
     *   "nome": "João Silva",
     *   "email": "joao@email.com",
     *   "telefone": "11987654321",
     *   "role": "CLIENTE",
     *   "ativo": true,
     *   "dataCriacao": "2025-10-27T18:30:00"
     * }
     * 
     * Erro (400):
     * "Email já cadastrado no sistema"
     * 
     * @param requestDto Dados do cliente a ser registrado
     * @return ResponseEntity com cliente criado ou erro
     */
    @PostMapping("/cliente/registrar")
    public ResponseEntity<?> registrarCliente(@Valid @RequestBody ClienteRequestDto requestDto) {
        try {
            // Chama o service para registrar o cliente
            ClienteResponseDto clienteCriado = clienteService.registrarCliente(requestDto);
            
            // Retorna 201 (Created) com os dados do cliente (sem senha)
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteCriado);
            
        } catch (IllegalArgumentException e) {
            // Retorna 400 (Bad Request) com a mensagem de erro
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Retorna 500 (Internal Server Error) para erros inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao registrar cliente: " + e.getMessage());
        }
    }
    
    /**
     * Endpoint para verificar se um email já está cadastrado
     * 
     * GET /api/auth/cliente/email-disponivel?email=teste@email.com
     * 
     * @param email Email a ser verificado
     * @return ResponseEntity com true se email está disponível, false caso contrário
     */
    @GetMapping("/cliente/email-disponivel")
    public ResponseEntity<Boolean> verificarEmailDisponivel(@RequestParam String email) {
        try {
            boolean disponivel = !clienteService.emailJaCadastrado(email);
            return ResponseEntity.ok(disponivel);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
