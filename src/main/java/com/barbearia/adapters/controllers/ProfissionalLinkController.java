package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ApiErrorDto;
import com.barbearia.application.dto.FuncionarioLinkRequestDto;
import com.barbearia.application.dto.FuncionarioLinkResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.ProfissionalLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
                        @ApiResponse(responseCode = "400", description = "Funcionário não pertence à barbearia", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Validação", value = """
                                        {
                                          "timestamp": "2025-11-25T17:00:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "Funcionário não pertence a esta barbearia",
                                          "path": "/api/barbearias/funcionarios/1/link-acesso"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
                                        {
                                          "timestamp": "2025-11-25T17:00:00",
                                          "status": 401,
                                          "error": "Unauthorized",
                                          "message": "Token JWT inválido ou expirado",
                                          "path": "/api/barbearias/funcionarios/1/link-acesso"
                                        }
                                        """)))
        })
        @PostMapping("/{funcionarioId}/link-acesso")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> gerarLinkAcesso(
                        @Parameter(description = "ID do funcionário") @PathVariable Long funcionarioId,
                        @RequestBody(required = false) @Valid FuncionarioLinkRequestDto requestDto,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);

                // Se requestDto for null, cria um default (ex: expiração padrão 30 dias)
                if (requestDto == null) {
                        requestDto = new FuncionarioLinkRequestDto();
                        requestDto.setDiasExpiracao(30);
                }

                // Se diasExpiracao foi informado, calcula data. Se data foi informada, usa ela.
                // Se nenhum, usa 30 dias.
                java.time.LocalDateTime dataExpiracao = null;
                if (requestDto.getDataExpiracao() != null) {
                        dataExpiracao = requestDto.getDataExpiracao();
                } else if (requestDto.getDiasExpiracao() != null) {
                        dataExpiracao = java.time.LocalDateTime.now().plusDays(requestDto.getDiasExpiracao());
                } else {
                        dataExpiracao = java.time.LocalDateTime.now().plusDays(30);
                }

                FuncionarioLinkResponseDto response = profissionalLinkService.gerarLinkAcesso(
                                barbeariaId,
                                funcionarioId,
                                dataExpiracao);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Consultar status do link", description = "Verifica se o funcionário tem link ativo", security = @SecurityRequirement(name = "Bearer"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Status retornado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioLinkResponseDto.class))),
                        @ApiResponse(responseCode = "403", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/{funcionarioId}")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> consultarStatusLink(
                        @Parameter(description = "ID do funcionário") @PathVariable Long funcionarioId,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                FuncionarioLinkResponseDto response = profissionalLinkService.consultarStatusLink(barbeariaId,
                                funcionarioId);
                if (response == null) {
                        return ResponseEntity.notFound().build();
                }
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Desativar link", description = "Desativa o link de acesso imediatamente", security = @SecurityRequirement(name = "Bearer"))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Link desativado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioLinkResponseDto.class))),
                        @ApiResponse(responseCode = "403", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PostMapping("/{funcionarioId}/desativar")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> desativarLink(
                        @Parameter(description = "ID do funcionário") @PathVariable Long funcionarioId,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);
                profissionalLinkService.desativarLink(barbeariaId, funcionarioId);
                FuncionarioLinkResponseDto response = profissionalLinkService.consultarStatusLink(barbeariaId,
                                funcionarioId);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Atualizar expiração", description = "Atualiza a data de expiração do link", security = @SecurityRequirement(name = "Bearer"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Nova data", value = """
                        {
                          "diasExpiracao": 90
                        }
                        """))))
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Expiração atualizada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = FuncionarioLinkResponseDto.class))),
                        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Validação", value = """
                                        {
                                          "timestamp": "2025-11-25T17:00:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "Data de expiração inválida",
                                          "path": "/api/barbearias/funcionarios/1/link-acesso/expiracao"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "403", description = "Não autorizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "404", description = "Funcionário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class))),
                        @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @PutMapping("/{funcionarioId}/expiracao")
        @PreAuthorize("hasRole('BARBEARIA')")
        public ResponseEntity<?> atualizarExpiracao(
                        @Parameter(description = "ID do funcionário") @PathVariable Long funcionarioId,
                        @Valid @RequestBody FuncionarioLinkRequestDto requestDto,
                        HttpServletRequest request) {
                Long barbeariaId = extrairBarbeariaId(request);

                java.time.LocalDateTime novaData = null;
                if (requestDto.getDataExpiracao() != null) {
                        novaData = requestDto.getDataExpiracao();
                } else if (requestDto.getDiasExpiracao() != null) {
                        novaData = java.time.LocalDateTime.now().plusDays(requestDto.getDiasExpiracao());
                } else {
                        return ResponseEntity.badRequest()
                                        .body(ApiErrorDto.builder()
                                                        .timestamp(java.time.LocalDateTime.now())
                                                        .status(400)
                                                        .error("Bad Request")
                                                        .message("Data ou dias de expiração devem ser informados")
                                                        .path(request.getRequestURI())
                                                        .build());
                }

                profissionalLinkService.atualizarExpiracao(barbeariaId, funcionarioId, novaData);
                // Retorna status atualizado
                FuncionarioLinkResponseDto response = profissionalLinkService.consultarStatusLink(barbeariaId,
                                funcionarioId);
                return ResponseEntity.ok(response);
        }

        private Long extrairBarbeariaId(HttpServletRequest request) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        return ((Number) jwtService.extractClaim(token, "userId")).longValue();
                }
                throw new RuntimeException("Token não encontrado ou inválido");
        }
}
