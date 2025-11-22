package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.BarbeariaListItemDto;
import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.dto.HorarioDisponivelDto;
import com.barbearia.application.dto.FuncionarioRequestDto;
import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.application.dto.AgendamentoBarbeariaDto;
import com.barbearia.application.dto.AgendamentoResponseDto;
import com.barbearia.application.dto.AgendamentoUpdateDto;
import com.barbearia.application.dto.RelatorioFinanceiroDto;
import com.barbearia.application.dto.RelatorioComissoesDto;
import com.barbearia.application.dto.ClienteAtendidoDto;
import com.barbearia.application.dto.ClienteDetalhesDto;
import com.barbearia.application.services.BarbeariaService;
import com.barbearia.application.services.HorarioService;
import com.barbearia.application.services.FuncionarioService;
import com.barbearia.application.services.AgendamentoService;
import com.barbearia.application.services.FinanceiroService;
import com.barbearia.application.services.ComissaoService;
import com.barbearia.application.services.ClienteGestaoService;
import com.barbearia.application.security.JwtService;
import com.barbearia.domain.enums.PeriodoRelatorio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Gestão de Barbearias.
 */
@Tag(name = "Barbearias", description = "Gestão completa de barbearias: públicos, serviços, funcionários, agendamentos, financeiro")
@RestController
@RequestMapping("/api/barbearias")
@CrossOrigin(origins = "*")
public class BarbeariaController {

    private final BarbeariaService barbeariaService;
    private final HorarioService horarioService;
    private final FuncionarioService funcionarioService;
    private final AgendamentoService agendamentoService;
    private final FinanceiroService financeiroService;
    private final ComissaoService comissaoService;
    private final ClienteGestaoService clienteGestaoService;
    private final com.barbearia.application.services.HorarioGestaoService horarioGestaoService;
    private final JwtService jwtService;

