package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.dto.ServicoRequestDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.BarbeariaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações de serviços por barbearias autenticadas.
 * 
 * Endpoints:
 * - POST /api/barbearias/servicos - Criar novo serviço
 * 
 * Todos os endpoints requerem autenticação JWT com role BARBEARIA.
 * O ID da barbearia é extraído automaticamente do token JWT.
 * 
 * @author Sua Barbearia Team
 */
@RestController
@RequestMapping("/api/barbearias/servicos")
@CrossOrigin(origins = "*")
public class ServicoController {
    
    private final BarbeariaService barbeariaService;
    private final JwtService jwtService;
    
    public ServicoController(BarbeariaService barbeariaService, JwtService jwtService) {
        this.barbeariaService = barbeariaService;
        this.jwtService = jwtService;
    }
    
    /**
     * Cria um novo serviço para a barbearia autenticada.
     * 
     * Segurança:
     * - Requer autenticação JWT (Bearer token)
     * - Apenas role BARBEARIA pode acessar
     * - BarbeariaId é extraído do token (não pode ser alterado)
     * 
     * Validações:
     * - Nome é obrigatório
     * - Preço deve ser maior que zero
     * - Duração deve ser maior que zero (em minutos)
     * - Barbearia deve existir e estar ativa
     * 
     * Retorna:
     * - 201 (Created) com dados do serviço criado
     * - 400 (Bad Request) se dados inválidos
     * - 401 (Unauthorized) se token inválido/ausente
     * - 403 (Forbidden) se não for role BARBEARIA
     * - 500 (Internal Server Error) em caso de erro
     * 
     * @param requestDto Dados do serviço a ser criado
     * @param request Requisição HTTP (para extrair token JWT)
     * @return DTO com dados do serviço criado
     */
    @PostMapping
    public ResponseEntity<?> criarServico(
            @RequestBody ServicoRequestDto requestDto,
            HttpServletRequest request) {
        try {
            // Extrai token do cabeçalho Authorization
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT não fornecido ou inválido");
            }
            
            // Remove prefixo "Bearer " do token
            token = token.substring(7);
            
            // Extrai ID da barbearia do token
            Object userIdObj = jwtService.extractClaim(token, "userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT inválido: userId não encontrado");
            }
            
            Long barbeariaId = ((Number) userIdObj).longValue();
            
            // Cria o serviço
            ServicoDto servicoCriado = barbeariaService.criarServico(barbeariaId, requestDto);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(servicoCriado);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao criar serviço: " + e.getMessage());
        }
    }
}
