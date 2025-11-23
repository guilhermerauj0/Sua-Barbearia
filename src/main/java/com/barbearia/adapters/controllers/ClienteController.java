package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AgendamentoBriefDto;
import com.barbearia.application.dto.ClienteProfileDto;
import com.barbearia.application.dto.ClienteUpdateDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.AgendamentoService;
import com.barbearia.application.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Perfil e Histórico do Cliente.
 */
@Tag(name = "Clientes", description = "Perfil do cliente e histórico de agendamentos")
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

    @Operation(summary = "Histórico de agendamentos", description = "Lista agendamentos passados do cliente autenticado", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @GetMapping("/meus-agendamentos/historico")
    public ResponseEntity<?> listarHistorico(HttpServletRequest request) {
        try {
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

    @Operation(summary = "Listar agendamentos recentes", description = "Retorna agendamentos recém-criados (futuros) ou recém-concluídos dos últimos 30 dias", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada"),
            @ApiResponse(responseCode = "400", description = "Tipo inválido"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @GetMapping("/meus-agendamentos/recentes")
    public ResponseEntity<?> listarAgendamentosRecentes(
            @io.swagger.v3.oas.annotations.Parameter(description = "Tipo: 'futuros' (recém-criados) ou 'concluidos_recentes' (recém-concluídos). Sem parâmetro retorna ambos.") @RequestParam(required = false) String tipo,
            HttpServletRequest request) {
        try {
            Long clienteId = extrairClienteIdDoToken(request);

            if (clienteId == null) {
                return ResponseEntity.status(401).body("Token JWT inválido ou clienteId não encontrado");
            }

            List<AgendamentoBriefDto> agendamentos;

            if ("futuros".equals(tipo)) {
                // Agendamentos futuros criados nos últimos 30 dias
                agendamentos = agendamentoService.listarAgendamentosRecentesFuturos(clienteId, 30);
            } else if ("concluidos_recentes".equals(tipo)) {
                // Agendamentos concluídos nos últimos 30 dias
                agendamentos = agendamentoService.listarAgendamentosConcluídosRecentes(clienteId, 30);
            } else if (tipo == null || tipo.isBlank()) {
                // Retorna ambos
                List<AgendamentoBriefDto> futuros = agendamentoService.listarAgendamentosRecentesFuturos(clienteId, 30);
                List<AgendamentoBriefDto> concluidos = agendamentoService
                        .listarAgendamentosConcluídosRecentes(clienteId, 30);

                agendamentos = new java.util.ArrayList<>();
                agendamentos.addAll(futuros);
                agendamentos.addAll(concluidos);
            } else {
                return ResponseEntity.badRequest()
                        .body("{\"erro\": \"Tipo deve ser 'futuros' ou 'concluidos_recentes'\"}");
            }

            return ResponseEntity.ok(agendamentos);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao buscar agendamentos recentes: " + e.getMessage());
        }
    }

    @io.swagger.v3.oas.annotations.Hidden
    @GetMapping("/meu-perfil")
    public ResponseEntity<?> buscarMeuPerfil(HttpServletRequest request) {
        try {
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

    @Operation(summary = "Atualizar perfil", description = "Atualiza dados do cliente (nome, email, telefone)", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado"),
            @ApiResponse(responseCode = "400", description = "Email já em uso ou dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PutMapping("/meu-perfil")
    public ResponseEntity<?> atualizarMeuPerfil(
            HttpServletRequest request,
            @RequestBody ClienteUpdateDto updateDto) {
        try {
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

    private Long extrairClienteIdDoToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);

        Object userIdClaim = jwtService.extractClaim(token, "userId");

        if (userIdClaim instanceof Number) {
            return ((Number) userIdClaim).longValue();
        } else if (userIdClaim instanceof String) {
            return Long.parseLong((String) userIdClaim);
        }

        return null;
    }
}
