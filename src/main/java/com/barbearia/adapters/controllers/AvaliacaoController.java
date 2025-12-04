package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AvaliacaoRequestDto;
import com.barbearia.application.dto.AvaliacaoResponseDto;
import com.barbearia.application.dto.EstatisticasAvaliacoesDto;
import com.barbearia.application.services.AvaliacaoService;
import com.barbearia.application.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gerenciamento de Avaliações.
 * 
 * Funcionalidades:
 * - Clientes avaliam barbearias (pós-agendamento)
 * - Consulta pública de avaliações e estatísticas
 */
@Tag(name = "Avaliações", description = "Sistema de avaliações multi-aspecto de barbearias")
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;
    private final JwtService jwtService;

    public AvaliacaoController(AvaliacaoService avaliacaoService, JwtService jwtService) {
        this.avaliacaoService = avaliacaoService;
        this.jwtService = jwtService;
    }

    /**
     * Cliente cria avaliação de uma barbearia.
     * Requer agendamento CONCLUÍDO.
     */
    @Operation(summary = "Criar avaliação", description = "Cliente avalia barbearia com 4 notas (serviço, ambiente, limpeza, atendimento) + comentário. Agendamento deve estar CONCLUÍDO.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvaliacaoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Agendamento já avaliado ou não concluído", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Agendamento já avaliado",
                      "path": "/api/avaliacoes"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @PostMapping("/avaliacoes")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<?> criarAvaliacao(
            @Valid @RequestBody AvaliacaoRequestDto requestDto,
            HttpServletRequest request) {
        // Extrai cliente ID do token
        String token = extrairToken(request);
        Object userIdObj = jwtService.extractClaim(token, "userId");
        Long clienteId = ((Number) userIdObj).longValue();

        AvaliacaoResponseDto avaliacao = avaliacaoService.criarAvaliacao(clienteId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacao);
    }

    /**
     * Lista avaliações públicas de uma barbearia.
     */
    @Operation(summary = "Listar avaliações", description = "Retorna todas as avaliações de uma barbearia (públicas, sem autenticação)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de avaliações", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvaliacaoResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @GetMapping("/barbearias/{barbeariaId}/avaliacoes")
    public ResponseEntity<?> listarAvaliacoes(@PathVariable Long barbeariaId) {
        List<AvaliacaoResponseDto> avaliacoes = avaliacaoService.buscarAvaliacoesPorBarbearia(barbeariaId);
        return ResponseEntity.ok(avaliacoes);
    }

    /**
     * Estatísticas públicas de avaliações.
     */
    @Operation(summary = "Estatísticas de avaliações", description = "Retorna médias por aspecto, total de avaliações e distribuição 1-5 estrelas (público)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estatísticas calculadas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EstatisticasAvaliacoesDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @GetMapping("/barbearias/{barbeariaId}/estatisticas-avaliacoes")
    public ResponseEntity<?> obterEstatisticas(@PathVariable Long barbeariaId) {
        EstatisticasAvaliacoesDto stats = avaliacaoService.calcularEstatisticas(barbeariaId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Verifica se um agendamento já foi avaliado.
     */
    @Operation(summary = "Verificar avaliação", description = "Verifica se um agendamento específico já foi avaliado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retornado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @GetMapping("/avaliacoes/verificar/{agendamentoId}")
    public ResponseEntity<Boolean> verificarAvaliacao(@PathVariable Long agendamentoId) {
        boolean avaliado = avaliacaoService.verificarSeAgendamentoAvaliado(agendamentoId);
        return ResponseEntity.ok(avaliado);
    }

    // Helper method

    private String extrairToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token JWT não fornecido");
        }
        return authHeader.substring(7);
    }
}
