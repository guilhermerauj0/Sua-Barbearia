package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AgendamentoDetailDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.AgendamentoService;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import com.barbearia.domain.exceptions.AgendamentoNaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações de agendamentos.
 * 
 * Endpoints:
 * - GET /api/agendamentos/{id} - Busca detalhes de um agendamento específico
 * 
 * Todos os endpoints exigem autenticação JWT.
 * O ID do cliente/usuário é extraído automaticamente do token JWT.
 * 
 * @author Sua Barbearia Team
 */
@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {
    
    private final AgendamentoService agendamentoService;
    private final JwtService jwtService;
    
    public AgendamentoController(AgendamentoService agendamentoService,
                                JwtService jwtService) {
        this.agendamentoService = agendamentoService;
        this.jwtService = jwtService;
    }
    
    /**
     * Busca os detalhes completos de um agendamento específico.
     * 
     * Retorna 200 (OK) com os detalhes do agendamento.
     * Retorna 404 (Not Found) se o agendamento não existe.
     * Retorna 403 (Forbidden) se o usuário não tem permissão para acessar.
     * Retorna 401 (Unauthorized) se o token JWT é inválido.
     * 
     * Segurança:
     * - Requer autenticação JWT
     * - Cliente só acessa seus próprios agendamentos
     * - ID do cliente é extraído do token JWT
     * - Lógica de autorização centralizada no service
     * 
     * @param id ID do agendamento
     * @param request Requisição HTTP contendo o token JWT
     * @return Detalhes completos do agendamento
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarAgendamentoPorId(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            // Extrai o ID do usuário (cliente) do token JWT
            Long usuarioId = extrairUsuarioIdDoToken(request);
            
            if (usuarioId == null) {
                return ResponseEntity.status(401).body("Token JWT inválido ou userId não encontrado");
            }
            
            // Busca o agendamento com verificação de autorização
            // Por enquanto, o tipo de usuário é fixo como CLIENTE, mas pode ser estendido
            AgendamentoDetailDto agendamento = agendamentoService.buscarAgendamentoPorId(id, usuarioId, "CLIENTE");
            
            return ResponseEntity.ok(agendamento);
            
        } catch (AgendamentoNaoEncontradoException e) {
            // Retorna 404 (Not Found)
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (AcessoNegadoException e) {
            // Retorna 403 (Forbidden)
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Retorna 400 (Bad Request)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Retorna 500 (Internal Server Error)
            return ResponseEntity.internalServerError()
                    .body("Erro ao buscar agendamento: " + e.getMessage());
        }
    }
    
    /**
     * Extrai o ID do usuário (cliente) do token JWT presente no header Authorization.
     * 
     * @param request Requisição HTTP
     * @return ID do usuário ou null se não encontrado
     */
    private Long extrairUsuarioIdDoToken(HttpServletRequest request) {
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
            try {
                return Long.parseLong((String) userIdClaim);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
}
