package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.*;
import com.barbearia.application.services.*;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Dashboard Self-Service para Profissionais (Sem JWT).
 * 
 * Autenticação: Via accessToken UUID no path.
 * A barbearia gera link único para cada profissional.
 */
@Tag(name = "Dashboard Profissional", description = "Dashboard self-service via link único (sem senha)")
@RestController
@RequestMapping("/api/profissional")
@CrossOrigin(origins = "*")
public class ProfissionalDashboardController {

    private final ProfissionalLinkService profissionalLinkService;
    private final HorarioBloqueioService horarioBloqueioService;
    // AgendamentoService removed - não está sendo usado ainda

    public ProfissionalDashboardController(ProfissionalLinkService profissionalLinkService,
            HorarioBloqueioService horarioBloqueioService) {
        this.profissionalLinkService = profissionalLinkService;
        this.horarioBloqueioService = horarioBloqueioService;
    }

    /**
     * Dashboard do profissional.
     */
    @Operation(summary = "Dashboard do profissional", description = "Retorna informações básicas do profissional via accessToken (gerado pela barbearia)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dashboard carregado"),
            @ApiResponse(responseCode = "401", description = "Token inválido, expirado ou desativado")
    })
    @GetMapping("/{accessToken}/dashboard")
    public ResponseEntity<?> obterDashboard(
            @Parameter(description = "Token de acesso UUID gerado pela barbearia", example = "abc123def456") @PathVariable String accessToken) {
        try {
            JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

            // Retorna dados básicos
            return ResponseEntity.ok(new DashboardResponseDto(
                    funcionario.getId(),
                    funcionario.getNome(),
                    funcionario.getEmail(),
                    funcionario.getPerfilType().name(),
                    "Link válido até: "
                            + (funcionario.getTokenExpiraEm() != null ? funcionario.getTokenExpiraEm().toString()
                                    : "Sem expiração")));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Lista agendamentos do profissional com filtros.
     */
    @GetMapping("/{accessToken}/agendamentos")
    public ResponseEntity<?> listarAgendamentos(
            @PathVariable String accessToken,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {
        try {
            JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

            // Delega para AgendamentoService (assumindo que existe método
            // listByFuncionarioId)
            // Por simplicidade, retornando mensagem aqui
            return ResponseEntity.ok("Lista de agendamentos do profissional " + funcionario.getId());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Criar bloqueio de horário.
     */
    @Operation(summary = "Criar bloqueio", description = "Profissional bloqueia horário específico (almoço, reunião, etc)", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Bloqueio Almoço", value = """
            {
              "data": "2025-11-25",
              "horarioInicio": "12:00",
              "horarioFim": "13:00",
              "motivo": "Almoço"
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bloqueio criado"),
            @ApiResponse(responseCode = "400", description = "Sobreposição com bloqueio existente"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/{accessToken}/bloqueios")
    public ResponseEntity<?> criarBloqueio(
            @Parameter(description = "Token de acesso") @PathVariable String accessToken,
            @Valid @RequestBody HorarioBloqueadoRequestDto requestDto) {
        try {
            JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

            HorarioBloqueadoResponseDto bloqueio = horarioBloqueioService.criarBloqueio(
                    funcionario.getId(),
                    requestDto,
                    "PROFISSIONAL");

            return ResponseEntity.status(HttpStatus.CREATED).body(bloqueio);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    /**
     * Cria múltiplos bloqueios em lote.
     */
    @Operation(summary = "Criar bloqueios em lote", description = "Profissional cria múltiplos bloqueios de uma vez (ex: todos os almoços da semana)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bloqueios criados"),
            @ApiResponse(responseCode = "400", description = "Sobreposição detectada"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/{accessToken}/bloqueios/lote")
    public ResponseEntity<?> criarBloqueiosEmLote(
            @PathVariable String accessToken,
            @Valid @RequestBody HorarioBloqueadoLoteRequestDto requestDto) {
        try {
            JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

            List<HorarioBloqueadoResponseDto> bloqueios = horarioBloqueioService.criarBloqueiosEmLote(
                    funcionario.getId(),
                    requestDto,
                    "PROFISSIONAL");

            return ResponseEntity.status(HttpStatus.CREATED).body(bloqueios);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    /**
     * Lista bloqueios do profisional.
     */
    @Operation(summary = "Listar bloqueios", description = "Profissional lista todos seus bloqueios ativos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de bloqueios"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @GetMapping("/{accessToken}/bloqueios")
    public ResponseEntity<?> listarBloqueios(
            @PathVariable String accessToken,
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim) {
        try {
            JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

            LocalDate inicio = dataInicio != null ? LocalDate.parse(dataInicio) : LocalDate.now();
            LocalDate fim = dataFim != null ? LocalDate.parse(dataFim) : LocalDate.now().plusMonths(1);

            List<HorarioBloqueadoResponseDto> bloqueios = horarioBloqueioService.listarBloqueiosPorPeriodo(
                    funcionario.getId(),
                    inicio,
                    fim);

            return ResponseEntity.ok(bloqueios);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    /**
     * Remove bloqueio (apenas os criados pelo profissional).
     */
    @Operation(summary = "Remover bloqueio", description = "Profissional remove bloqueio específico. Apenas bloqueios criados por ele podem ser removidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bloqueio removido"),
            @ApiResponse(responseCode = "404", description = "Bloqueio não encontrado"),
            @ApiResponse(responseCode = "403", description = "Bloqueio criado pela barbearia (não pode remover)"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @DeleteMapping("/{accessToken}/bloqueios/{bloqueioId}")
    public ResponseEntity<?> removerBloqueio(
            @PathVariable String accessToken,
            @PathVariable Long bloqueioId) {
        try {
            JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

            horarioBloqueioService.removerBloqueio(bloqueioId, funcionario.getId(), "PROFISSIONAL");

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Token")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DTO interno para Dashboard
    static class DashboardResponseDto {
        public final Long funcionarioId;
        public final String nome;
        public final String email;
        public final String perfil;
        public final String linkInfo;

        public DashboardResponseDto(Long id, String nome, String email, String perfil, String linkInfo) {
            this.funcionarioId = id;
            this.nome = nome;
            this.email = email;
            this.perfil = perfil;
            this.linkInfo = linkInfo;
        }
    }
}
