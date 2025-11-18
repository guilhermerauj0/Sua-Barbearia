package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.BarbeariaListItemDto;
import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.dto.HorarioDisponivelDto;
import com.barbearia.application.dto.HorarioFuncionamentoRequestDto;
import com.barbearia.application.dto.HorarioFuncionamentoResponseDto;
import com.barbearia.application.dto.FuncionarioRequestDto;
import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.application.dto.AgendamentoBarbeariaDto;
import com.barbearia.application.dto.AgendamentoResponseDto;
import com.barbearia.application.dto.AgendamentoUpdateDto;
import com.barbearia.application.dto.RelatorioFinanceiroDto;
import com.barbearia.application.services.BarbeariaService;
import com.barbearia.application.services.HorarioService;
import com.barbearia.application.services.FuncionarioService;
import com.barbearia.application.services.AgendamentoService;
import com.barbearia.application.services.FinanceiroService;
import com.barbearia.application.security.JwtService;
import com.barbearia.domain.enums.PeriodoRelatorio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller REST para operações de leitura de barbearias e serviços.
 * 
 * Endpoints:
 * - GET /api/barbearias - Lista todas as barbearias ativas
 * - GET /api/barbearias/{id}/servicos - Lista serviços de uma barbearia
 * 
 * Todos os endpoints são públicos (não requerem autenticação).
 * 
 * @author Sua Barbearia Team
 */
@RestController
@RequestMapping("/api/barbearias")
@CrossOrigin(origins = "*")
public class BarbeariaController {
    
    private final BarbeariaService barbeariaService;
    private final HorarioService horarioService;
    private final FuncionarioService funcionarioService;
    private final AgendamentoService agendamentoService;
    private final FinanceiroService financeiroService;
    private final JwtService jwtService;
    
    public BarbeariaController(BarbeariaService barbeariaService, 
                              HorarioService horarioService,
                              FuncionarioService funcionarioService,
                              AgendamentoService agendamentoService,
                              FinanceiroService financeiroService,
                              JwtService jwtService) {
        this.barbeariaService = barbeariaService;
        this.horarioService = horarioService;
        this.funcionarioService = funcionarioService;
        this.agendamentoService = agendamentoService;
        this.financeiroService = financeiroService;
        this.jwtService = jwtService;
    }
    
    /**
     * Lista todas as barbearias ativas do sistema.
     * 
     * Retorna 200 (OK) com lista de barbearias.
     * Retorna 500 (Internal Server Error) em caso de erro.
     * 
     * Informações retornadas:
     * - ID, Nome, Nome Fantasia, Endereço, Telefone, Email
     * - Avaliação média (preparado para futuro)
     * 
     * @return Lista de barbearias ativas
     */
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
    
