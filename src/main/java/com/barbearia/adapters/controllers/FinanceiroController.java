package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.*;
import com.barbearia.application.services.ComissaoService;
import com.barbearia.application.services.FinanceiroService;
import com.barbearia.application.services.RelatorioService;
import com.barbearia.application.security.JwtService;
import com.barbearia.domain.enums.PeriodoRelatorio;
import com.barbearia.infrastructure.persistence.entities.JpaDespesa;
import com.barbearia.infrastructure.persistence.entities.JpaReceita;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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

import java.time.LocalDate;
import java.util.List;

/**
 * Controller responsável pela gestão financeira da barbearia.
 * <p>
 * Inclui:
 * - Relatórios financeiros
 * - Gestão de receitas extras
 * - Gestão de despesas
 * - Relatórios de comissões
 * - Métricas do dashboard
 * </p>
 */
@Tag(name = "Financeiro", description = "Gestão financeira completa: receitas, despesas, relatórios e comissões")
@RestController
@RequestMapping("/api/financeiro")
@CrossOrigin(origins = "*")
public class FinanceiroController {

        private final FinanceiroService financeiroService;
        private final ComissaoService comissaoService;
        private final RelatorioService relatorioService;
        private final JwtService jwtService;

        public FinanceiroController(FinanceiroService financeiroService,
                        ComissaoService comissaoService,
                        RelatorioService relatorioService,
                        JwtService jwtService) {
                this.financeiroService = financeiroService;
                this.comissaoService = comissaoService;
                this.relatorioService = relatorioService;
                this.jwtService = jwtService;
        }

        // ==================== RELATÓRIOS GERAIS ====================

        @Operation(summary = "Obter relatório financeiro", description = "Gera relatório financeiro da barbearia para o período selecionado.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RelatorioFinanceiroDto.class))),
                        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/relatorios/geral")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> obterRelatorioFinanceiro(
                        @Parameter(description = "Período do relatório") @RequestParam(defaultValue = "MES") PeriodoRelatorio periodo,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                return ResponseEntity.ok(financeiroService.gerarRelatorioFinanceiro(barbeariaId, periodo));
        }

        @Operation(summary = "Gerar relatório de comissões", description = "Gera relatório detalhado de comissões dos profissionais.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RelatorioComissoesDto.class))),
                        @ApiResponse(responseCode = "400", description = "Datas inválidas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/relatorios/comissoes")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> gerarRelatorioComissoes(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                return ResponseEntity.ok(comissaoService.gerarRelatorioComissoes(barbeariaId, dataInicio, dataFim));
        }

        @Operation(summary = "Métricas do dashboard", description = "Retorna métricas gerais para o dashboard.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Métricas retornadas com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DashboardMetricasDto.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/dashboard/metricas")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> obterMetricasDashboard(HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                return ResponseEntity.ok(relatorioService.obterMetricasDashboard(barbeariaId));
        }

        // ==================== RECEITAS EXTRAS ====================

        @Operation(summary = "Adicionar receita extra", description = "Registra uma nova receita extra (venda de produto, etc).")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Receita criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JpaReceita.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: valor negativo, categoria nula, data futura)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado (Token JWT ausente, inválido ou expirado)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/receitas")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<JpaReceita> adicionarReceita(
                        @Valid @RequestBody ReceitaExtraRequestDto dto,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(financeiroService.adicionarReceita(barbeariaId, dto));
        }

        @Operation(summary = "Listar receitas", description = "Lista receitas extras com filtro opcional de data.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = JpaReceita.class)))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado (Token JWT ausente, inválido ou expirado)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/receitas")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<List<JpaReceita>> listarReceitas(
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                return ResponseEntity.ok(financeiroService.listarReceitas(barbeariaId, inicio, fim));
        }

        @Operation(summary = "Editar receita", description = "Atualiza uma receita existente.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Receita atualizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JpaReceita.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: valor negativo) ou ID da URL não corresponde ao corpo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado (Token JWT ausente, inválido ou expirado)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Receita não encontrada para esta barbearia", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PutMapping("/receitas/{id}")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<JpaReceita> editarReceita(
                        @PathVariable Long id,
                        @Valid @RequestBody ReceitaExtraRequestDto dto,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                return ResponseEntity.ok(financeiroService.editarReceita(barbeariaId, id, dto));
        }

        @Operation(summary = "Remover receita", description = "Remove uma receita do sistema.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Receita removida com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado (Token JWT ausente, inválido ou expirado)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Receita não encontrada para esta barbearia", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @DeleteMapping("/receitas/{id}")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<Void> removerReceita(
                        @PathVariable Long id,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                financeiroService.removerReceita(barbeariaId, id);
                return ResponseEntity.noContent().build();
        }

        // ==================== DESPESAS ====================

        @Operation(summary = "Adicionar despesa", description = "Registra uma nova despesa.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Despesa criada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JpaDespesa.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: valor negativo, categoria nula, data futura)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado (Token JWT ausente, inválido ou expirado)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/despesas")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<JpaDespesa> adicionarDespesa(
                        @Valid @RequestBody DespesaRequestDto dto,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(financeiroService.adicionarDespesa(barbeariaId, dto));
        }

        @Operation(summary = "Listar despesas", description = "Lista despesas com filtro opcional de data.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = JpaDespesa.class)))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado (Token JWT ausente, inválido ou expirado)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/despesas")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<List<JpaDespesa>> listarDespesas(
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                return ResponseEntity.ok(financeiroService.listarDespesas(barbeariaId, inicio, fim));
        }

        @Operation(summary = "Editar despesa", description = "Atualiza uma despesa existente.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Despesa atualizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JpaDespesa.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: valor negativo) ou ID da URL não corresponde ao corpo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "401", description = "Não autorizado (Token JWT ausente, inválido ou expirado)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Despesa não encontrada para esta barbearia", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PutMapping("/despesas/{id}")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<JpaDespesa> editarDespesa(
                        @PathVariable Long id,
                        @Valid @RequestBody DespesaRequestDto dto,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                return ResponseEntity.ok(financeiroService.editarDespesa(barbeariaId, id, dto));
        }

        @Operation(summary = "Remover despesa", description = "Remove uma despesa do sistema.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Despesa removida com sucesso"),
                        @ApiResponse(responseCode = "401", description = "Não autorizado (Token JWT ausente, inválido ou expirado)", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Despesa não encontrada para esta barbearia", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @DeleteMapping("/despesas/{id}")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<Void> removerDespesa(
                        @PathVariable Long id,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                financeiroService.removerDespesa(barbeariaId, id);
                return ResponseEntity.noContent().build();
        }

        // Helper
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
