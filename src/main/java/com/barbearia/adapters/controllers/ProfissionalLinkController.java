package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.FuncionarioLinkRequestDto;
import com.barbearia.application.dto.FuncionarioLinkResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.ProfissionalLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

/**
 * Gestão de Links de Profissionais (Barbearia).
 */
@Tag(name = "Gestão de Links Profissionais", description = "Barbearia gerencia links de acesso dos profissionais")
@RestController
@RequestMapping("/api/barbearias/funcionarios")
@CrossOrigin(origins = "*")
public class ProfissionalLinkController {

    private final ProfissionalLinkService profissionalLinkService;
    private final JwtService jwtService;

    public ProfissionalLinkController(ProfissionalLinkService profissionalLinkService,
            JwtService jwtService) {
        this.profissionalLinkService = profissionalLinkService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Gerar link de acesso", description = "Barbearia gera link único para profissional acessar dashboard. Pode definir expiração opcional.", security = @SecurityRequirement(name = "Bearer"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "Com expiração (30 dias)", value = """
                    {
                      "dataExpiracao": "2025-12-25"
                    }
                    """),
            @ExampleObject(name = "Sem expiração", value = "{}")
    })))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Link gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Funcionário não pertence à barbearia"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/{funcionarioId}/link-acesso")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> gerarLinkAcesso(
            @Parameter(description = "ID do funcionário") @PathVariable Long funcionarioId,
            @RequestBody(required = false) @Valid FuncionarioLinkRequestDto requestDto,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);

            FuncionarioLinkResponseDto response = profissionalLinkService.gerarLinkAcesso(
                    barbeariaId,
                    funcionarioId,
                    requestDto != null ? requestDto.getDataExpiracao() : null);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @Operation(summary = "Consultar status do link", description = "Verifica se profissional tem link ativo, expirado ou desativado", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retornado"),
            @ApiResponse(responseCode = "404", description = "Profissional não possui link"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @GetMapping("/{funcionarioId}/link-acesso")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> consultarStatusLink(
            @Parameter(description = "ID do funcionário") @PathVariable Long funcionarioId,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);

            FuncionarioLinkResponseDto response = profissionalLinkService.consultarStatusLink(
                    barbeariaId,
                    funcionarioId);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Profissional não possui link gerado");
            }

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @Operation(summary = "Ativar link", description = "Reativa link desativado. Pode atualizar data de expiração.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Link ativado"),
            @ApiResponse(responseCode = "400", description = "Link não existe"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PatchMapping("/{funcionarioId}/link-acesso/ativar")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> ativarLink(
            @Parameter(description = "ID do funcionário") @PathVariable Long funcionarioId,
            @RequestBody(required = false) @Valid FuncionarioLinkRequestDto requestDto,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);

            FuncionarioLinkResponseDto response = profissionalLinkService.reativarLink(
                    barbeariaId,
                    funcionarioId,
                    requestDto != null ? requestDto.getDataExpiracao() : null);

            return ResponseEntity.ok(response);

        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @Operation(summary = "Desativar link", description = "Desativa link do profissional. Ele não poderá mais acessar o dashboard até reativar.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Link desativado"),
            @ApiResponse(responseCode = "400", description = "Link não existe"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PatchMapping("/{funcionarioId}/link-acesso/desativar")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> desativarLink(
            @Parameter(description = "ID do funcionário") @PathVariable Long funcionarioId,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);

            profissionalLinkService.desativarLink(barbeariaId, funcionarioId);

            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    @Operation(summary = "Atualizar expiração", description = "Modifica data de expiração de link ativo", security = @SecurityRequirement(name = "Bearer"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Nova data", value = """
            {
              "dataExpiracao": "2026-01-01"
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Expiração atualizada"),
            @ApiResponse(responseCode = "400", description = "Data inválida ou link não existe"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PutMapping("/{funcionarioId}/link-acesso/expiracao")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> atualizarExpiracao(
            @Parameter(description = "ID do funcionário") @PathVariable Long funcionarioId,
            @Valid @RequestBody FuncionarioLinkRequestDto requestDto,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);

            profissionalLinkService.atualizarExpiracao(
                    barbeariaId,
                    funcionarioId,
                    requestDto.getDataExpiracao());

            return ResponseEntity.noContent().build();

        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
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
