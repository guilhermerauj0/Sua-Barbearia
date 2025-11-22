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
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou inválido", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "403", description = "Usuário não possui role BARBEARIA", content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor", content = @Content(mediaType = "text/plain"))
    })
    @PostMapping
    public ResponseEntity<?> criarServico(
            @RequestBody ServicoRequestDto requestDto,
            HttpServletRequest request) {
        try {
            // Extract JWT token
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT não fornecido ou inválido");
            }
            token = token.substring(7);
            // Extract barbershop ID from token
            Object userIdObj = jwtService.extractClaim(token, "userId");
            if (userIdObj == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token JWT inválido: userId não encontrado");
            }
            Long barbeariaId = ((Number) userIdObj).longValue();
            // Create the service
            ServicoDto servicoCriado = barbeariaService.criarServico(barbeariaId, requestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(servicoCriado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao criar serviço: " + e.getMessage());
        }
    }
}
