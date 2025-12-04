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
    private final HorarioGestaoService horarioGestaoService;

    public ProfissionalDashboardController(ProfissionalLinkService profissionalLinkService,
            HorarioBloqueioService horarioBloqueioService,
            HorarioGestaoService horarioGestaoService) {
        this.profissionalLinkService = profissionalLinkService;
        this.horarioBloqueioService = horarioBloqueioService;
        this.horarioGestaoService = horarioGestaoService;
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
     * Lista horários de funcionamento do profissional.
     */
    @Operation(summary = "Listar meus horários", description = "Profissional visualiza seus horários de funcionamento (seg-dom)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horários"),
            @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    @GetMapping("/{accessToken}/horarios")
    public ResponseEntity<?> listarMeusHorarios(
            @Parameter(description = "Token de acesso UUID", example = "abc123def456") @PathVariable String accessToken) {
        try {
            JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

            List<HorarioFuncionamentoResponseDto> horarios = horarioGestaoService
                    .listarHorariosFuncionario(funcionario.getId());

            return ResponseEntity.ok(horarios);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * Profissional define seu horário de trabalho para um dia da semana.
     */
    @Operation(summary = "Definir meu horário", description = "Profissional define seu horário de trabalho para um dia da semana específico", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Horário Segunda-feira", value = """
            {
              "diaSemana": "SEGUNDA",
              "horaAbertura": "09:00",
              "horaFechamento": "18:00",
              "ativo": true
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horário salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(examples = @ExampleObject(value = "Horário de abertura deve ser antes do horário de fechamento"))),
            @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    @PostMapping("/{accessToken}/horarios")
    public ResponseEntity<?> definirMeuHorario(
            @Parameter(description = "Token de acesso") @PathVariable String accessToken,
            @Valid @RequestBody HorarioFuncionamentoRequestDto requestDto) {
        try {
            JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

            HorarioFuncionamentoResponseDto horario = horarioGestaoService.salvarHorarioFuncionario(
                    funcionario.getBarbeariaId(), funcionario.getId(), requestDto);

            return ResponseEntity.ok(horario);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Token")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar horário: " + e.getMessage());
        }
    }

    /**
     * Profissional define todos os horários da semana de uma vez.
     */
    @Operation(summary = "Definir horários da semana", description = "Profissional define todos os dias da semana de uma vez (seg-dom)", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Semana Completa", value = """
            {
              "horarios": [
                {"diaSemana": "SEGUNDA", "horaAbertura": "09:00", "horaFechamento": "18:00", "ativo": true},
                {"diaSemana": "TERCA", "horaAbertura": "09:00", "horaFechamento": "18:00", "ativo": true},
                {"diaSemana": "QUARTA", "horaAbertura": "09:00", "horaFechamento": "18:00", "ativo": true},
                {"diaSemana": "QUINTA", "horaAbertura": "09:00", "horaFechamento": "18:00", "ativo": true},
                {"diaSemana": "SEXTA", "horaAbertura": "09:00", "horaFechamento": "18:00", "ativo": true},
                {"diaSemana": "SABADO", "horaAbertura": "09:00", "horaFechamento": "13:00", "ativo": true},
                {"diaSemana": "DOMINGO", "horaAbertura": "00:00", "horaFechamento": "00:00", "ativo": false}
              ]
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horários salvos com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token inválido ou expirado")
    })
    @PostMapping("/{accessToken}/horarios/lote")
    public ResponseEntity<?> definirHorariosEmLote(
            @PathVariable String accessToken,
            @Valid @RequestBody HorarioLoteRequestDto requestDto) {
        try {
            JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

            List<HorarioFuncionamentoResponseDto> horarios = new java.util.ArrayList<>();

            for (HorarioFuncionamentoRequestDto horario : requestDto.getHorarios()) {
                HorarioFuncionamentoResponseDto salvo = horarioGestaoService.salvarHorarioFuncionario(
                        funcionario.getBarbeariaId(), funcionario.getId(), horario);
                horarios.add(salvo);
            }

            return ResponseEntity.ok(horarios);

        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Token")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar horários: " + e.getMessage());
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