    /**
     * Lista todos os serviços ativos de uma barbearia específica.
     * 
     * Retorna 200 (OK) com lista de serviços.
     * Retorna 404 (Not Found) se a barbearia não existe.
     * Retorna 400 (Bad Request) se a barbearia está inativa.
     * Retorna 500 (Internal Server Error) em caso de erro.
     * 
     * Informações retornadas:
     * - ID, Nome, Descrição, Preço, Duração (em minutos), Status ativo
     * 
     * @param id ID da barbearia
     * @return Lista de serviços da barbearia
     */
    @GetMapping("/{id}/servicos")
    public ResponseEntity<?> listarServicosPorBarbearia(@PathVariable Long id) {
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
    
    /**
     * Obtém horários disponíveis para agendamento em uma barbearia.
     * 
     * Retorna 200 (OK) com lista de horários disponíveis.
     * Retorna 404 (Not Found) se a barbearia ou serviço não existe.
     * Retorna 400 (Bad Request) se os parâmetros são inválidos.
     * Retorna 500 (Internal Server Error) em caso de erro.
     * 
     * Parâmetros de query:
     * - servicoId: ID do serviço desejado (obrigatório)
     * - data: Data para consultar (formato: yyyy-MM-dd, obrigatório)
     * 
     * Informações retornadas para cada horário disponível:
     * - funcionarioId: ID do profissional disponível
     * - funcionarioNome: Nome do profissional
     * - profissao: Tipo de profissional (BARBEIRO, MANICURE, ESTETICISTA, COLORISTA)
     * - data: Data do agendamento
     * - horarioInicio: Hora de início do slot
     * - horarioFim: Hora de fim do slot
     * 
     * @param id ID da barbearia
     * @param servicoId ID do serviço desejado
     * @param dataStr Data em formato yyyy-MM-dd
     * @return Lista de horários disponíveis
     */
    @GetMapping("/{id}/horarios-disponiveis")
    public ResponseEntity<?> obterHorariosDisponiveis(
            @PathVariable Long id,
            @RequestParam Long servicoId,
            @RequestParam String dataStr) {
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
            List<HorarioDisponivelDto> horariosDisponiveis = horarioService.obterHorariosDisponiveis(id, servicoId, data);
            
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
     * - 401 (Unauthorized) se token inválido/ausente
     * - 403 (Forbidden) se não for role BARBEARIA
     * - 500 (Internal Server Error) em caso de erro
     * 
     * @param requestDto Dados do horário a ser criado
     * @param request Requisição HTTP (para extrair token JWT)
     * @return DTO com dados do horário criado/atualizado
     */
    @PostMapping("/horarios")
    public ResponseEntity<?> criarHorarioFuncionamento(
            @RequestBody HorarioFuncionamentoRequestDto requestDto,
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
            
            // Cria o horário de funcionamento
            HorarioFuncionamentoResponseDto horarioCriado = 
                barbeariaService.criarHorarioFuncionamento(barbeariaId, requestDto);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(horarioCriado);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao criar horário de funcionamento: " + e.getMessage());
        }
    }
    
    /**
     * Lista todos os funcionários ativos da barbearia autenticada.
     * 
     * Segurança:
     * - Requer autenticação JWT (Bearer token)
     * - Apenas role BARBEARIA pode acessar
     * - BarbeariaId é extraído do token (não pode listar funcionários de outras barbearias)
     * 
     * Retorna:
     * - 200 (OK) com lista de funcionários ativos
     * - 401 (Unauthorized) se token inválido/ausente
     * - 403 (Forbidden) se não for role BARBEARIA
     * - 500 (Internal Server Error) em caso de erro
     * 
     * Informações retornadas para cada funcionário:
     * - id, barbeariaId, nome, email, telefone, profissao, ativo, dataCriacao, dataAtualizacao
     * 
     * @param request Requisição HTTP (para extrair token JWT)
     * @return Lista de funcionários ativos da barbearia
     */
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
            List<FuncionarioResponseDto> funcionarios = 
                funcionarioService.listarFuncionariosDaBarbearia(barbeariaId);
            
            return ResponseEntity.ok(funcionarios);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao listar funcionários: " + e.getMessage());
        }
    }
    
    /**
     * Cria um novo funcionário para a barbearia autenticada.
     * 
     * Segurança:
     * - Requer autenticação JWT (Bearer token)
     * - Apenas role BARBEARIA pode acessar
     * - BarbeariaId é extraído do token (não pode criar funcionários para outras barbearias)
     * 
     * Validações:
     * - nome: obrigatório, 3-100 caracteres
     * - email: obrigatório, formato válido, único por barbearia
     * - telefone: obrigatório, 10-20 caracteres
     * - profissao: obrigatório, valores permitidos: BARBEIRO, MANICURE, ESTETICISTA, COLORISTA
     * 
     * Retorna:
     * - 201 (Created) com dados do funcionário criado
     * - 400 (Bad Request) se dados inválidos ou email duplicado
     * - 401 (Unauthorized) se token inválido/ausente
     * - 403 (Forbidden) se não for role BARBEARIA
     * - 500 (Internal Server Error) em caso de erro
     * 
     * @param requestDto Dados do funcionário a ser criado
     * @param request Requisição HTTP (para extrair token JWT)
     * @return DTO com dados do funcionário criado
     */
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
            FuncionarioResponseDto funcionarioCriado = 
                funcionarioService.criarFuncionario(requestDto, barbeariaId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioCriado);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao criar funcionário: " + e.getMessage());
        }
    }
    
    /**
     * Lista agendamentos da barbearia autenticada, com filtro opcional por data.
     * 
     * Segurança:
     * - Requer autenticação JWT (Bearer token)
     * - Apenas role BARBEARIA pode acessar
     * - BarbeariaId é extraído do token
     * 
     * Parâmetros:
     * - data (opcional): Filtrar agendamentos por data específica (formato: yyyy-MM-dd)
     * 
     * Retorna:
     * - 200 (OK) com lista de agendamentos detalhados
     * - 400 (Bad Request) se formato de data inválido
     * - 401 (Unauthorized) se token inválido/ausente
     * - 403 (Forbidden) se não for role BARBEARIA
     * - 500 (Internal Server Error) em caso de erro
     * 
     * @param data Data para filtrar (opcional, formato: yyyy-MM-dd)
     * @param request Requisição HTTP (para extrair token JWT)
     * @return Lista de agendamentos com dados de cliente, serviço e funcionário
     */
    @GetMapping("/meus-agendamentos")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> listarMeusAgendamentos(
            @RequestParam(required = false) String data,
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
            List<AgendamentoBarbeariaDto> agendamentos = 
                agendamentoService.listarAgendamentosBarbearia(barbeariaId, dataFiltro);
            
            return ResponseEntity.ok(agendamentos);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao listar agendamentos: " + e.getMessage());
        }
    }
    
    /**
     * Atualiza o status de um agendamento da barbearia autenticada.
     * 
     * Segurança:
     * - Requer autenticação JWT (Bearer token)
     * - Apenas role BARBEARIA pode acessar
     * - BarbeariaId é extraído do token
     * - Validação: agendamento deve pertencer à barbearia
     * 
     * Validações:
     * - Agendamento deve existir
     * - Agendamento deve pertencer à barbearia autenticada
     * - Transições de status devem ser válidas
     * - Operação é idempotente (mesmo status não gera erro)
     * 
     * Regras de transição:
     * - Não pode confirmar agendamento cancelado
     * - Não pode cancelar agendamento concluído
     * - Pode sempre marcar como concluído
     * 
     * Retorna:
     * - 200 (OK) com dados atualizados do agendamento
     * - 400 (Bad Request) se transição de status inválida
     * - 401 (Unauthorized) se token inválido/ausente
     * - 403 (Forbidden) se não for role BARBEARIA ou agendamento não pertence à barbearia
     * - 404 (Not Found) se agendamento não existe
     * - 500 (Internal Server Error) em caso de erro
     * 
     * @param id ID do agendamento
     * @param updateDto DTO com novo status
     * @param request Requisição HTTP (para extrair token JWT)
     * @return DTO com dados atualizados do agendamento
     */
    @PatchMapping("/agendamentos/{id}")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> atualizarStatusAgendamento(
            @PathVariable Long id,
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
            AgendamentoResponseDto agendamentoAtualizado = 
                agendamentoService.atualizarStatusAgendamento(id, barbeariaId, updateDto);
            
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
    
    /**
     * Obtém relatório financeiro da barbearia autenticada por período.
     * 
     * <p>Endpoint protegido: apenas barbearias podem acessar.</p>
     * 
     * <p>Períodos suportados:</p>
     * <ul>
     *   <li><b>DIA:</b> Últimas 24 horas</li>
     *   <li><b>SEMANA:</b> Últimos 7 dias</li>
     *   <li><b>MES:</b> Últimos 30 dias</li>
     * </ul>
     * 
     * <p>O relatório inclui:</p>
     * <ul>
     *   <li>Faturamento total do período</li>
     *   <li>Total de agendamentos concluídos</li>
     *   <li>Ticket médio</li>
     *   <li>Faturamento médio por dia</li>
     *   <li>Top 5 serviços mais rentáveis</li>
     * </ul>
     * 
     * @param periodo Período do relatório (DIA, SEMANA, MES)
     * @param request Request HTTP para extração do JWT
     * @return 200 (OK) com relatório financeiro
     *         400 (BAD REQUEST) se período for inválido
     *         401 (UNAUTHORIZED) se não autenticado
     *         403 (FORBIDDEN) se não for barbearia
     *         500 (INTERNAL SERVER ERROR) em caso de erro
     */
    @GetMapping("/gestao-financeira")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> obterRelatorioFinanceiro(
            @RequestParam(defaultValue = "MES") PeriodoRelatorio periodo,
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
}