    public BarbeariaController(BarbeariaService barbeariaService,
            HorarioService horarioService,
            FuncionarioService funcionarioService,
            AgendamentoService agendamentoService,
            FinanceiroService financeiroService,
            ComissaoService comissaoService,
            ClienteGestaoService clienteGestaoService,
            com.barbearia.application.services.HorarioGestaoService horarioGestaoService,
            JwtService jwtService) {
        this.barbeariaService = barbeariaService;
        this.horarioService = horarioService;
        this.funcionarioService = funcionarioService;
        this.agendamentoService = agendamentoService;
        this.financeiroService = financeiroService;
        this.comissaoService = comissaoService;
        this.clienteGestaoService = clienteGestaoService;
        this.horarioGestaoService = horarioGestaoService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Listar todas as barbearias ativas", description = "Retorna uma lista de barbearias ativas no sistema. Endpoint público, não requer autenticação.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BarbeariaListItemDto.class)), examples = @ExampleObject(name = "Lista de Barbearias", value = "[{\"id\":1,\"nome\":\"Barbearia Elite\",\"ativo\":true},{\"id\":2,\"nome\":\"Salão Premium\",\"ativo\":true}]"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping
    public ResponseEntity<?> listarBarbearias() {
        try {
            List<BarbeariaListItemDto> barbearias = barbeariaService.listarBarbearias();
            return ResponseEntity.ok(barbearias);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao listar barbearias: " + e.getMessage());
        }
    }

    @Operation(summary = "Listar serviços de uma barbearia", description = "Retorna todos os serviços ativos de uma barbearia específica. Endpoint público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ServicoDto.class)), examples = @ExampleObject(name = "Serviços da Barbearia", value = "[{\"id\":1,\"nome\":\"Corte Masculino\",\"descricao\":\"Corte clássico\",\"preco\":30.0,\"duracao\":30,\"ativo\":true},{\"id\":2,\"nome\":\"Barba\",\"descricao\":\"Aparar e modelar\",\"preco\":25.0,\"duracao\":20,\"ativo\":true}]"))),
            @ApiResponse(responseCode = "400", description = "Barbearia está inativa", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Barbearia não encontrada", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/{id}/servicos")
    public ResponseEntity<?> listarServicosPorBarbearia(
            @Parameter(description = "ID da barbearia", required = true, example = "1") @PathVariable Long id) {
        try {
            List<ServicoDto> servicos = barbeariaService.listarServicosPorBarbearia(id);
            return ResponseEntity.ok(servicos);
        } catch (IllegalArgumentException e) {
            String mensagem = e.getMessage();

            // Se for barbearia não encontrada, retorna 404
            if (mensagem.contains("Barbearia não encontrada")) {
                return ResponseEntity.status(404).body(mensagem);
            }

            // Se for barbearia inativa, retorna 400
            if (mensagem.contains("inativa")) {
                return ResponseEntity.badRequest().body(mensagem);
            }

            return ResponseEntity.badRequest().body(mensagem);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao listar serviços: " + e.getMessage());
        }
    }

    @Operation(summary = "Obter horários disponíveis para agendamento", description = "Retorna lista de horários disponíveis para um serviço específico em uma barbearia. "
            +
            "Permite filtrar por profissional específico (opcional).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horários disponíveis retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HorarioDisponivelDto.class)), examples = @ExampleObject(name = "Horários Disponíveis", value = "[{\"funcionarioId\":1,\"funcionarioNome\":\"João Silva\",\"profissao\":\"BARBEIRO\",\"data\":\"2024-11-25\",\"horarioInicio\":\"09:00\",\"horarioFim\":\"09:30\"},{\"funcionarioId\":1,\"funcionarioNome\":\"João Silva\",\"profissao\":\"BARBEIRO\",\"data\":\"2024-11-25\",\"horarioInicio\":\"09:30\",\"horarioFim\":\"10:00\"}]"))),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Barbearia ou serviço não encontrado", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/{id}/horarios-disponiveis")
    public ResponseEntity<?> obterHorariosDisponiveis(
            @Parameter(description = "ID da barbearia", required = true, example = "1") @PathVariable Long id,
            @Parameter(description = "ID do serviço desejado", required = true, example = "1") @RequestParam Long servicoId,
            @Parameter(description = "Data para consultar horários (formato: yyyy-MM-dd)", required = true, example = "2024-11-25") @RequestParam String dataStr,
            @Parameter(description = "ID do profissional (opcional, para filtrar)", required = false, example = "1") @RequestParam(required = false) Long profissionalId) {
        try {
            // Validar parâmetros
            if (servicoId == null) {
                return ResponseEntity.badRequest().body("Parâmetro servicoId é obrigatório");
            }

            if (dataStr == null || dataStr.isBlank()) {
                return ResponseEntity.badRequest().body("Parâmetro data é obrigatório (formato: yyyy-MM-dd)");
            }

            // Fazer parse da data
            LocalDate data;
            try {
                data = LocalDate.parse(dataStr);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Formato de data inválido. Use yyyy-MM-dd");
            }

            // Chamar serviço para obter horários disponíveis
            List<HorarioDisponivelDto> horariosDisponiveis = horarioService.obterHorariosDisponiveis(id, servicoId,
                    data, profissionalId);

            return ResponseEntity.ok(horariosDisponiveis);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao obter horários disponíveis: " + e.getMessage());
        }
    }

    /**
     * Cria ou atualiza um horário de funcionamento para a barbearia autenticada.
     * 
     * Segurança:
     * - Requer autenticação JWT (Bearer token)
     * - Apenas role BARBEARIA pode acessar
     * - BarbeariaId é extraído do token (não pode ser alterado)
     * 
     * Validações:
     * - diaSemana deve estar entre 0 (domingo) e 6 (sábado)
     * - horaAbertura e horaFechamento são obrigatórios
     * - horaAbertura deve ser antes de horaFechamento
     * - Barbearia deve existir e estar ativa
     * 
     * Retorna:
     * - 201 (Created) com dados do horário criado/atualizado
     * - 400 (Bad Request) se dados inválidos
     * /*
     * ROTA DEPRECATED - REMOVIDA NA REFATORAÇÃO
     * 
     * Motivo: Horários de funcionamento são SEMPRE associados a um profissional
     * específico.
     * Use: POST /api/horarios/funcionario/{funcionarioId} no HorarioController
     * 
     * Esta rota foi desativada pois não faz sentido criar horário sem vincular a um
     * profissional.
     * No novo fluxo: Serviços → Profissionais → Horários Disponíveis
     */
    /*
     * @PostMapping("/horarios")
     * public ResponseEntity<?> criarHorarioFuncionamento(
     * 
     * @RequestBody HorarioFuncionamentoRequestDto requestDto,
     * HttpServletRequest request) {
     * try {
     * // Extrai token do cabeçalho Authorization
     * String token = request.getHeader("Authorization");
     * if (token == null || !token.startsWith("Bearer ")) {
     * return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
     * .body("Token JWT não fornecido ou inválido");
     * }
     * 
     * // Remove prefixo "Bearer " do token
     * token = token.substring(7);
     * 
     * // Extrai ID da barbearia do token
     * Object userIdObj = jwtService.extractClaim(token, "userId");
     * if (userIdObj == null) {
     * return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
     * .body("Token JWT inválido: userId não encontrado");
     * }
     * 
     * Long barbeariaId = ((Number) userIdObj).longValue();
     * 
     * // Delega para o serviço de gestão de horários
     * HorarioFuncionamentoResponseDto responseDto =
     * horarioGestaoService.criarHorarioFuncionamento(barbeariaId, requestDto);
     * 
     * return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
     * 
     * } catch (IllegalArgumentException e) {
     * return ResponseEntity.badRequest().body(e.getMessage());
     * } catch (Exception e) {
     * return ResponseEntity.internalServerError()
     * .body("Erro ao criar horário de funcionamento: " + e.getMessage());
     * }
     * }
     */

    @Operation(summary = "Listar meus funcionários", description = "Retorna todos os funcionários ativos da barbearia autenticada. "
            +
            "Requer autenticação JWT com role BARBEARIA. O ID da barbearia é extraído do token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de funcionários retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = FuncionarioResponseDto.class)), examples = @ExampleObject(name = "Lista de Funcionários", value = "[{\"id\":1,\"barbeariaId\":1,\"nome\":\"João Silva\",\"email\":\"joao@email.com\",\"telefone\":\"87999998888\",\"profissao\":\"BARBEIRO\",\"ativo\":true,\"dataCriacao\":\"2024-01-15T10:00:00\",\"dataAtualizacao\":\"2024-01-15T10:00:00\"},{\"id\":2,\"barbeariaId\":1,\"nome\":\"Maria Santos\",\"email\":\"maria@email.com\",\"telefone\":\"87988887777\",\"profissao\":\"MANICURE\",\"ativo\":true,\"dataCriacao\":\"2024-02-01T14:30:00\",\"dataAtualizacao\":\"2024-02-01T14:30:00\"}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/meus-funcionarios")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> listarMeusFuncionarios(HttpServletRequest request) {
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

            // Lista funcionários ativos da barbearia
            List<FuncionarioResponseDto> funcionarios = funcionarioService.listarFuncionariosDaBarbearia(barbeariaId);

            return ResponseEntity.ok(funcionarios);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao listar funcionários: " + e.getMessage());
        }
    }

    @Operation(summary = "Criar novo funcionário", description = "Cria um funcionário para a barbearia autenticada. " +
            "Profissões permitidas: BARBEIRO, MANICURE, ESTETICISTA, COLORISTA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioResponseDto.class), examples = @ExampleObject(name = "Funcionário Criado", value = "{\"id\":3,\"barbeariaId\":1,\"nome\":\"Carlos Souza\",\"email\":\"carlos@email.com\",\"telefone\":\"87977776666\",\"profissao\":\"BARBEIRO\",\"ativo\":true,\"dataCriacao\":\"2024-11-22T17:50:00\",\"dataAtualizacao\":\"2024-11-22T17:50:00\"}"))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já cadastrado", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do funcionário", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioRequestDto.class), examples = @ExampleObject(name = "Novo Funcionário", value = "{\"nome\":\"Carlos Souza\",\"email\":\"carlos@email.com\",\"telefone\":\"87977776666\",\"perfilType\":\"BARBEIRO\"}")))
    @PostMapping("/meus-funcionarios")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> criarFuncionario(
            @Valid @RequestBody FuncionarioRequestDto requestDto,
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

            // Cria o funcionário
            FuncionarioResponseDto funcionarioCriado = funcionarioService.criarFuncionario(requestDto, barbeariaId);

            return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioCriado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao criar funcionário: " + e.getMessage());
        }
    }

    @Operation(summary = "Listar agendamentos da barbearia", description = "Retorna todos os agendamentos da barbearia autenticada. "
            +
            "Permite filtrar por data específica (opcional).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AgendamentoBarbeariaDto.class)), examples = @ExampleObject(name = "Lista de Agendamentos", value = "[{\"id\":1,\"clienteNome\":\"Pedro Oliveira\",\"servicoNome\":\"Corte Masculino\",\"funcionarioNome\":\"João Silva\",\"data\":\"2024-11-25\",\"horarioInicio\":\"14:00\",\"horarioFim\":\"14:30\",\"status\":\"CONFIRMADO\",\"valorTotal\":30.0},{\"id\":2,\"clienteNome\":\"Ana Costa\",\"servicoNome\":\"Manicure\",\"funcionarioNome\":\"Maria Santos\",\"data\":\"2024-11-25\",\"horarioInicio\":\"15:00\",\"horarioFim\":\"16:00\",\"status\":\"PENDENTE\",\"valorTotal\":40.0}]"))),
            @ApiResponse(responseCode = "400", description = "Formato de data inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/meus-agendamentos")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> listarMeusAgendamentos(
            @Parameter(description = "Data para filtrar agendamentos (formato: yyyy-MM-dd)", required = false, example = "2024-11-25") @RequestParam(required = false) String data,
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

            // Parse da data se fornecida
            LocalDate dataFiltro = null;
            if (data != null && !data.isBlank()) {
                try {
                    dataFiltro = LocalDate.parse(data);
                } catch (Exception e) {
                    return ResponseEntity.badRequest()
                            .body("Formato de data inválido. Use yyyy-MM-dd");
                }
            }

            // Lista agendamentos
            List<AgendamentoBarbeariaDto> agendamentos = agendamentoService.listarAgendamentosBarbearia(barbeariaId,
                    dataFiltro);

            return ResponseEntity.ok(agendamentos);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao listar agendamentos: " + e.getMessage());
        }
    }

    @Operation(summary = "Atualizar status de agendamento", description = "Atualiza o status de um agendamento da barbearia. "
            +
            "Status possíveis: PENDENTE, CONFIRMADO, CANCELADO, CONCLUIDO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgendamentoResponseDto.class), examples = @ExampleObject(name = "Agendamento Atualizado", value = "{\"id\":1,\"clienteId\":5,\"servicoId\":1,\"funcionarioId\":1,\"barbeariaId\":1,\"data\":\"2024-11-25\",\"horarioInicio\":\"14:00\",\"horarioFim\":\"14:30\",\"status\":\"CONFIRMADO\",\"valorTotal\":30.0,\"observacoes\":null}"))),
            @ApiResponse(responseCode = "400", description = "Transição de status inválida", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Agendamento não pertence à barbearia", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novo status do agendamento", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AgendamentoUpdateDto.class), examples = @ExampleObject(name = "Confirmar Agendamento", value = "{\"status\":\"CONFIRMADO\"}")))
    @PatchMapping("/agendamentos/{id}")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> atualizarStatusAgendamento(
            @Parameter(description = "ID do agendamento", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody AgendamentoUpdateDto updateDto,
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

            // Atualiza status do agendamento
            AgendamentoResponseDto agendamentoAtualizado = agendamentoService.atualizarStatusAgendamento(id,
                    barbeariaId, updateDto);

            return ResponseEntity.ok(agendamentoAtualizado);

        } catch (com.barbearia.domain.exceptions.AgendamentoNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (com.barbearia.domain.exceptions.AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao atualizar agendamento: " + e.getMessage());
        }
    }

    @Operation(summary = "Obter relatório financeiro", description = "Gera relatório financeiro da barbearia para o período selecionado. "
            +
            "Períodos disponíveis: DIA (24h), SEMANA (7 dias), MES (30 dias).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RelatorioFinanceiroDto.class), examples = @ExampleObject(name = "Relatório Financeiro", value = "{\"faturamentoTotal\":2500.50,\"totalAgendamentos\":45,\"ticketMedio\":55.57,\" faturamentoPorDia\":83.35,\"servicosMaisRentaveis\":[{\"servicoNome\":\"Corte Masculino\",\"quantidade\":25,\"faturamento\":750.0},{\"servicoNome\":\"Barba\",\"quantidade\":15,\"faturamento\":375.0}]}"))),
            @ApiResponse(responseCode = "400", description = "Período inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/gestao-financeira")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> obterRelatorioFinanceiro(
            @Parameter(description = "Período do relatório", required = false, example = "MES", schema = @Schema(allowableValues = {
                    "DIA", "SEMANA", "MES" })) @RequestParam(defaultValue = "MES") PeriodoRelatorio periodo,
            HttpServletRequest request) {

        try {
            // Extrair token JWT do header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT não fornecido");
            }

            // Remove prefixo "Bearer " do token
            String token = authHeader.substring(7);

            // Extrai ID da barbearia do token
            Object userIdObj = jwtService.extractClaim(token, "userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT inválido: userId não encontrado");
            }

            Long barbeariaId = ((Number) userIdObj).longValue();

            // Gerar relatório financeiro
            RelatorioFinanceiroDto relatorio = financeiroService.gerarRelatorioFinanceiro(
                    barbeariaId, periodo);

            return ResponseEntity.ok(relatorio);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao gerar relatório financeiro: " + e.getMessage());
        }
    }

    @Operation(summary = "Gerar relatório de comissões", description = "Gera relatório detalhado de comissões dos profissionais para um período específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório de comissões gerado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RelatorioComissoesDto.class), examples = @ExampleObject(name = "Relatório de Comissões", value = "{\"totalComissoes\":500.0,\"comissoesPorFuncionario\":[{\"funcionarioNome\":\"João Silva\",\" quantidadeAtendimentos\":20,\"valorTotalAtendimentos\":600.0,\"comissao\":300.0},{\"funcionarioNome\":\"Maria Santos\",\" quantidadeAtendimentos\":15,\"valorTotalAtendimentos\":400.0,\"comissao\":200.0}]}"))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de data inválidos", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/relatorios/comissoes")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> gerarRelatorioComissoes(
            @Parameter(description = "Data de início do período", required = true, example = "2024-11-01") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data de fim do período", required = true, example = "2024-11-30") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);
            RelatorioComissoesDto relatorio = comissaoService.gerarRelatorioComissoes(barbeariaId, dataInicio, dataFim);
            return ResponseEntity.ok(relatorio);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao gerar relatório de comissões: " + e.getMessage());
        }
    }

    @Operation(summary = "Listar meus clientes", description = "Retorna todos os clientes que possuem pelo menos um agendamento com a barbearia. "
            +
            "Clientes anonimizados (LGPD) não aparecem na listagem.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ClienteAtendidoDto.class)), examples = @ExampleObject(name = "Lista de Clientes", value = "[{\"id\":1,\"nome\":\"Pedro Oliveira\",\"email\":\"pedro@email.com\",\"telefone\":\"87999887766\",\"totalAgendamentos\":5,\"ultimoAgendamento\":\"2024-11-20\"},{\"id\":2,\"nome\":\"Ana Costa\",\"email\":\"ana@email.com\",\"telefone\":\"87988776655\",\"totalAgendamentos\":3,\"ultimoAgendamento\":\"2024-11-22\"}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/meus-clientes")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> listarMeusClientes(HttpServletRequest request) {

        try {
            // Extrair token JWT do header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT não fornecido");
            }

            // Remove prefixo "Bearer " do token
            String token = authHeader.substring(7);

            // Extrai ID da barbearia do token
            Object userIdObj = jwtService.extractClaim(token, "userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT inválido: userId não encontrado");
            }

            Long barbeariaId = ((Number) userIdObj).longValue();

            // Listar clientes atendidos
            List<ClienteAtendidoDto> clientes = clienteGestaoService.listarClientesAtendidos(barbeariaId);

            return ResponseEntity.ok(clientes);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao listar clientes: " + e.getMessage());
        }
    }

    @Operation(summary = "Buscar detalhes de um cliente", description = "Retorna histórico completo de agendamentos e estatísticas de um cliente específico. "
            +
            "Apenas a barbearia que atendeu o cliente pode visualizar seus dados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes do cliente retornados com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ClienteDetalhesDto.class), examples = @ExampleObject(name = "Detalhes do Cliente", value = "{\"id\":1,\"nome\":\"Pedro Oliveira\",\"email\":\"pedro@email.com\",\"telefone\":\"87999887766\",\"dataCadastro\":\"2024-01-10\",\"totalAgendamentos\":8,\"totalGasto\":420.0,\"servicosFavoritos\":[\"Corte Masculino\",\"Barba\"],\"ultimosAgendamentos\":[{\"id\":15,\"servicoNome\":\"Corte Masculino\",\"data\":\"2024-11-20\",\"horario\":\"14:00\",\"status\":\"CONCLUIDO\",\"valor\":30.0}]}"))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado ou não atendido pela barbearia", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/clientes/{id}")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> buscarDetalhesCliente(
            @Parameter(description = "ID do cliente", required = true, example = "1") @PathVariable Long id,
            HttpServletRequest request) {

        try {
            // Extrair token JWT do header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT não fornecido");
            }

            // Remove prefixo "Bearer " do token
            String token = authHeader.substring(7);

            // Extrai ID da barbearia do token
            Object userIdObj = jwtService.extractClaim(token, "userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT inválido: userId não encontrado");
            }

            Long barbeariaId = ((Number) userIdObj).longValue();

            // Buscar detalhes do cliente
            ClienteDetalhesDto cliente = clienteGestaoService.buscarDetalhesCliente(id, barbeariaId);

            return ResponseEntity.ok(cliente);

        } catch (com.barbearia.domain.exceptions.ClienteNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao buscar detalhes do cliente: " + e.getMessage());
        }
    }

    @Operation(summary = "Anonimizar dados do cliente (LGPD)", description = "Anonimiza irreversivelmente os dados pessoais de um cliente conforme LGPD. "
            +
            "Substitui dados por tokens únicos e preserva histórico de agendamentos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cliente anonimizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Cliente já anonimizado ou não pertence à barbearia", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @DeleteMapping("/clientes/{id}")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> anonimizarCliente(
            @Parameter(description = "ID do cliente a ser anonimizado", required = true, example = "1") @PathVariable Long id,
            HttpServletRequest request) {

        try {
            // Extrair token JWT do header Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT não fornecido");
            }

            // Remove prefixo "Bearer " do token
            String token = authHeader.substring(7);

            // Extrai ID da barbearia do token
            Object userIdObj = jwtService.extractClaim(token, "userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT inválido: userId não encontrado");
            }

            Long barbeariaId = ((Number) userIdObj).longValue();

            // Anonimizar dados do cliente
            clienteGestaoService.anonimizarCliente(id, barbeariaId);

            return ResponseEntity.noContent().build();

        } catch (com.barbearia.domain.exceptions.ClienteNaoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (com.barbearia.domain.exceptions.AcessoNegadoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao anonimizar cliente: " + e.getMessage());
        }
    }

    // ===== ENDPOINTS PARA GESTÃO DE EXCEÇÕES/FERIADOS (T17) =====

    @Operation(summary = "Listar exceções de horário", description = "Retorna todas as exceções de horário cadastradas (feriados, fechamentos especiais, etc).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de exceções retornada com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Lista de Exceções", value = "[{\"id\":1,\"data\":\"2024-12-25\",\"motivo\":\"Natal\",\"diaInteiro\":true,\"ativo\":true},{\"id\":2,\"data\":\"2025-01-01\",\"motivo\":\"Ano Novo\",\"diaInteiro\":true,\"ativo\":true}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/excecoes")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> listarExcecoes(HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);
            var excecoes = horarioGestaoService.listarExcecoes(barbeariaId);
            return ResponseEntity.ok(excecoes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao listar exceções: " + e.getMessage());
        }
    }

    @Operation(summary = "Listar exceções por período", description = "Retorna exceções de horário em um período específico de datas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de exceções no período retornada com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Exceções no Período", value = "[{\"id\":3,\"data\":\"2024-12-24\",\"motivo\":\"Véspera de Natal - Fechamento às 14h\",\"diaInteiro\":false,\"horarioInicio\":\"08:00\",\"horarioFim\":\"14:00\",\"ativo\":true}]"))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/excecoes/periodo")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> listarExcecoesNoPeriodo(
            @Parameter(description = "Data inicial do período", required = true, example = "2024-12-01") @RequestParam LocalDate dataInicio,
            @Parameter(description = "Data final do período", required = true, example = "2024-12-31") @RequestParam LocalDate dataFim,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);
            var excecoes = horarioGestaoService.listarExcecoesNoPeriodo(barbeariaId, dataInicio, dataFim);
            return ResponseEntity.ok(excecoes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao listar exceções no período: " + e.getMessage());
        }
    }

    @Operation(summary = "Buscar exceção por ID", description = "Retorna os detalhes de uma exceção de horário específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exceção encontrada", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Exceção Encontrada", value = "{\"id\":1,\"barbeariaId\":1,\"data\":\"2024-12-25\",\"motivo\":\"Natal\",\"diaInteiro\":true,\"ativo\":true}"))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Exceção não pertence à barbearia", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Exceção não encontrada", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/excecoes/{id}")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> buscarExcecaoPorId(
            @Parameter(description = "ID da exceção", required = true, example = "1") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);
            var excecao = horarioGestaoService.buscarExcecaoPorId(id, barbeariaId);
            return ResponseEntity.ok(excecao);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao buscar exceção: " + e.getMessage());
        }
    }

    @Operation(summary = "Criar exceção de horário", description = "Cria uma nova exceção de horário (feriado, fechamento especial, etc).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Exceção criada com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Exceção Criada", value = "{\"id\":4,\"barbeariaId\":1,\"data\":\"2025-02-14\",\"motivo\":\"Carnaval\",\"diaInteiro\":true,\"ativo\":true}"))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou já existe exceção para esta data", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados da exceção", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Nova Exceção", value = "{\"data\":\"2025-02-14\",\"motivo\":\"Carnaval\",\"diaInteiro\":true}")))
    @PostMapping("/excecoes")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> criarExcecao(
            @Valid @RequestBody com.barbearia.application.dto.FeriadoExcecaoRequestDto requestDto,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);
            var excecao = horarioGestaoService.criarExcecao(barbeariaId, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(excecao);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao criar exceção: " + e.getMessage());
        }
    }

    @Operation(summary = "Atualizar exceção de horário", description = "Atualiza os dados de uma exceção de horário existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Exceção atualizada com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Exceção Atualizada", value = "{\"id\":1,\"barbeariaId\":1,\"data\":\"2024-12-25\",\"motivo\":\"Natal - Fechado o dia todo\",\"diaInteiro\":true,\"ativo\":true}"))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Exceção não pertence à barbearia", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Exceção não encontrada", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Novos dados da exceção", required = true, content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Atualizar Exceção", value = "{\"data\":\"2024-12-25\",\"motivo\":\"Natal - Fechado o dia todo\",\"diaInteiro\":true}")))
    @PutMapping("/excecoes/{id}")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> atualizarExcecao(
            @Parameter(description = "ID da exceção", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody com.barbearia.application.dto.FeriadoExcecaoRequestDto requestDto,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);
            var excecao = horarioGestaoService.atualizarExcecao(id, barbeariaId, requestDto);
            return ResponseEntity.ok(excecao);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            if (e.getMessage().contains("não pertence")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao atualizar exceção: " + e.getMessage());
        }
    }

    @Operation(summary = "Remover exceção de horário", description = "Remove (desativa) uma exceção de horário.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Exceção removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Exceção não pertence à barbearia", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Exceção não encontrada", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @DeleteMapping("/excecoes/{id}")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> removerExcecao(
            @Parameter(description = "ID da exceção", required = true, example = "1") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);
            horarioGestaoService.removerExcecao(id, barbeariaId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao remover exceção: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para extrair ID da barbearia do token JWT.
     */
    private Long extrairBarbeariaId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token JWT não fornecido");
        }

        String token = authHeader.substring(7);
        Object userIdObj = jwtService.extractClaim(token, "userId");
        if (userIdObj == null) {
            throw new IllegalArgumentException("Token JWT inválido: userId não encontrado");
        }

        return ((Number) userIdObj).longValue();
    }
}
