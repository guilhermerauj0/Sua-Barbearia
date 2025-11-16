package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.BarbeariaListItemDto;
import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.dto.HorarioDisponivelDto;
import com.barbearia.application.dto.HorarioFuncionamentoRequestDto;
import com.barbearia.application.dto.HorarioFuncionamentoResponseDto;
import com.barbearia.application.services.BarbeariaService;
import com.barbearia.application.services.HorarioService;
import com.barbearia.application.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final JwtService jwtService;
    
    public BarbeariaController(BarbeariaService barbeariaService, 
                              HorarioService horarioService,
                              JwtService jwtService) {
        this.barbeariaService = barbeariaService;
        this.horarioService = horarioService;
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
}
