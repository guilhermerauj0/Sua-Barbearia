package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AgendamentoBriefDto;
import com.barbearia.application.dto.ClienteProfileDto;
import com.barbearia.application.dto.ClienteUpdateDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.AgendamentoService;
import com.barbearia.application.services.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(summary = "Histórico de agendamentos", description = "Retorna todos os agendamentos do cliente (passados e futuros)", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico retornado", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AgendamentoBriefDto.class)))),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @GetMapping("/meus-agendamentos/historico")
    public ResponseEntity<?> listarHistorico(HttpServletRequest request) {
        Long clienteId = extrairClienteIdDoToken(request);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(new com.barbearia.application.dto.ApiErrorDto(
                    java.time.LocalDateTime.now(),
                    401,
                    "Unauthorized",
                    "Token inválido ou ausente",
                    request.getRequestURI()));
        }
        List<AgendamentoBriefDto> historico = agendamentoService.listarHistoricoCliente(clienteId);
        return ResponseEntity.ok(historico);
    }

    @Operation(summary = "Agendamentos recentes", description = "Retorna agendamentos recentes (futuros ou recém-concluídos)", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AgendamentoBriefDto.class)))),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @GetMapping("/meus-agendamentos/recentes")
    public ResponseEntity<?> listarAgendamentosRecentes(
            @io.swagger.v3.oas.annotations.Parameter(description = "Tipo: 'futuros' (recém-criados) ou 'concluidos_recentes' (recém-concluídos). Sem parâmetro retorna ambos.") @RequestParam(required = false) String tipo,
            HttpServletRequest request) {
        Long clienteId = extrairClienteIdDoToken(request);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(new com.barbearia.application.dto.ApiErrorDto(
                    java.time.LocalDateTime.now(),
                    401,
                    "Unauthorized",
                    "Token inválido ou ausente",
                    request.getRequestURI()));
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
            throw new IllegalArgumentException("Tipo deve ser 'futuros' ou 'concluidos_recentes'");
        }

        return ResponseEntity.ok(agendamentos);
    }

    @Operation(summary = "Meu perfil", description = "Retorna dados do perfil do cliente autenticado", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil retornado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteProfileDto.class))),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @GetMapping("/meu-perfil")
    public ResponseEntity<?> buscarMeuPerfil(HttpServletRequest request) {
        Long clienteId = extrairClienteIdDoToken(request);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(new com.barbearia.application.dto.ApiErrorDto(
                    java.time.LocalDateTime.now(),
                    401,
                    "Unauthorized",
                    "Token inválido ou ausente",
                    request.getRequestURI()));
        }
        ClienteProfileDto perfil = clienteService.buscarMeuPerfil(clienteId);
        return ResponseEntity.ok(perfil);
    }

    @Operation(summary = "Atualizar perfil", description = "Atualiza dados do cliente (nome, email, telefone)", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil atualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteProfileDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(name = "Validação", value = """
                    {
                      "timestamp": "2025-11-25T17:00:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Email inválido",
                      "path": "/api/clientes/meu-perfil"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @PutMapping("/meu-perfil")
    public ResponseEntity<?> atualizarMeuPerfil(
            HttpServletRequest request,
            @RequestBody ClienteUpdateDto updateDto) {
        Long clienteId = extrairClienteIdDoToken(request);
        if (clienteId == null) {
            return ResponseEntity.status(401).body(new com.barbearia.application.dto.ApiErrorDto(
                    java.time.LocalDateTime.now(),
                    401,
                    "Unauthorized",
                    "Token inválido ou ausente",
                    request.getRequestURI()));
        }
        ClienteProfileDto perfil = clienteService.atualizarMeuPerfil(clienteId, updateDto);
        return ResponseEntity.ok(perfil);
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
