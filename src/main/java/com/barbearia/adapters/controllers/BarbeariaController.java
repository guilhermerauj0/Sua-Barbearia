package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.BarbeariaListItemDto;
import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.services.BarbeariaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    
    public BarbeariaController(BarbeariaService barbeariaService) {
        this.barbeariaService = barbeariaService;
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
}
