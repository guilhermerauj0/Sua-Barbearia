package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ServicoDto;
import com.barbearia.application.dto.ServicoRequestDto;
import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.BarbeariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para operações de serviços por barbearias autenticadas.
 * 
 * Endpoints:
 * - POST /api/barbearias/servicos - Criar novo serviço
 * - PUT /api/barbearias/servicos/{id} - Editar serviço existente
 * - DELETE /api/barbearias/servicos/{id} - Desativar serviço (soft delete)
 * 
 * Todos os endpoints requerem autenticação JWT com role BARBEARIA.
 * O ID da barbearia é extraído automaticamente do token JWT.
 * 
 * @author Sua Barbearia Team
 */
@Tag(name = "Serviços", description = "Operações de gerenciamento de serviços para barbearias autenticadas")
@RestController
@RequestMapping("/api/barbearias/servicos")
@CrossOrigin(origins = "*")
public class ServicoController {

    private final BarbeariaService barbeariaService;
    private final JwtService jwtService;

    public ServicoController(BarbeariaService barbeariaService, JwtService jwtService) {
        this.barbeariaService = barbeariaService;
        this.jwtService = jwtService;
    }

    @Operation(summary = "Criar novo serviço para a barbearia autenticada", description = "Cria um serviço (ex: corte, barba) vinculado à barbearia autenticada. "
            +
            "O corpo da requisição deve conter nome, descrição, preço e duração.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Serviço criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServicoDto.class), examples = @ExampleObject(name = "Serviço Criado", value = "{\"id\":1,\"nome\":\"Corte Masculino\",\"descricao\":\"Corte clássico\",\"preco\":30.0,\"duracao\":30,\"ativo\":true}"))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:00:00",
                      "status": 400,
                      "error": "Dados Inválidos",
                      "message": "Nome do serviço é obrigatório",
                      "path": "/api/barbearias/servicos"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:00:00",
                      "status": 401,
                      "error": "Não Autorizado",
                      "message": "Token JWT inválido ou expirado",
                      "path": "/api/barbearias/servicos"
                    }
                    """))),
            @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:00:00",
                      "status": 403,
                      "error": "Acesso Negado",
                      "message": "Acesso negado: Apenas barbearias podem realizar esta operação",
                      "path": "/api/barbearias/servicos"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @PostMapping
    public ResponseEntity<?> criarServico(
            @RequestBody ServicoRequestDto requestDto,
            HttpServletRequest request) {
        // Extract JWT token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new com.barbearia.domain.exceptions.AcessoNegadoException("Token JWT não fornecido ou inválido");
        }
        token = token.substring(7);
        // Extract barbershop ID from token
        Object userIdObj = jwtService.extractClaim(token, "userId");
        if (userIdObj == null) {
            throw new com.barbearia.domain.exceptions.AcessoNegadoException(
                    "Token JWT inválido: userId não encontrado");
        }
        Long barbeariaId = ((Number) userIdObj).longValue();
        // Create the service
        ServicoDto servicoCriado = barbeariaService.criarServico(barbeariaId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoCriado);
    }

    @Operation(summary = "Editar serviço existente", description = "Atualiza os dados de um serviço da barbearia autenticada. "
            +
            "Campos editáveis: nome, descrição, preço e duração. " +
            "O tipo de serviço (tipoServico) é IMUTÁVEL e não pode ser alterado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço atualizado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServicoDto.class), examples = @ExampleObject(name = "Serviço Atualizado", value = "{\"id\":1,\"nome\":\"Corte Premium\",\"descricao\":\"Corte moderno\",\"preco\":50.0,\"duracao\":45,\"ativo\":true}"))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou tentativa de alterar tipoServico", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:00:00",
                      "status": 400,
                      "error": "Dados Inválidos",
                      "message": "Não é permitido alterar o tipo de serviço",
                      "path": "/api/barbearias/servicos/1"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado ou não pertence à barbearia", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class), examples = @ExampleObject(value = """
                    {
                      "timestamp": "2025-11-25T14:00:00",
                      "status": 404,
                      "error": "Não Encontrado",
                      "message": "Serviço não encontrado",
                      "path": "/api/barbearias/servicos/1"
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> editarServico(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID do serviço a ser editado", required = true, example = "1") @PathVariable Long id,
            @RequestBody ServicoRequestDto requestDto,
            HttpServletRequest request) {
        // Extrai token JWT
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new com.barbearia.domain.exceptions.AcessoNegadoException("Token JWT não fornecido ou inválido");
        }
        token = token.substring(7);

        // Extrai ID da barbearia do token
        Object userIdObj = jwtService.extractClaim(token, "userId");
        if (userIdObj == null) {
            throw new com.barbearia.domain.exceptions.AcessoNegadoException(
                    "Token JWT inválido: userId não encontrado");
        }
        Long barbeariaId = ((Number) userIdObj).longValue();

        // Edita o serviço
        ServicoDto servicoAtualizado = barbeariaService.editarServico(barbeariaId, id, requestDto);
        return ResponseEntity.ok(servicoAtualizado);
    }

    @Operation(summary = "Desativar serviço (soft delete)", description = "Marca um serviço como inativo ao invés de removê-lo do banco de dados. "
            +
            "Isso preserva o histórico de agendamentos e a integridade referencial. " +
            "Serviços inativos não aparecem mais nas listagens públicas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Serviço desativado com sucesso", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Serviço desativado com sucesso. O histórico de agendamentos foi preservado."))),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class))),
            @ApiResponse(responseCode = "404", description = "Serviço não encontrado ou não pertence à barbearia", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ApiErrorDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desativarServico(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID do serviço a ser desativado", required = true, example = "1") @PathVariable Long id,
            HttpServletRequest request) {
        // Extrai token JWT
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new com.barbearia.domain.exceptions.AcessoNegadoException("Token JWT não fornecido ou inválido");
        }
        token = token.substring(7);

        // Extrai ID da barbearia do token
        Object userIdObj = jwtService.extractClaim(token, "userId");
        if (userIdObj == null) {
            throw new com.barbearia.domain.exceptions.AcessoNegadoException(
                    "Token JWT inválido: userId não encontrado");
        }
        Long barbeariaId = ((Number) userIdObj).longValue();

        // Desativa o serviço
        String mensagem = barbeariaService.desativarServico(barbeariaId, id);
        return ResponseEntity.ok(mensagem);
    }
}
