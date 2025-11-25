package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ApiErrorDto;
import com.barbearia.application.dto.BarbeariaListItemDto;
import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.dto.FuncionarioRequestDto;
import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.application.dto.AgendamentoBarbeariaDto;
import com.barbearia.application.dto.AgendamentoResponseDto;
import com.barbearia.application.dto.AgendamentoReagendamentoDto;
import com.barbearia.application.dto.DashboardMetricasDto;
import com.barbearia.application.dto.ServicoPopularDto;
import com.barbearia.application.dto.HorarioPicoDto;
import com.barbearia.application.dto.RelatorioFinanceiroDto;
import com.barbearia.application.dto.RelatorioComissoesDto;
import com.barbearia.application.dto.ClienteAtendidoDto;
import com.barbearia.application.dto.ClienteDetalhesDto;
import com.barbearia.application.dto.HorarioDisponivelDto;
import com.barbearia.application.services.BarbeariaService;
import com.barbearia.application.services.FuncionarioService;
import com.barbearia.application.services.AgendamentoService;
import com.barbearia.application.services.FinanceiroService;
import com.barbearia.application.services.ComissaoService;
import com.barbearia.application.services.ClienteGestaoService;
import com.barbearia.application.services.HorarioService;
import com.barbearia.application.services.RelatorioService;
import com.barbearia.application.security.JwtService;
import com.barbearia.domain.enums.PeriodoRelatorio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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

