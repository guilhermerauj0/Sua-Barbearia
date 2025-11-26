package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.*;
import com.barbearia.application.services.*;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

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
        private final AgendamentoService agendamentoService;

        public ProfissionalDashboardController(
                        ProfissionalLinkService profissionalLinkService,
                        HorarioBloqueioService horarioBloqueioService,
                        HorarioGestaoService horarioGestaoService,
                        AgendamentoService agendamentoService) {
                this.profissionalLinkService = profissionalLinkService;
                this.horarioBloqueioService = horarioBloqueioService;
                this.horarioGestaoService = horarioGestaoService;
                this.agendamentoService = agendamentoService;
        }

        /**
         * Dashboard do profissional.
         */
        @Operation(summary = "Obter dashboard", description = "Retorna dados do profissional e resumo")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Dashboard retornado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardResponseDto.class))),
                        @ApiResponse(responseCode = "401", description = "Link inválido ou expirado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/{accessToken}")
        public ResponseEntity<?> obterDashboard(
                        @Parameter(description = "Token de acesso UUID gerado pela barbearia", example = "abc123def456") @PathVariable String accessToken) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                return ResponseEntity.ok(new DashboardResponseDto(
                                funcionario.getId(),
                                funcionario.getNome(),
                                funcionario.getEmail(),
                                funcionario.getPerfilType().toString(),
                                "Link válido"));
        }

        /**
         * Lista agendamentos do profissional com filtros.
         */
        @Operation(summary = "Listar agendamentos", description = "Lista agendamentos do profissional")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de agendamentos", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AgendamentoBriefDto.class)))),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/{accessToken}/agendamentos")
        public ResponseEntity<?> listarAgendamentos(
                        @PathVariable String accessToken,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String dataInicio,
                        @RequestParam(required = false) String dataFim) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                List<AgendamentoBriefDto> agendamentos = agendamentoService
                                .listarAgendamentosProfissional(funcionario.getId());
                return ResponseEntity.ok(agendamentos);
        }

        /**
         * Lista horários de funcionamento do profissional.
         */
        @Operation(summary = "Listar horários", description = "Lista horários de trabalho")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de horários", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HorarioFuncionamentoResponseDto.class)))),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/{accessToken}/horarios")
        public ResponseEntity<?> listarMeusHorarios(
                        @Parameter(description = "Token de acesso UUID", example = "abc123def456") @PathVariable String accessToken) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

                List<HorarioFuncionamentoResponseDto> horarios = horarioGestaoService
                                .listarHorariosFuncionario(funcionario.getId());

                return ResponseEntity.ok(horarios);
        }

        /**
         * Profissional define seu horário de trabalho para um dia da semana.
         */
        @Operation(summary = "Definir horário", description = "Define horário de trabalho para um dia")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Horário salvo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HorarioFuncionamentoResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/{accessToken}/horarios")
        public ResponseEntity<?> definirMeuHorario(
                        @Parameter(description = "Token de acesso") @PathVariable String accessToken,
                        @Valid @RequestBody HorarioFuncionamentoRequestDto requestDto) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                // Profissional define seu próprio horário -> usa ID da barbearia associada ao
                // funcionário
                Long barbeariaId = funcionario.getBarbeariaId();
                var horario = horarioGestaoService.salvarHorarioFuncionario(barbeariaId, funcionario.getId(),
                                requestDto);
                return ResponseEntity.ok(horario);
        }

        /**
         * Profissional define todos os horários da semana de uma vez.
         */
        @Operation(summary = "Definir horários em lote", description = "Define horários para a semana toda")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Horários salvos", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HorarioFuncionamentoResponseDto.class)))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/{accessToken}/horarios/lote")
        public ResponseEntity<?> definirHorariosEmLote(
                        @PathVariable String accessToken,
                        @Valid @RequestBody HorarioLoteRequestDto requestDto) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                Long barbeariaId = funcionario.getBarbeariaId();
                List<HorarioFuncionamentoResponseDto> horarios = horarioGestaoService.salvarHorariosEmLote(barbeariaId,
                                funcionario.getId(), requestDto);
                return ResponseEntity.ok(horarios);
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
                        @ApiResponse(responseCode = "201", description = "Bloqueio criado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HorarioBloqueadoResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos ou sobreposição", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/{accessToken}/bloqueios")
        public ResponseEntity<?> criarBloqueio(
                        @Parameter(description = "Token de acesso") @PathVariable String accessToken,
                        @Valid @RequestBody HorarioBloqueadoRequestDto requestDto) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

                HorarioBloqueadoResponseDto bloqueio = horarioBloqueioService.criarBloqueio(
                                funcionario.getId(), requestDto, "PROFISSIONAL");

                return ResponseEntity.status(HttpStatus.CREATED).body(bloqueio);
        }

        /**
         * Cria múltiplos bloqueios em lote.
         */
        @Operation(summary = "Criar bloqueios em lote", description = "Profissional cria múltiplos bloqueios de uma vez (ex: todos os almoços da semana)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Bloqueios criados", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HorarioBloqueadoResponseDto.class)))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos ou sobreposição", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/{accessToken}/bloqueios/lote")
        public ResponseEntity<?> criarBloqueiosEmLote(
                        @PathVariable String accessToken,
                        @Valid @RequestBody HorarioBloqueadoLoteRequestDto requestDto) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

                List<HorarioBloqueadoResponseDto> bloqueios = horarioBloqueioService.criarBloqueiosEmLote(
                                funcionario.getId(), requestDto, "PROFISSIONAL");

                return ResponseEntity.status(HttpStatus.CREATED).body(bloqueios);
        }

        /**
         * Lista bloqueios do profisional.
         */
        @Operation(summary = "Listar bloqueios", description = "Profissional lista todos seus bloqueios ativos")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de bloqueios", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HorarioBloqueadoResponseDto.class)))),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/{accessToken}/bloqueios")
        public ResponseEntity<?> listarBloqueios(
                        @PathVariable String accessToken,
                        @RequestParam(required = false) String dataInicio,
                        @RequestParam(required = false) String dataFim) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

                LocalDate inicio = dataInicio != null ? LocalDate.parse(dataInicio) : null;
                LocalDate fim = dataFim != null ? LocalDate.parse(dataFim) : null;
                List<HorarioBloqueadoResponseDto> bloqueios;
                if (inicio != null && fim != null) {
                        bloqueios = horarioBloqueioService.listarBloqueiosPorPeriodo(funcionario.getId(), inicio, fim);
                } else {
                        // Se não passar datas, lista futuros ou todos (depende da regra, aqui listando
                        // tudo por enquanto)
                        // Como não tem método listarTodos sem barbeariaId no service exposto aqui,
                        // vamos usar o por periodo com range grande ou criar metodo novo.
                        // Usando periodo padrao de 1 ano para frente se nao informado
                        bloqueios = horarioBloqueioService.listarBloqueiosPorPeriodo(funcionario.getId(),
                                        LocalDate.now(),
                                        LocalDate.now().plusYears(1));
                }
                return ResponseEntity.ok(bloqueios);
        }

        /**
         * Remove bloqueio (apenas os criados pelo profissional).
         */
        @Operation(summary = "Remover bloqueio", description = "Remove um bloqueio (apenas se criado pelo profissional)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Bloqueio removido"),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "403", description = "Não autorizado a remover este bloqueio", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Bloqueio não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @DeleteMapping("/{accessToken}/bloqueios/{bloqueioId}")
        public ResponseEntity<?> removerBloqueio(
                        @PathVariable String accessToken,
                        @PathVariable Long bloqueioId) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                horarioBloqueioService.removerBloqueio(bloqueioId, funcionario.getId(), "PROFISSIONAL");
                return ResponseEntity.noContent().build();
        }

        // ===== ENDPOINTS Para EXCEÇÕES DE HORÁRIO =====

        @Operation(summary = "Listar exceções de horário", description = "Profissional lista suas exceções (disponibilidade extra em datas específicas)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de exceções", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HorarioExcecaoResponseDto.class)))),
                        @ApiResponse(responseCode = "401", description = "Link inválido ou expirado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/{accessToken}/excecoes")
        public ResponseEntity<?> listarExcecoes(
                        @PathVariable String accessToken,
                        @RequestParam(required = false) String dataInicio,
                        @RequestParam(required = false) String dataFim) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                LocalDate inicio = dataInicio != null ? LocalDate.parse(dataInicio) : LocalDate.now();
                LocalDate fim = dataFim != null ? LocalDate.parse(dataFim) : LocalDate.now().plusYears(1);
                List<HorarioExcecaoResponseDto> excecoes = horarioGestaoService.listarExcecoesPorPeriodo(
                                funcionario.getId(), inicio, fim);
                return ResponseEntity.ok(excecoes);
        }

        @Operation(summary = "Criar exceção de horário", description = "Profissional adiciona disponibilidade extra em data específica (ex: trabalhar no domingo)", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Trabalhar Domingo", value = """
                        {
                          "data": "2025-12-01",
                          "horaAbertura": "10:00",
                          "horaFechamento": "14:00",
                          "motivo": "Trabalhando domingo por demanda"
                        }
                        """))))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Exceção criada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HorarioExcecaoResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos ou já existe exceção nesta data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/{accessToken}/excecoes")
        public ResponseEntity<?> criarExcecao(
                        @PathVariable String accessToken,
                        @Valid @RequestBody HorarioExcecaoRequestDto requestDto) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);

                HorarioExcecaoResponseDto excecao = horarioGestaoService.criarExcecao(
                                funcionario.getId(), requestDto, "PROFISSIONAL");

                return ResponseEntity.status(HttpStatus.CREATED).body(excecao);
        }

        @Operation(summary = "Criar exceções em lote", description = "Profissional cria múltiplas exceções de uma vez")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Exceções criadas", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HorarioExcecaoResponseDto.class)))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/{accessToken}/excecoes/lote")
        public ResponseEntity<?> criarExcecoesEmLote(
                        @PathVariable String accessToken,
                        @Valid @RequestBody HorarioExcecaoLoteRequestDto requestDto) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                List<HorarioExcecaoResponseDto> excecoes = horarioGestaoService.criarExcecoesEmLote(
                                funcionario.getId(), requestDto, "PROFISSIONAL");
                return ResponseEntity.status(HttpStatus.CREATED).body(excecoes);
        }

        @Operation(summary = "Remover exceção", description = "Remove uma exceção (apenas se criada pelo profissional)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Exceção removida"),
                        @ApiResponse(responseCode = "401", description = "Link inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "403", description = "Não autorizado a remover esta exceção", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Exceção não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @DeleteMapping("/{accessToken}/excecoes/{excecaoId}")
        public ResponseEntity<?> removerExcecao(
                        @PathVariable String accessToken,
                        @PathVariable Long excecaoId) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                horarioGestaoService.removerExcecao(excecaoId, funcionario.getId(), "PROFISSIONAL");
                return ResponseEntity.noContent().build();
        }

        // ===== ENDPOINTS Para GERENCIAR AGENDAMENTOS =====

        @Operation(summary = "Confirmar agendamento", description = "Profissional confirma um agendamento PENDENTE do seu calendário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Agendamento confirmado"),
                        @ApiResponse(responseCode = "401", description = "Link inválido ou expirado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "403", description = "Sem permissão para confirmar este agendamento", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/{accessToken}/agendamentos/{agendamentoId}/confirmar")
        public ResponseEntity<?> confirmarAgendamento(
                        @PathVariable String accessToken,
                        @PathVariable Long agendamentoId) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                agendamentoService.confirmarAgendamento(agendamentoId, funcionario.getId(), "BARBEIRO");
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Concluir agendamento", description = "Profissional marca agendamento como CONCLUÍDO após atendimento")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Agendamento concluído"),
                        @ApiResponse(responseCode = "401", description = "Link inválido ou expirado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "403", description = "Sem permissão para concluir este agendamento", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/{accessToken}/agendamentos/{agendamentoId}/concluir")
        public ResponseEntity<?> concluirAgendamento(
                        @PathVariable String accessToken,
                        @PathVariable Long agendamentoId) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                agendamentoService.concluirAgendamento(agendamentoId, funcionario.getId(), "BARBEIRO");
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Cancelar agendamento", description = "Profissional cancela um agendamento do seu calendário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Agendamento cancelado"),
                        @ApiResponse(responseCode = "401", description = "Link inválido ou expirado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "403", description = "Sem permissão para cancelar este agendamento", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/{accessToken}/agendamentos/{agendamentoId}/cancelar")
        public ResponseEntity<?> cancelarAgendamento(
                        @PathVariable String accessToken,
                        @PathVariable Long agendamentoId) {
                JpaFuncionario funcionario = profissionalLinkService.validarToken(accessToken);
                agendamentoService.cancelarAgendamento(agendamentoId, funcionario.getId(), "BARBEIRO");
                return ResponseEntity.noContent().build();
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiErrorDto> handleIllegalArgumentException(IllegalArgumentException ex,
                        HttpServletRequest request) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorDto(
                                java.time.LocalDateTime.now(),
                                401,
                                "Unauthorized",
                                ex.getMessage(),
                                request.getRequestURI()));
        }
}
