package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.HorarioFuncionamentoRequestDto;
import com.barbearia.application.dto.HorarioFuncionamentoResponseDto;
import com.barbearia.application.services.HorarioGestaoService;
import com.barbearia.application.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Gestão de Horários de Funcionários.
 */
@Tag(name = "Horários", description = "Horários de funcionamento dos profissionais")
@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = "*")
public class HorarioController {

    private final HorarioGestaoService horarioGestaoService;
    private final JwtService jwtService;

    public HorarioController(HorarioGestaoService horarioGestaoService, JwtService jwtService) {
        this.horarioGestaoService = horarioGestaoService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Listar horários do funcionário", description = "Retorna horários base de funcionamento de um profissional (seg-dom)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de horários", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HorarioFuncionamentoResponseDto.class)))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<HorarioFuncionamentoResponseDto>> listarHorariosFuncionario(
            @Parameter(description = "ID do funcionário", example = "1") @PathVariable Long funcionarioId) {
        return ResponseEntity.ok(horarioGestaoService.listarHorariosFuncionario(funcionarioId));
    }

    @Operation(summary = "Definir horário do funcionário", description = "Barbearia define horário de trabalho de um profissional para um dia da semana", security = @SecurityRequirement(name = "Bearer"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Horário Segunda-feira", value = """
            {
              "diaSemana": "SEGUNDA",
              "horaAbertura": "09:00",
              "horaFechamento": "18:00",
              "ativo": true
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horário salvo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HorarioFuncionamentoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(name = "Validação", value = """
                    {
                      "timestamp": "2025-11-25T17:00:00",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Horário de abertura inválido",
                      "path": "/api/barbearias/horarios/funcionario/1"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @PostMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<?> salvarHorarioFuncionario(
            @Parameter(description = "ID do funcionário") @PathVariable Long funcionarioId,
            @RequestBody HorarioFuncionamentoRequestDto dto,
            HttpServletRequest request) {
        Long barbeariaId = extrairBarbeariaIdDoToken(request);
        if (barbeariaId == null) {
            throw new IllegalArgumentException("Token JWT inválido ou barbearia não encontrada");
        }
        var horario = horarioGestaoService.salvarHorarioFuncionario(barbeariaId, funcionarioId, dto);
        return ResponseEntity.ok(horario);
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
