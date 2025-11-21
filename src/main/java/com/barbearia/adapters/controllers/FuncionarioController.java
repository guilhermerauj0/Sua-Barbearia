package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.FuncionarioResponseDto;
import com.barbearia.application.services.FuncionarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações de funcionários.
 */
@RestController
@RequestMapping("/api/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    /**
     * Lista todos os funcionários de uma barbearia.
     */
    @GetMapping("/barbearia/{barbeariaId}")
    public ResponseEntity<List<FuncionarioResponseDto>> listarPorBarbearia(@PathVariable Long barbeariaId) {
        return ResponseEntity.ok(funcionarioService.listarFuncionariosDaBarbearia(barbeariaId));
    }

    /**
     * Lista profissionais que realizam um determinado serviço.
     */
    @GetMapping("/servico/{servicoId}")
    public ResponseEntity<List<FuncionarioResponseDto>> listarPorServico(@PathVariable Long servicoId) {
        return ResponseEntity.ok(funcionarioService.listarProfissionaisPorServico(servicoId));
    }
}
