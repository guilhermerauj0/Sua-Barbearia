package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AgendamentoRequestDto;
import com.barbearia.application.dto.AgendamentoResponseDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.AgendamentoService;
import com.barbearia.domain.exceptions.AcessoNegadoException;
import com.barbearia.domain.exceptions.AgendamentoNaoEncontradoException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Gestão de Agendamentos (Cliente).
 */
@Tag(name = "Agendamentos", description = "Criar, consultar e gerenciar agendamentos")
@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final JwtService jwtService;

    public AgendamentoController(AgendamentoService agendamentoService,
            JwtService jwtService) {
        this.agendamentoService = agendamentoService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Buscar agendamento", description = "Retorna detalhes completos do agendamento. Cliente vê apenas seus próprios.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agendamento encontrado", content = @Content(schema = @Schema(implementation = AgendamentoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não existe"),
            @ApiResponse(responseCode = "403", description = "Sem permissão"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarAgendamentoPorId(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            // Extrai o ID do usuário do token JWT
            Long usuarioId = extrairUsuarioIdDoToken(request);

            if (usuarioId == null) {
                return ResponseEntity.status(401).body("Token JWT inválido ou userId não encontrado");
            }

            // Extrai o tipo de usuário (CLIENTE, BARBEARIA, BARBEIRO) do token JWT
            String tipoUsuario = extrairTipoUsuarioDoToken(request);

            if (tipoUsuario == null) {
                return ResponseEntity.status(401).body("Token JWT inválido ou role não encontrado");
            }

            // Busca o agendamento com verificação de autorização
            AgendamentoResponseDto agendamento = agendamentoService.buscarAgendamentoPorId(id, usuarioId, tipoUsuario);

            return ResponseEntity.ok(agendamento);

        } catch (AgendamentoNaoEncontradoException e) {
            // Retorna 404 (Not Found)
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (AcessoNegadoException e) {
            // Retorna 403 (Forbidden)
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Retorna 400 (Bad Request)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Retorna 500 (Internal Server Error)
            return ResponseEntity.internalServerError()
                    .body("Erro ao buscar agendamento: " + e.getMessage());
        }
    }

    @Operation(summary = "Criar agendamento", description = "Cliente agenda serviço com profissional específico. Valida conflitos e disponibilidade.", security = @SecurityRequirement(name = "Bearer"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Agendamento Exemplo", value = """
            {
              "servicoId": 1,
              "funcionarioId": 1,
              "barbeariaId": 1,
              "dataHora": "2025-11-25T14:30:00",
              "observacoes": "Preferência: barba com navalha"
            }
            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Agendamento criado", content = @Content(schema = @Schema(implementation = AgendamentoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "422", description = "Horário indisponível ou conflito"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping
    public ResponseEntity<?> criarAgendamento(
            @RequestBody AgendamentoRequestDto requestDto,
            HttpServletRequest request) {
        try {
            // Extrai o ID do cliente (usuário autenticado) do token JWT
            Long clienteId = extrairUsuarioIdDoToken(request);

            if (clienteId == null) {
                return ResponseEntity.status(401).body("Token JWT inválido ou userId não encontrado");
            }

            // Extrai o tipo de usuário do token JWT
            String tipoUsuario = extrairTipoUsuarioDoToken(request);

            // Validação: apenas CLIENTE pode criar agendamentos
            if (!"CLIENTE".equalsIgnoreCase(tipoUsuario)) {
                return ResponseEntity.status(403).body("Apenas clientes podem criar agendamentos");
            }

            // Cria o agendamento
            AgendamentoResponseDto resposta = agendamentoService.criarAgendamento(clienteId, requestDto);

            return ResponseEntity.status(201).body(resposta);

        } catch (IllegalArgumentException e) {
            // Retorna 422 (Unprocessable Entity) para validações de negócio
            // ou 400 (Bad Request) para validações simples
            if (e.getMessage().contains("não existe")) {
                return ResponseEntity.status(400).body(e.getMessage());
            } else if (e.getMessage().contains("não pode ser") || e.getMessage().contains("Horário não disponível")
                    || e.getMessage().contains("não executa")) {
                return ResponseEntity.status(422).body(e.getMessage());
            } else {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } catch (Exception e) {
            // Retorna 500 (Internal Server Error)
            return ResponseEntity.internalServerError()
                    .body("Erro ao criar agendamento: " + e.getMessage());
        }
    }

    /**
     * Cancela um agendamento.
     */
    @Operation(summary = "Cancelar agendamento", description = "Cliente ou barbearia cancela agendamento. Apenas status PENDENTE ou CONFIRMADO podem ser cancelados.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cancelado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão ou status inválido"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarAgendamento(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long usuarioId = extrairUsuarioIdDoToken(request);
            String tipoUsuario = extrairTipoUsuarioDoToken(request);

            if (usuarioId == null || tipoUsuario == null) {
                return ResponseEntity.status(401).body("Token JWT inválido");
            }

            agendamentoService.cancelarAgendamento(id, usuarioId, tipoUsuario);

            return ResponseEntity.noContent().build();
        } catch (AgendamentoNaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao cancelar agendamento: " + e.getMessage());
        }
    }

    @Operation(summary = "Reagendar agendamento", description = "Cliente ou barbearia muda data/hora. Valida conflitos no novo horário.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reagendado", content = @Content(schema = @Schema(implementation = AgendamentoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "400", description = "Novo horário indisponível"),
            @ApiResponse(responseCode = "403", description = "Sem permissão"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/{id}/reagendar")
    public ResponseEntity<?> reagendarAgendamento(
            @PathVariable Long id,
            @RequestBody com.barbearia.application.dto.AgendamentoReagendamentoDto dto,
            HttpServletRequest request) {
        try {
            Long usuarioId = extrairUsuarioIdDoToken(request);
            String tipoUsuario = extrairTipoUsuarioDoToken(request);

            if (usuarioId == null || tipoUsuario == null) {
                return ResponseEntity.status(401).body("Token JWT inválido");
            }

            AgendamentoResponseDto agendamento = agendamentoService.reagendarAgendamento(id, dto.novaDataHora(),
                    usuarioId, tipoUsuario);

            return ResponseEntity.ok(agendamento);
        } catch (AgendamentoNaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao reagendar agendamento: " + e.getMessage());
        }
    }

    /**
     * Repete um agendamento concluído criando um novo.
     */
    @Operation(summary = "Repetir agendamento", description = "CRIA UM NOVO agendamento baseado em um serviço já concluído. "
            +
            "Não modifica o histórico - cria um registro novo com os mesmos dados (profissional, serviço) mas nova data.", security = @SecurityRequirement(name = "Bearer"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Repetir Exemplo", value = """
                    {
                      "novaDataHora": "2025-12-05T14:00:00"
                    }
                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Novo agendamento criado", content = @Content(schema = @Schema(implementation = AgendamentoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Agendamento original não está concluído ou data inválida"),
            @ApiResponse(responseCode = "404", description = "Agendamento original não encontrado"),
            @ApiResponse(responseCode = "403", description = "Sem permissão"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/{id}/repetir")
    public ResponseEntity<?> repetirAgendamento(
            @PathVariable Long id,
            @RequestBody com.barbearia.application.dto.AgendamentoReagendamentoDto dto,
            HttpServletRequest request) {
        try {
            Long usuarioId = extrairUsuarioIdDoToken(request);
            String tipoUsuario = extrairTipoUsuarioDoToken(request);

            if (usuarioId == null || tipoUsuario == null) {
                return ResponseEntity.status(401).body("Token JWT inválido");
            }

            // Criar novo agendamento baseado no concluído
            AgendamentoResponseDto novoAgendamento = agendamentoService.repetirAgendamento(id, dto.novaDataHora(),
                    usuarioId, tipoUsuario);

            return ResponseEntity.status(201).body(novoAgendamento);

        } catch (AgendamentoNaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao repetir agendamento: " + e.getMessage());
        }
    }

    /**
     * Confirma um agendamento.
     */
    @Operation(summary = "Confirmar agendamento", description = "Barbearia confirma agendamento PENDENTE.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Confirmado"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "403", description = "Apenas barbearia pode confirmar"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/{id}/confirmar")
    public ResponseEntity<?> confirmarAgendamento(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long usuarioId = extrairUsuarioIdDoToken(request);
            String tipoUsuario = extrairTipoUsuarioDoToken(request);

            if (usuarioId == null || tipoUsuario == null) {
                return ResponseEntity.status(401).body("Token JWT inválido");
            }

            agendamentoService.confirmarAgendamento(id, usuarioId, tipoUsuario);

            return ResponseEntity.ok().build();
        } catch (AgendamentoNaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao confirmar agendamento: " + e.getMessage());
        }
    }

    @Operation(summary = "Concluir agendamento", description = "Barbearia marca agendamento como CONCLUÍDO após atendimento.", security = @SecurityRequirement(name = "Bearer"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Concluído"),
            @ApiResponse(responseCode = "404", description = "Agendamento não encontrado"),
            @ApiResponse(responseCode = "403", description = "Apenas barbearia pode concluir"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @PostMapping("/{id}/concluir")
    public ResponseEntity<?> concluirAgendamento(
            @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long usuarioId = extrairUsuarioIdDoToken(request);
            String tipoUsuario = extrairTipoUsuarioDoToken(request);

            if (usuarioId == null || tipoUsuario == null) {
                return ResponseEntity.status(401).body("Token JWT inválido");
            }

            agendamentoService.concluirAgendamento(id, usuarioId, tipoUsuario);

            return ResponseEntity.ok().build();
        } catch (AgendamentoNaoEncontradoException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (AcessoNegadoException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao concluir agendamento: " + e.getMessage());
        }
    }

    /**
     * Extrai o ID do usuário do token JWT presente no header Authorization.
     * 
     * @param request Requisição HTTP
     * @return ID do usuário ou null se não encontrado
     */
    private Long extrairUsuarioIdDoToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);

        // Extrai o claim "userId" do token
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

    /**
     * Extrai o tipo de usuário (role) do token JWT presente no header
     * Authorization.
     * 
     * @param request Requisição HTTP
     * @return Tipo de usuário (CLIENTE, BARBEARIA, BARBEIRO) ou null se não
     *         encontrado
     */
    private String extrairTipoUsuarioDoToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.substring(7);

        // Extrai o claim "role" do token
        Object roleClaim = jwtService.extractClaim(token, "role");

        if (roleClaim != null) {
            return roleClaim.toString();
        }

        return null;
    }
}