import com.barbearia.application.dto.HorarioExcecaoRequestDto;
import com.barbearia.application.dto.HorarioExcecaoResponseDto;
import com.barbearia.application.services.HorarioGestaoService;

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
        private final FuncionarioService funcionarioService;
        private final AgendamentoService agendamentoService;
        private final FinanceiroService financeiroService;
        private final ComissaoService comissaoService;
        private final ClienteGestaoService clienteGestaoService;
        private final HorarioService horarioService;
        private final RelatorioService relatorioService;
        private final HorarioGestaoService horarioGestaoService;
        private final JwtService jwtService;

        public BarbeariaController(
                        BarbeariaService barbeariaService,
                        FuncionarioService funcionarioService,
                        AgendamentoService agendamentoService,
                        FinanceiroService financeiroService,
                        ComissaoService comissaoService,
                        ClienteGestaoService clienteGestaoService,
                        HorarioService horarioService,
                        RelatorioService relatorioService,
                        HorarioGestaoService horarioGestaoService,
                        JwtService jwtService) {
                this.barbeariaService = barbeariaService;
                this.funcionarioService = funcionarioService;
                this.agendamentoService = agendamentoService;
                this.financeiroService = financeiroService;
                this.comissaoService = comissaoService;
                this.clienteGestaoService = clienteGestaoService;
                this.horarioService = horarioService;
                this.relatorioService = relatorioService;
                this.horarioGestaoService = horarioGestaoService;
                this.jwtService = jwtService;
        }

        @Operation(summary = "Listar horários disponíveis", description = "Retorna os horários disponíveis para um serviço em uma data específica")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de horários retornada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Erro de Validação", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "Data inválida",
                                          "path": "/api/barbearias/1/servicos/1/horarios-disponiveis"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Barbearia ou serviço não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Serviço com ID 1 não encontrado",
                                          "path": "/api/barbearias/1/servicos/1/horarios-disponiveis"
                                        }
                                        """)))
        })
        @GetMapping("/{barbeariaId}/servicos/{servicoId}/horarios-disponiveis")
        public ResponseEntity<List<HorarioDisponivelDto>> obterHorariosDisponiveis(
                        @Parameter(description = "ID da barbearia", required = true) @PathVariable Long barbeariaId,
                        @Parameter(description = "ID do serviço", required = true) @PathVariable Long servicoId,
                        @Parameter(description = "Data (YYYY-MM-DD)", required = true) @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                        @Parameter(description = "ID do funcionário (opcional)") @RequestParam(required = false) Long funcionarioId) {

                List<HorarioDisponivelDto> horarios = horarioService.obterHorariosDisponiveis(
                                barbeariaId, servicoId, data, funcionarioId);

                return ResponseEntity.ok(horarios);
        }

        @Operation(summary = "Listar datas com disponibilidade", description = "Retorna os dias do mês que possuem horários disponíveis")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de datas retornada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Erro de Validação", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "Mês ou ano inválido",
                                          "path": "/api/barbearias/1/servicos/1/datas-disponiveis"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Barbearia ou serviço não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Barbearia com ID 1 não encontrada",
                                          "path": "/api/barbearias/1/servicos/1/datas-disponiveis"
                                        }
                                        """)))
        })
        @GetMapping("/{barbeariaId}/servicos/{servicoId}/datas-disponiveis")
        public ResponseEntity<List<LocalDate>> obterDatasDisponiveis(
                        @Parameter(description = "ID da barbearia", required = true) @PathVariable Long barbeariaId,
                        @Parameter(description = "ID do serviço", required = true) @PathVariable Long servicoId,
                        @Parameter(description = "Ano", required = true) @RequestParam int ano,
                        @Parameter(description = "Mês", required = true) @RequestParam int mes,
                        @Parameter(description = "ID do funcionário (opcional)") @RequestParam(required = false) Long funcionarioId) {

                List<LocalDate> datas = horarioService.obterDatasDisponiveis(
                                barbeariaId, servicoId, ano, mes, funcionarioId);

                return ResponseEntity.ok(datas);
        }

        @Operation(summary = "Listar todas as barbearias ativas", description = "Retorna uma lista de barbearias ativas no sistema. Endpoint público, não requer autenticação.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = BarbeariaListItemDto.class)), examples = @ExampleObject(name = "Lista de Barbearias", value = "[{\"id\":1,\"nome\":\"Barbearia Elite\",\"ativo\":true},{\"id\":2,\"nome\":\"Salão Premium\",\"ativo\":true}]"))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
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
                        @ApiResponse(responseCode = "400", description = "Barbearia inativa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(value = """
                                        {
                                          "timestamp": "2025-11-25T14:00:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "Barbearia inativa",
                                          "path": "/api/barbearias/1/servicos"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Barbearia não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(value = """
                                        {
                                          "timestamp": "2025-11-25T14:00:00",
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Barbearia não encontrada com id: 1",
                                          "path": "/api/barbearias/1/servicos"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/{id}/servicos")
        public ResponseEntity<?> listarServicosPorBarbearia(
                        @Parameter(description = "ID da barbearia", required = true, example = "1") @PathVariable Long id) {
                List<ServicoDto> servicos = barbeariaService.listarServicosPorBarbearia(id);
                return ResponseEntity.ok(servicos);
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
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/meus-funcionarios")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> listarMeusFuncionarios(HttpServletRequest request) {
                // Extrai token do cabeçalho Authorization
                String token = request.getHeader("Authorization");
                if (token == null || !token.startsWith("Bearer ")) {
                        throw new com.barbearia.domain.exceptions.AcessoNegadoException(
                                        "Token JWT não fornecido ou inválido");
                }

                // Remove prefixo "Bearer " do token
                token = token.substring(7);

                // Extrai ID da barbearia do token
                Object userIdObj = jwtService.extractClaim(token, "userId");
                if (userIdObj == null) {
                        throw new com.barbearia.domain.exceptions.AcessoNegadoException(
                                        "Token JWT inválido: userId não encontrado");
                }

                Long barbeariaId = ((Number) userIdObj).longValue();

                // Lista funcionários ativos da barbearia
                List<FuncionarioResponseDto> funcionarios = funcionarioService
                                .listarFuncionariosDaBarbearia(barbeariaId);

                return ResponseEntity.ok(funcionarios);
        }

        @Operation(summary = "Criar novo funcionário", description = "Cria um funcionário para a barbearia autenticada. "
                        +
                        "Profissões permitidas: BARBEIRO, MANICURE, ESTETICISTA, COLORISTA.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Funcionário criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioResponseDto.class), examples = @ExampleObject(name = "Funcionário Criado", value = "{\"id\":3,\"barbeariaId\":1,\"nome\":\"Carlos Souza\",\"email\":\"carlos@email.com\",\"telefone\":\"87977776666\",\"profissao\":\"BARBEIRO\",\"ativo\":true,\"dataCriacao\":\"2024-11-22T17:50:00\",\"dataAtualizacao\":\"2024-11-22T17:50:00\"}"))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já cadastrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(value = """
                                        {
                                          "timestamp": "2025-11-25T14:00:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "Email já cadastrado",
                                          "path": "/api/barbearias/meus-funcionarios"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do funcionário", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioRequestDto.class), examples = @ExampleObject(name = "Novo Funcionário", value = "{\"nome\":\"Carlos Souza\",\"email\":\"carlos@email.com\",\"telefone\":\"87977776666\",\"perfilType\":\"BARBEIRO\"}")))
        @PostMapping("/meus-funcionarios")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> criarFuncionario(
                        @Valid @RequestBody FuncionarioRequestDto requestDto,
                        HttpServletRequest request) {
                // Extrai token do cabeçalho Authorization
                String token = request.getHeader("Authorization");
                if (token == null || !token.startsWith("Bearer ")) {
                        throw new com.barbearia.domain.exceptions.AcessoNegadoException(
                                        "Token JWT não fornecido ou inválido");
                }

                // Remove prefixo "Bearer " do token
                token = token.substring(7);

                // Extrai ID da barbearia do token
                Object userIdObj = jwtService.extractClaim(token, "userId");
                if (userIdObj == null) {
                        throw new com.barbearia.domain.exceptions.AcessoNegadoException(
                                        "Token JWT inválido: userId não encontrado");
                }

                Long barbeariaId = ((Number) userIdObj).longValue();

                // Cria o funcionário
                FuncionarioResponseDto funcionarioCriado = funcionarioService.criarFuncionario(requestDto, barbeariaId);

                return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioCriado);
        }

        @Operation(summary = "Editar funcionário", description = "Atualiza dados de um funcionário da barbearia autenticada")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Funcionário atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioResponseDto.class), examples = @ExampleObject(name = "Sucesso", value = """
                                        {
                                          "id": 1,
                                          "nome": "João Silva",
                                          "email": "joao.silva@example.com",
                                          "telefone": "11987654321",
                                          "perfilType": "BARBEIRO",
                                          "ativo": true
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Erro de Validação", value = """
                                        {
                                          "timestamp": "2025-11-25T15:30:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "Email inválido",
                                          "path": "/api/barbearias/meus-funcionarios/1"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
                                        {
                                          "timestamp": "2025-11-25T15:30:00",
                                          "status": 401,
                                          "error": "Unauthorized",
                                          "message": "Token JWT inválido ou expirado",
                                          "path": "/api/barbearias/meus-funcionarios/1"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
                                        {
                                          "timestamp": "2025-11-25T15:30:00",
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Funcionário com ID 1 não encontrado",
                                          "path": "/api/barbearias/meus-funcionarios/1"
                                        }
                                        """)))
        })
        @PutMapping("/meus-funcionarios/{id}")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> editarFuncionario(
                        @PathVariable Long id,
                        @Valid @RequestBody FuncionarioRequestDto requestDto,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                FuncionarioResponseDto atualizado = funcionarioService.editarFuncionario(barbeariaId, id, requestDto);
                return ResponseEntity.ok(atualizado);
        }

        @Operation(summary = "Desativar funcionário", description = "Desativa um funcionário da barbearia (soft delete)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Funcionário desativado com sucesso", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Sucesso", value = "\"Funcionário desativado com sucesso\""))),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
                                        {
                                          "timestamp": "2025-11-25T15:30:00",
                                          "status": 401,
                                          "error": "Unauthorized",
                                          "message": "Token JWT inválido ou expirado",
                                          "path": "/api/barbearias/meus-funcionarios/1"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
                                        {
                                          "timestamp": "2025-11-25T15:30:00",
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Funcionário com ID 1 não encontrado",
                                          "path": "/api/barbearias/meus-funcionarios/1"
                                        }
                                        """)))
        })
        @DeleteMapping("/meus-funcionarios/{id}")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> desativarFuncionario(
                        @PathVariable Long id,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                return ResponseEntity.ok(funcionarioService.desativarFuncionario(barbeariaId, id));
        }

        @Operation(summary = "Associar serviços a um funcionário", description = "Define quais serviços um funcionário pode realizar. Substitui a lista anterior de serviços.")
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Lista de IDs dos serviços a serem associados", required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = "array", example = "[1, 2, 3]")))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Serviços associados com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class, type = "array"))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: serviço de outra barbearia)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Funcionário ou serviço não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/funcionarios/{id}/servicos")
        public ResponseEntity<List<Long>> associarServicos(
                        @Parameter(description = "ID do funcionário", example = "1") @PathVariable Long id,
                        @RequestBody List<Long> servicoIds,
                        HttpServletRequest request) {

                Long barbeariaId = extrairBarbeariaId(request);
                List<Long> associados = funcionarioService.associarServicos(barbeariaId, id, servicoIds);
                return ResponseEntity.ok(associados);
        }

        @Operation(summary = "Buscar agendamento por ID", description = "Retorna detalhes de um agendamento específico da barbearia")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Agendamento encontrado"),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 401,
                                          "error": "Unauthorized",
                                          "message": "Token JWT inválido ou expirado",
                                          "path": "/api/barbearias/agendamentos/1"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Agendamento com ID 1 não encontrado",
                                          "path": "/api/barbearias/agendamentos/1"
                                        }
                                        """)))
        })
        @GetMapping("/agendamentos/{id}")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> buscarAgendamento(
                        @PathVariable Long id,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                AgendamentoBarbeariaDto agendamento = agendamentoService.buscarAgendamentoPorIdParaBarbearia(
                                barbeariaId,
                                id);
                return ResponseEntity.ok(agendamento);
        }

        @Operation(summary = "Editar agendamento", description = "Reagenda um agendamento existente. Apenas agendamentos PENDENTE ou CONFIRMADO podem ser editados")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Agendamento reagendado com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos ou horário indisponível", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 401,
                                          "error": "Unauthorized",
                                          "message": "Token JWT inválido ou expirado",
                                          "path": "/api/barbearias/agendamentos/1/reagendar"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Agendamento com ID 1 não encontrado",
                                          "path": "/api/barbearias/agendamentos/1/reagendar"
                                        }
                                        """)))
        })
        @PutMapping("/agendamentos/{id}")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> editarAgendamento(
                        @PathVariable Long id,
                        @Valid @RequestBody AgendamentoReagendamentoDto dto,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                AgendamentoBarbeariaDto agendamentoAtual = agendamentoService
                                .buscarAgendamentoPorIdParaBarbearia(barbeariaId, id);
                Long clienteId = agendamentoAtual.clienteId();
                AgendamentoResponseDto atualizado = agendamentoService.reagendarAgendamento(id, dto.novaDataHora(),
                                clienteId, "BARBEARIA");
                return ResponseEntity.ok(atualizado);
        }

        @Operation(summary = "Métricas do dashboard", description = "Retorna métricas gerais: clientes únicos, agendamentos do mês, receita média e taxa de cancelamento")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Métricas retornadas com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 401,
                                          "error": "Unauthorized",
                                          "message": "Token JWT inválido ou expirado",
                                          "path": "/api/barbearias/dashboard/metricas"
                                        }
                                        """)))
        })
        @GetMapping("/dashboard/metricas")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> obterMetricasDashboard(HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                DashboardMetricasDto metricas = relatorioService.obterMetricasDashboard(barbeariaId);
                return ResponseEntity.ok(metricas);
        }

        @Operation(summary = "Serviços populares", description = "Retorna serviços mais agendados no período especificado")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 401,
                                          "error": "Unauthorized",
                                          "message": "Token JWT inválido ou expirado",
                                          "path": "/api/barbearias/relatorios/servicos-populares"
                                        }
                                        """)))
        })
        @GetMapping("/relatorios/servicos-populares")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> obterServicosPopulares(
                        @Parameter(description = "Período: MES | TRIMESTRE | ANO") @RequestParam(defaultValue = "MES") String periodo,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                java.util.List<ServicoPopularDto> servicos = relatorioService.obterServicosPopulares(barbeariaId,
                                periodo);
                return ResponseEntity.ok(servicos);
        }

        @Operation(summary = "Horários de pico", description = "Retorna distribuição de agendamentos por faixa horária (últimos 30 dias)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
                                        {
                                          "timestamp": "2025-11-25T16:00:00",
                                          "status": 401,
                                          "error": "Unauthorized",
                                          "message": "Token JWT inválido ou expirado",
                                          "path": "/api/barbearias/relatorios/horarios-pico"
                                        }
                                        """)))
        })
        @GetMapping("/relatorios/horarios-pico")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> obterHorariosPico(HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                java.util.List<HorarioPicoDto> horarios = relatorioService.obterHorariosPico(barbeariaId);
                return ResponseEntity.ok(horarios);
        }

        @Operation(summary = "Listar agendamentos da barbearia", description = "Retorna todos os agendamentos da barbearia autenticada. "
                        +
                        "Permite filtrar por data específica (opcional).")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AgendamentoBarbeariaDto.class)), examples = @ExampleObject(name = "Lista de Agendamentos", value = "[{\"id\":1,\"clienteNome\":\"Pedro Oliveira\",\"servicoNome\":\"Corte Masculino\",\"funcionarioNome\":\"João Silva\",\"data\":\"2024-11-25\",\"horarioInicio\":\"14:00\",\"horarioFim\":\"14:30\",\"status\":\"CONFIRMADO\",\"valorTotal\":30.0},{\"id\":2,\"clienteNome\":\"Ana Costa\",\"servicoNome\":\"Manicure\",\"funcionarioNome\":\"Maria Santos\",\"data\":\"2024-11-25\",\"horarioInicio\":\"15:00\",\"horarioFim\":\"16:00\",\"status\":\"PENDENTE\",\"valorTotal\":40.0}]"))),
                        @ApiResponse(responseCode = "400", description = "Formato de data inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(value = """
                                        {
                                          "timestamp": "2025-11-25T14:00:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "Formato de data inválido. Use yyyy-MM-dd",
                                          "path": "/api/barbearias/meus-agendamentos"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/meus-agendamentos")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> listarMeusAgendamentos(
                        @Parameter(description = "Data para filtrar agendamentos (formato: yyyy-MM-dd)", required = false, example = "2024-11-25") @RequestParam(required = false) String data,
                        HttpServletRequest request) {
                // Extrai token do cabeçalho Authorization
                String token = request.getHeader("Authorization");
                if (token == null || !token.startsWith("Bearer ")) {
                        throw new com.barbearia.domain.exceptions.AcessoNegadoException(
                                        "Token JWT não fornecido ou inválido");
                }

                // Remove prefixo "Bearer " do token
                token = token.substring(7);

                // Extrai ID da barbearia do token
                Object userIdObj = jwtService.extractClaim(token, "userId");
                if (userIdObj == null) {
                        throw new com.barbearia.domain.exceptions.AcessoNegadoException(
                                        "Token JWT inválido: userId não encontrado");
                }

                Long barbeariaId = ((Number) userIdObj).longValue();

                // Parse da data se fornecida
                LocalDate dataFiltro = null;
                if (data != null && !data.isBlank()) {
                        try {
                                dataFiltro = LocalDate.parse(data);
                        } catch (Exception e) {
                                throw new IllegalArgumentException("Formato de data inválido. Use yyyy-MM-dd");
                        }
                }

                // Lista agendamentos
                List<AgendamentoBarbeariaDto> agendamentos = agendamentoService.listarAgendamentosBarbearia(barbeariaId,
                                dataFiltro);

                return ResponseEntity.ok(agendamentos);
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
                                        "DIA", "SEMANA",
                                        "MES" })) @RequestParam(defaultValue = "MES") PeriodoRelatorio periodo,
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
                        RelatorioComissoesDto relatorio = comissaoService.gerarRelatorioComissoes(barbeariaId,
                                        dataInicio, dataFim);
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

        /**
         * Criar agendamento direto (sem cliente ter conta).
         */
        @Operation(summary = "Criar agendamento direto", description = "Barbearia agenda cliente sem que ele precise ter conta. "
                        +
                        "Se o telefone já existir, reutiliza o cliente. Caso contrário, cria novo cliente automaticamente.", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Agendamento Direto", value = """
                                        {
                                          "clienteNome": "João Silva",
                                          "clienteTelefone": "87999998888",
                                          "clienteEmail": "joao@email.com",
                                          "servicoId": 1,
                                          "funcionarioId": 1,
                                          "dataHora": "2025-11-25T14:30:00",
                                          "observacoes": "Cliente preferencial"
                                        }
                                        """))))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Agendamento criado", content = @Content(schema = @Schema(implementation = AgendamentoResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos ou horário indisponível"),
                        @ApiResponse(responseCode = "422", description = "Funcionário não pertence à barbearia"),
                        @ApiResponse(responseCode = "401", description = "Token inválido")
        })
        @PostMapping("/agendamentos-diretos")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> criarAgendamentoDireto(
                        @org.springframework.web.bind.annotation.RequestBody com.barbearia.application.dto.AgendamentoDiretoRequestDto requestDto,
                        HttpServletRequest request) {
                try {
                        Long barbeariaId = extrairBarbeariaId(request);

                        if (barbeariaId == null) {
                                return ResponseEntity.status(401)
                                                .body("Token JWT inválido ou barbeariaId não encontrado");
                        }

                        AgendamentoResponseDto agendamento = agendamentoService.criarAgendamentoDireto(barbeariaId,
                                        requestDto);

                        return ResponseEntity.status(201).body(agendamento);

                } catch (IllegalArgumentException e) {
                        if (e.getMessage().contains("não pertence")) {
                                return ResponseEntity.status(422).body(e.getMessage());
                        }
                        return ResponseEntity.badRequest().body(e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.internalServerError()
                                        .body("Erro ao criar agendamento direto: " + e.getMessage());
                }
        }

        /**
         * Método auxiliar para extrair ID da barbearia do token JWT.
         */

        // ===== ENDPOINTS Para EXCEÇÕES DE HORÁRIO =====

        @Operation(summary = "Criar exceção de horário para funcionário", description = "Barbearia pode adicionar disponibilidade extra em data específica para um funcionário")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Exceção criada com sucesso"),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos ou já existe exceção nesta data"),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado")
        })
        @PostMapping("/funcionarios/{funcionarioId}/excecoes")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> criarExcecaoFuncionario(
                        @PathVariable Long funcionarioId,
                        @Valid @RequestBody HorarioExcecaoRequestDto requestDto,
                        HttpServletRequest request) {
                try {
                        extrairBarbeariaId(request);

                        HorarioExcecaoResponseDto excecao = horarioGestaoService.criarExcecao(
                                        funcionarioId,
                                        requestDto,
                                        "BARBEARIA");

                        return ResponseEntity.status(HttpStatus.CREATED).body(excecao);
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().body("Erro ao criar exceção: " + e.getMessage());
                }
        }

        @Operation(summary = "Listar exceções de horário do funcionário", description = "Lista exceções (disponibilidade extra) de um funcion...ário específico")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido")
        })
        @GetMapping("/funcionarios/{funcionarioId}/excecoes")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> listarExcecoesFuncionario(
                        @PathVariable Long funcionarioId,
                        @RequestParam(required = false) String dataInicio,
                        @RequestParam(required = false) String dataFim,
                        HttpServletRequest request) {
                try {
                        extrairBarbeariaId(request);

                        LocalDate inicio = dataInicio != null ? LocalDate.parse(dataInicio) : LocalDate.now();
                        LocalDate fim = dataFim != null ? LocalDate.parse(dataFim) : LocalDate.now().plusMonths(3);

                        List<HorarioExcecaoResponseDto> excecoes = horarioGestaoService.listarExcecoesPorPeriodo(
                                        funcionarioId, inicio, fim);

                        return ResponseEntity.ok(excecoes);
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().body("Erro ao listar exceções: " + e.getMessage());
                }
        }

        @Operation(summary = "Remover exceção de horário", description = "Barbearia pode remover qualquer exceção (criada por ela ou pelo profissional)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Exceção removida com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido"),
                        @ApiResponse(responseCode = "404", description = "Exceção não encontrada")
        })
        @DeleteMapping("/funcionarios/{funcionarioId}/excecoes/{excecaoId}")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> removerExcecaoFuncionario(
                        @PathVariable Long funcionarioId,
                        @PathVariable Long excecaoId,
                        HttpServletRequest request) {
                try {
                        extrairBarbeariaId(request);

                        horarioGestaoService.removerExcecao(excecaoId, funcionarioId, "BARBEARIA");

                        return ResponseEntity.noContent().build();
                } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                } catch (Exception e) {
                        return ResponseEntity.internalServerError().body("Erro ao remover exceção: " + e.getMessage());
                }
        }

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
