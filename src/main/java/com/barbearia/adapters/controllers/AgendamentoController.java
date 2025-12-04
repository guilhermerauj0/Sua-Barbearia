package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.AgendamentoRequestDto;
import com.barbearia.application.dto.AgendamentoResponseDto;
import com.barbearia.application.dto.ApiErrorDto;
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
      @ApiResponse(responseCode = "404", description = "Agendamento não existe", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
      @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content(schema = @Schema(implementation = ApiErrorDto.class))),
      @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(schema = @Schema(implementation = ApiErrorDto.class)))
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

  @Operation(summary = "Criar agendamento", description = "Cliente agenda serviço com profissional específico. Valida conflitos e disponibilidade. O barbeariaId é determinado automaticamente através do funcionário.", security = @SecurityRequirement(name = "Bearer"), requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(name = "Agendamento Exemplo", value = """
      {
        "servicoId": 1,
        "funcionarioId": 1,
        "dataHora": "2025-12-01T14:30:00",
        "observacoes": "Preferência: barba com navalha"
      }
      """))))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Agendamento criado", content = @Content(schema = @Schema(implementation = AgendamentoResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Validação", value = """
          {
            "timestamp": "2025-11-25T14:30:00",
            "status": 400,
            "error": "Bad Request",
            "message": "Dados inválidos: servicoId é obrigatório",
            "path": "/api/agendamentos"
          }
          """))),
      @ApiResponse(responseCode = "422", description = "Horário indisponível ou conflito", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(value = """
          {
            "timestamp": "2025-11-25T14:30:00",
            "status": 422,
            "error": "Unprocessable Entity",
            "message": "Horário não disponível para este profissional",
            "path": "/api/agendamentos"
          }
          """))),
      @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(value = """
          {
            "timestamp": "2025-11-25T14:30:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Token JWT inválido ou expirado",
            "path": "/api/agendamentos"
          }
          """)))
  })
  @PostMapping
  public ResponseEntity<?> criarAgendamento(
      @RequestBody AgendamentoRequestDto requestDto,
      HttpServletRequest request) {
    // Extrai o ID do cliente (usuário autenticado) do token JWT
    Long clienteId = extrairUsuarioIdDoToken(request);

    if (clienteId == null) {
      throw new com.barbearia.domain.exceptions.AcessoNegadoException(
          "Token JWT inválido ou userId não encontrado");
    }

    // Extrai o tipo de usuário do token JWT
    String tipoUsuario = extrairTipoUsuarioDoToken(request);

    // Validação: apenas CLIENTE pode criar agendamentos
    if (!"CLIENTE".equalsIgnoreCase(tipoUsuario)) {
      throw new com.barbearia.domain.exceptions.AcessoNegadoException("Apenas clientes podem criar agendamentos");
    }

    // Cria o agendamento
    AgendamentoResponseDto resposta = agendamentoService.criarAgendamento(clienteId, requestDto);

    return ResponseEntity.status(201).body(resposta);
  }

  /**
   * Cancela um agendamento.
   */
  @Operation(summary = "Cancelar agendamento", description = "Cliente ou barbearia cancela agendamento. Apenas status PENDENTE ou CONFIRMADO podem ser cancelados.", security = @SecurityRequirement(name = "Bearer"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Cancelado com sucesso"),
      @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Token JWT inválido",
            "path": "/api/agendamentos/1/cancelar"
          }
          """))),
      @ApiResponse(responseCode = "403", description = "Sem permissão ou status inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Acesso Negado", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 403,
            "error": "Forbidden",
            "message": "Você não tem permissão para cancelar este agendamento",
            "path": "/api/agendamentos/1/cancelar"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 404,
            "error": "Not Found",
            "message": "Agendamento com ID 1 não encontrado",
            "path": "/api/agendamentos/1/cancelar"
          }
          """)))
  })
  @PostMapping("/{id}/cancelar")
  public ResponseEntity<?> cancelarAgendamento(
      @PathVariable Long id,
      HttpServletRequest request) {
    Long usuarioId = extrairUsuarioIdDoToken(request);
    String tipoUsuario = extrairTipoUsuarioDoToken(request);

    if (usuarioId == null || tipoUsuario == null) {
      throw new com.barbearia.domain.exceptions.AcessoNegadoException("Token JWT inválido");
    }

    agendamentoService.cancelarAgendamento(id, usuarioId, tipoUsuario);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Reagendar agendamento", description = "Cliente ou barbearia muda data/hora. Valida conflitos no novo horário.", security = @SecurityRequirement(name = "Bearer"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Reagendado", content = @Content(schema = @Schema(implementation = AgendamentoResponseDto.class))),
      @ApiResponse(responseCode = "400", description = "Novo horário indisponível", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Horário Indisponível", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "O novo horário 2025-12-01T10:00:00 não está disponível para este profissional.",
            "path": "/api/agendamentos/1/reagendar"
          }
          """))),
      @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Token JWT inválido",
            "path": "/api/agendamentos/1/reagendar"
          }
          """))),
      @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Acesso Negado", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 403,
            "error": "Forbidden",
            "message": "Você não tem permissão para reagendar este agendamento.",
            "path": "/api/agendamentos/1/reagendar"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 404,
            "error": "Not Found",
            "message": "Agendamento com ID 1 não encontrado",
            "path": "/api/agendamentos/1/reagendar"
          }
          """)))
  })
  @PostMapping("/{id}/reagendar")
  public ResponseEntity<?> reagendarAgendamento(
      @PathVariable Long id,
      @RequestBody com.barbearia.application.dto.AgendamentoReagendamentoDto dto,
      HttpServletRequest request) {
    Long usuarioId = extrairUsuarioIdDoToken(request);
    String tipoUsuario = extrairTipoUsuarioDoToken(request);

    if (usuarioId == null || tipoUsuario == null) {
      throw new com.barbearia.domain.exceptions.AcessoNegadoException("Token JWT inválido");
    }

    AgendamentoResponseDto agendamento = agendamentoService.reagendarAgendamento(id, dto.novaDataHora(),
        usuarioId, tipoUsuario);

    return ResponseEntity.ok(agendamento);
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
      @ApiResponse(responseCode = "400", description = "Agendamento original não está concluído ou data inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Validação", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 400,
            "error": "Bad Request",
            "message": "O agendamento original não está concluído",
            "path": "/api/agendamentos/1/repetir"
          }
          """))),
      @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Token JWT inválido",
            "path": "/api/agendamentos/1/repetir"
          }
          """))),
      @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Acesso Negado", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 403,
            "error": "Forbidden",
            "message": "Você não tem permissão para repetir este agendamento",
            "path": "/api/agendamentos/1/repetir"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Agendamento original não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
          {
            "timestamp": "2025-11-25T16:00:00",
            "status": 404,
            "error": "Not Found",
            "message": "Agendamento com ID 1 não encontrado",
            "path": "/api/agendamentos/1/repetir"
          }
          """)))
  })
  @PostMapping("/{id}/repetir")
  public ResponseEntity<?> repetirAgendamento(
      @PathVariable Long id,
      @RequestBody com.barbearia.application.dto.AgendamentoReagendamentoDto dto,
      HttpServletRequest request) {
    Long usuarioId = extrairUsuarioIdDoToken(request);
    String tipoUsuario = extrairTipoUsuarioDoToken(request);

    if (usuarioId == null || tipoUsuario == null) {
      throw new com.barbearia.domain.exceptions.AcessoNegadoException("Token JWT inválido");
    }

    // Criar novo agendamento baseado no concluído
    AgendamentoResponseDto novoAgendamento = agendamentoService.repetirAgendamento(id, dto.novaDataHora(),
        usuarioId, tipoUsuario);

    return ResponseEntity.status(201).body(novoAgendamento);
  }

  /**
   * Confirma um agendamento.
   */
  @Operation(summary = "Confirmar agendamento", description = "Barbearia confirma agendamento PENDENTE.", security = @SecurityRequirement(name = "Bearer"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Confirmado", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Sucesso", value = "\"Agendamento confirmado com sucesso\""))),
      @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
          {
            "timestamp": "2025-11-25T15:30:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Token JWT inválido",
            "path": "/api/agendamentos/1/confirmar"
          }
          """))),
      @ApiResponse(responseCode = "403", description = "Apenas barbearia pode confirmar", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Acesso Negado", value = """
          {
            "timestamp": "2025-11-25T15:30:00",
            "status": 403,
            "error": "Forbidden",
            "message": "Apenas barbearia pode confirmar agendamento",
            "path": "/api/agendamentos/1/confirmar"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
          {
            "timestamp": "2025-11-25T15:30:00",
            "status": 404,
            "error": "Not Found",
            "message": "Agendamento com ID 1 não encontrado",
            "path": "/api/agendamentos/1/confirmar"
          }
          """)))
  })
  @PostMapping("/{id}/confirmar")
  public ResponseEntity<?> confirmarAgendamento(
      @PathVariable Long id,
      HttpServletRequest request) {
    Long usuarioId = extrairUsuarioIdDoToken(request);
    String tipoUsuario = extrairTipoUsuarioDoToken(request);

    if (usuarioId == null || tipoUsuario == null) {
      throw new com.barbearia.domain.exceptions.AcessoNegadoException("Token JWT inválido");
    }

    agendamentoService.confirmarAgendamento(id, usuarioId, tipoUsuario);

    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Concluir agendamento", description = "Barbearia marca agendamento como CONCLUÍDO após atendimento.", security = @SecurityRequirement(name = "Bearer"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Concluído", content = @Content(mediaType = "application/json", examples = @ExampleObject(name = "Sucesso", value = "\"Agendamento concluído com sucesso\""))),
      @ApiResponse(responseCode = "401", description = "Token inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Autorizado", value = """
          {
            "timestamp": "2025-11-25T15:30:00",
            "status": 401,
            "error": "Unauthorized",
            "message": "Token JWT inválido",
            "path": "/api/agendamentos/1/concluir"
          }
          """))),
      @ApiResponse(responseCode = "403", description = "Apenas barbearia pode concluir", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Acesso Negado", value = """
          {
            "timestamp": "2025-11-25T15:30:00",
            "status": 403,
            "error": "Forbidden",
            "message": "Apenas barbearia pode concluir agendamento",
            "path": "/api/agendamentos/1/concluir"
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(name = "Não Encontrado", value = """
          {
            "timestamp": "2025-11-25T15:30:00",
            "status": 404,
            "error": "Not Found",
            "message": "Agendamento com ID 1 não encontrado",
            "path": "/api/agendamentos/1/concluir"
          }
          """)))
  })
  @PostMapping("/{id}/concluir")
  public ResponseEntity<?> concluirAgendamento(
      @PathVariable Long id,
      HttpServletRequest request) {
    Long usuarioId = extrairUsuarioIdDoToken(request);
    String tipoUsuario = extrairTipoUsuarioDoToken(request);

    if (usuarioId == null || tipoUsuario == null) {
      throw new com.barbearia.domain.exceptions.AcessoNegadoException("Token JWT inválido");
    }

    agendamentoService.concluirAgendamento(id, usuarioId, tipoUsuario);

    return ResponseEntity.ok().build();
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
