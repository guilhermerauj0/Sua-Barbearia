package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.application.services.FuncionarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
            @ApiResponse(responseCode = "200", description = "Lista retornada")
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
            @ApiResponse(responseCode = "200", description = "Lista de profissionais filtrados")
    })
    @GetMapping("/servico/{servicoId}")
    public ResponseEntity<List<FuncionarioResponseDto>> listarPorServico(
            @Parameter(description = "ID do serviço", example = "1") @PathVariable Long servicoId,
            @Parameter(description = "ID da barbearia (OBRIGATÓRIO)", required = true, example = "1") @RequestParam Long barbeariaId) {
        return ResponseEntity.ok(funcionarioService.listarProfissionaisPorServicoEBarbearia(servicoId, barbeariaId));
    }
}
