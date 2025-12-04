package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.ApiErrorDto;
import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.application.services.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Consulta de Funcionários/Profissionais.
 */
@Tag(name = "Funcionários", description = "Listar profissionais por barbearia ou serviço")
@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

        private final FuncionarioService funcionarioService;

        public FuncionarioController(FuncionarioService funcionarioService) {
                this.funcionarioService = funcionarioService;
        }

        @Operation(summary = "Listar funcionários da barbearia", description = "Retorna todos os funcionários ativos de uma barbearia específica")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista retornada"),
                        @ApiResponse(responseCode = "404", description = "Barbearia não encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(value = """
                                        {
                                          "timestamp": "2025-11-25T14:00:00",
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Barbearia não encontrada com id: 1",
                                          "path": "/api/funcionarios/barbearia/1"
                                        }
                                        """)))
        })
        @GetMapping("/barbearia/{barbeariaId}")
        public ResponseEntity<List<FuncionarioResponseDto>> listarPorBarbearia(
                        @Parameter(description = "ID da barbearia", example = "1") @PathVariable Long barbeariaId) {
                return ResponseEntity.ok(funcionarioService.listarFuncionariosDaBarbearia(barbeariaId));
        }

        @Operation(summary = "Listar profissionais por serviço", description = "Retorna profissionais que executam um serviço DENTRO de uma barbearia específica. "
                        +
                        "⚠️ barbeariaId é OBRIGATÓRIO para evitar listar profissionais de outras barbearias.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de profissionais filtrados"),
                        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(value = """
                                        {
                                          "timestamp": "2025-11-25T14:00:00",
                                          "status": 400,
                                          "error": "Bad Request",
                                          "message": "Parâmetro obrigatório ausente: barbeariaId",
                                          "path": "/api/funcionarios/servico/1"
                                        }
                                        """))),
                        @ApiResponse(responseCode = "404", description = "Serviço ou barbearia não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class), examples = @ExampleObject(value = """
                                        {
                                          "timestamp": "2025-11-25T14:00:00",
                                          "status": 404,
                                          "error": "Not Found",
                                          "message": "Serviço não encontrado com id: 1",
                                          "path": "/api/funcionarios/servico/1"
                                        }
                                        """)))
        })
        @GetMapping("/servico/{servicoId}")
        public ResponseEntity<List<FuncionarioResponseDto>> listarPorServico(
                        @Parameter(description = "ID do serviço", example = "1") @PathVariable Long servicoId,
                        @Parameter(description = "ID da barbearia (OBRIGATÓRIO)", required = true, example = "1") @RequestParam Long barbeariaId) {
                return ResponseEntity
                                .ok(funcionarioService.listarProfissionaisPorServicoEBarbearia(servicoId, barbeariaId));
        }

        @Operation(summary = "Listar serviços do profissional", description = "Retorna todos os serviços ativos que um profissional está habilitado a realizar.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lista de serviços retornada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.barbearia.application.dto.ServicoDto.class))),
                        @ApiResponse(responseCode = "404", description = "Profissional não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorDto.class)))
        })
        @GetMapping("/{id}/servicos")
        public ResponseEntity<List<com.barbearia.application.dto.ServicoDto>> listarServicosDoProfissional(
                        @Parameter(description = "ID do profissional", example = "1") @PathVariable Long id) {
                return ResponseEntity.ok(funcionarioService.listarServicosDoProfissional(id));
        }
}
