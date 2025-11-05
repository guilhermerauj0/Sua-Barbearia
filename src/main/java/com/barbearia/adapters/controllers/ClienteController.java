package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AgendamentoBriefDto;
import com.barbearia.application.dto.ClienteProfileDto;
import com.barbearia.application.dto.ClienteUpdateDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.AgendamentoService;
import com.barbearia.application.services.ClienteService;
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
    private final ClienteService clienteService;
    
    public ClienteController(AgendamentoService agendamentoService, 
                            JwtService jwtService,
                            ClienteService clienteService) {
        this.agendamentoService = agendamentoService;
        this.jwtService = jwtService;
        this.clienteService = clienteService;
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
     * Busca o perfil completo do cliente autenticado.
     * 
     * Retorna 200 (OK) com os dados do cliente.
     * Retorna 401 (Unauthorized) se o token JWT é inválido.
     * 
     * Segurança:
     * - Requer autenticação JWT
     * - Cliente só acessa seu próprio perfil
     * - ID do cliente é extraído do token JWT
     * 
     * @param request Requisição HTTP contendo o token JWT
     * @return Dados completos do cliente
     */
    @io.swagger.v3.oas.annotations.Hidden
    @GetMapping("/meu-perfil")
    public ResponseEntity<?> buscarMeuPerfil(HttpServletRequest request) {
        try {
            // Extrai o ID do cliente do token JWT
            Long clienteId = extrairClienteIdDoToken(request);
            
            if (clienteId == null) {
                return ResponseEntity.status(401).body("Token JWT inválido ou clienteId não encontrado");
            }
            
            ClienteProfileDto perfil = clienteService.buscarMeuPerfil(clienteId);
            
            return ResponseEntity.ok(perfil);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao buscar perfil: " + e.getMessage());
        }
    }
    
    /**
     * Atualiza os dados do cliente autenticado.
     * 
     * Permite atualização parcial:
     * - Apenas campos fornecidos são atualizados
     * - Campos omitidos mantêm seus valores
     * 
     * Campos que podem ser atualizados:
     * - nome
     * - email (com validação de unicidade)
     * - telefone
     * 
     * Campos que NÃO podem ser alterados:
     * - role (segurança)
     * - id (identificador imutável)
     * - datas de criação/atualização (gerenciadas pelo sistema)
     * 
     * Retorna 200 (OK) com dados atualizados.
     * Retorna 400 (Bad Request) se dados inválidos.
     * Retorna 401 (Unauthorized) se token JWT inválido.
     * 
     * Validações:
     * - Email deve ser válido e não pode estar em uso
     * - Telefone deve ter 10 ou 11 dígitos
     * - Nome não pode ser vazio
     * 
     * @param request Requisição HTTP contendo o token JWT
     * @param updateDto DTO com dados a atualizar
     * @return Dados atualizados do cliente
     */
    @PutMapping("/meu-perfil")
    public ResponseEntity<?> atualizarMeuPerfil(
            HttpServletRequest request,
            @RequestBody ClienteUpdateDto updateDto) {
        try {
            // Extrai o ID do cliente do token JWT
            Long clienteId = extrairClienteIdDoToken(request);
            
            if (clienteId == null) {
                return ResponseEntity.status(401).body("Token JWT inválido ou clienteId não encontrado");
            }
            
            ClienteProfileDto perfilAtualizado = clienteService.atualizarMeuPerfil(clienteId, updateDto);
            
            return ResponseEntity.ok(perfilAtualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao atualizar perfil: " + e.getMessage());
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
