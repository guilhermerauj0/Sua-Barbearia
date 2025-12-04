package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ApiErrorDto;
import com.barbearia.application.dto.HorarioBloqueadoLoteRequestDto;
import com.barbearia.application.dto.HorarioBloqueadoRequestDto;
import com.barbearia.application.dto.HorarioBloqueadoResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.HorarioBloqueioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
 * Controller para gestão de bloqueios de horário (férias, folgas, etc).
 */
@Tag(name = "Bloqueios de Horário", description = "Gestão de férias, folgas e bloqueios de agenda")
@RestController
@RequestMapping("/api/barbearias/{barbeariaId}/profissionais/{funcionarioId}/bloqueios")
@CrossOrigin(origins = "*")
public class HorarioBloqueioController {

    private final HorarioBloqueioService horarioBloqueioService;
    private final JwtService jwtService;

    public HorarioBloqueioController(HorarioBloqueioService horarioBloqueioService, JwtService jwtService) {
        this.horarioBloqueioService = horarioBloqueioService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Criar bloqueio", description = "Bloqueia um horário específico para um funcionário", security = @SecurityRequirement(name = "Bearer"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Bloqueio Específico", value = """
            {
              "data": "2025-12-25",
              "horarioInicio": "14:00",
              "horarioFim": "16:00",
              "motivo": "Consulta médica"
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bloqueio criado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HorarioBloqueadoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou conflito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:30:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Conflito de horário",
                      "path": "/api/barbearias/1/funcionarios/2/bloqueios"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> criarBloqueio(
            @PathVariable Long barbeariaId,
            @PathVariable Long funcionarioId,
            @Valid @RequestBody HorarioBloqueadoRequestDto requestDto,
            HttpServletRequest request) {
        Long usuarioId = extrairBarbeariaIdDoToken(request);
        if (usuarioId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorDto(java.time.LocalDateTime.now(),
                    401, "Unauthorized", "Token inválido ou ausente", request.getRequestURI()));
        }
        if (!usuarioId.equals(barbeariaId)) {
            throw new AcessoNegadoException("Você não tem permissão para gerenciar esta barbearia");
        }

        HorarioBloqueadoResponseDto bloqueio = horarioBloqueioService.bloquearHorario(barbeariaId, funcionarioId,
                requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(bloqueio);
    }

    @Operation(summary = "Bloquear dia completo", description = "Bloqueia o dia inteiro para um funcionário", security = @SecurityRequirement(name = "Bearer"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Dia Completo", value = """
            {
              "data": "2025-12-25",
              "motivo": "Natal - Feriado"
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dia bloqueado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HorarioBloqueadoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @PostMapping("/dia-completo")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> bloquearDiaCompleto(
            @PathVariable Long barbeariaId,
            @PathVariable Long funcionarioId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam String motivo,
            HttpServletRequest request) {
        Long usuarioId = extrairBarbeariaIdDoToken(request);
        if (!usuarioId.equals(barbeariaId)) {
            throw new AcessoNegadoException("Você não tem permissão para gerenciar esta barbearia");
        }

        HorarioBloqueadoResponseDto bloqueio = horarioBloqueioService.bloquearDiaCompleto(barbeariaId, funcionarioId,
                data, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(bloqueio);
    }

    @Operation(summary = "Bloquear múltiplos dias (lote)", description = "Bloqueia múltiplos dias consecutivos de um profissional", security = @SecurityRequirement(name = "Bearer"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Férias", value = """
            {
              "dataInicio": "2025-12-20",
              "dataFim": "2025-12-31",
              "motivo": "Férias de fim de ano"
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bloqueios criados com sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HorarioBloqueadoResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @PostMapping("/lote")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> bloquearLote(
            @PathVariable Long barbeariaId,
            @PathVariable Long funcionarioId,
            @RequestBody HorarioBloqueadoLoteRequestDto requestDto,
            HttpServletRequest request) {
        Long usuarioId = extrairBarbeariaIdDoToken(request);
        if (!usuarioId.equals(barbeariaId)) {
            throw new AcessoNegadoException("Você não tem permissão para gerenciar esta barbearia");
        }

        List<HorarioBloqueadoResponseDto> bloqueios = horarioBloqueioService.bloquearHorariosLote(
                barbeariaId, funcionarioId, requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(bloqueios);
    }

    @Operation(summary = "Listar bloqueios", description = "Lista bloqueios de um funcionário em um período", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de bloqueios", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HorarioBloqueadoResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Datas inválidas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('BARBEARIA', 'BARBEIRO', 'CLIENTE')")
    public ResponseEntity<List<HorarioBloqueadoResponseDto>> listarBloqueios(
            @PathVariable Long barbeariaId,
            @PathVariable Long funcionarioId,
            @Parameter(description = "Data inicial (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final (YYYY-MM-DD)") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<HorarioBloqueadoResponseDto> bloqueios;

        if (dataInicio != null && dataFim != null) {
            bloqueios = horarioBloqueioService.listarBloqueiosPorPeriodo(
                    barbeariaId, funcionarioId, dataInicio, dataFim);
        } else {
            bloqueios = horarioBloqueioService.listarBloqueios(barbeariaId, funcionarioId);
        }
        return ResponseEntity.ok(bloqueios);
    }

    @Operation(summary = "Remover bloqueio", description = "Remove um bloqueio específico", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Bloqueio removido"),
            @ApiResponse(responseCode = "404", description = "Bloqueio não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
    })
    @DeleteMapping("/{bloqueioId}")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> removerBloqueio(
            @PathVariable Long barbeariaId,
            @PathVariable Long funcionarioId,
            @PathVariable Long bloqueioId) {
        // Optional: Add a check here if the barbeariaId from token matches the path
        // variable barbeariaId
        // Long usuarioId = extrairBarbeariaIdDoToken(request);
        // if (!usuarioId.equals(barbeariaId)) {
        // throw new AcessoNegadoException("Você não tem permissão para gerenciar esta
        // barbearia");
        // }
        horarioBloqueioService.removerBloqueio(barbeariaId, bloqueioId);
        return ResponseEntity.noContent().build();
    }

    private Long extrairBarbeariaIdDoToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);
        Object userIdClaim = jwtService.extractClaim(token, "userId");

        if (userIdClaim instanceof Number) {
            return ((Number) userIdClaim).longValue();
        } else if (userIdClaim instanceof String) {
            try {
                return Long.parseLong((String) userIdClaim);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
