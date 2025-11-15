package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.BarbeariaListItemDto;
import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.dto.HorarioDisponivelDto;
import com.barbearia.application.services.BarbeariaService;
import com.barbearia.application.services.HorarioService;
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
    
    public BarbeariaController(BarbeariaService barbeariaService, HorarioService horarioService) {
        this.barbeariaService = barbeariaService;
        this.horarioService = horarioService;
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
}
