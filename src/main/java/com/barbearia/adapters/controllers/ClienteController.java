package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AgendamentoBriefDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.AgendamentoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações de clientes autenticados.
 * 
 * Endpoints:
 * - GET /api/clientes/meus-agendamentos/historico - Lista histórico de agendamentos
 * - GET /api/clientes/meus-agendamentos/futuros - Lista agendamentos futuros (futuro)
 * 
 * Todos os endpoints exigem autenticação JWT com role CLIENTE.
 * O ID do cliente é extraído automaticamente do token JWT.
 * 
 * @author Sua Barbearia Team
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {
    
    private final AgendamentoService agendamentoService;
    private final JwtService jwtService;
    
    public ClienteController(AgendamentoService agendamentoService, 
                            JwtService jwtService) {
        this.agendamentoService = agendamentoService;
        this.jwtService = jwtService;
    }
    
    /**
     * Lista o histórico de agendamentos do cliente autenticado.
     * 
     * Retorna apenas agendamentos passados (dataHora < now)
     * Ordenados por data decrescente (mais recente primeiro)
     * 
     * Segurança:
     * - Requer autenticação JWT
     * - Cliente só acessa seus próprios agendamentos
     * - ID do cliente é extraído do token JWT
     * 
     * @param request Requisição HTTP contendo o token JWT
     * @return Lista de agendamentos passados
     */
    @GetMapping("/meus-agendamentos/historico")
    public ResponseEntity<?> listarHistorico(HttpServletRequest request) {
        try {
            // Extrai o ID do cliente do token JWT
            Long clienteId = extrairClienteIdDoToken(request);
            
            if (clienteId == null) {
                return ResponseEntity.status(401).body("Token JWT inválido ou clienteId não encontrado");
            }
            
            List<AgendamentoBriefDto> historico = agendamentoService.listarHistoricoCliente(clienteId);
            
            return ResponseEntity.ok(historico);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao buscar histórico: " + e.getMessage());
        }
    }
    
    /**
     * Extrai o ID do cliente do token JWT presente no header Authorization.
     * 
     * @param request Requisição HTTP
     * @return ID do cliente ou null se não encontrado
     */
    private Long extrairClienteIdDoToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        
        String token = authHeader.substring(7);
        
        // Extrai o claim "userId" do token
        Object userIdClaim = jwtService.extractClaim(token, "userId");
        
        if (userIdClaim instanceof Number) {
            return ((Number) userIdClaim).longValue();
        } else if (userIdClaim instanceof String) {
            return Long.parseLong((String) userIdClaim);
        }
        
        return null;
    }
}
