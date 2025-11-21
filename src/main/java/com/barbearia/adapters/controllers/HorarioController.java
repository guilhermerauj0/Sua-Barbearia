package com.barbearia.adapters.controllers;

import com.barbearia.application.dto.HorarioFuncionamentoRequestDto;
import com.barbearia.application.dto.HorarioFuncionamentoResponseDto;
import com.barbearia.application.services.HorarioGestaoService;
import com.barbearia.application.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller REST para gestão de horários.
 */
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

    /**
     * Lista os horários de funcionamento de um funcionário.
     */
    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<HorarioFuncionamentoResponseDto>> listarHorariosFuncionario(@PathVariable Long funcionarioId) {
        return ResponseEntity.ok(horarioGestaoService.listarHorariosFuncionario(funcionarioId));
    }

    /**
     * Salva o horário de funcionamento de um funcionário.
     */
    @PostMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<?> salvarHorarioFuncionario(
            @PathVariable Long funcionarioId,
            @RequestBody HorarioFuncionamentoRequestDto dto,
            HttpServletRequest request) {
        try {
            // Extrai barbeariaId do token (assumindo que quem edita é a barbearia ou o próprio funcionário)
            // Simplificação: pegando do token se for barbearia, ou validando permissão
            // Aqui vou assumir que o token tem o ID da barbearia se for admin/barbearia
            // Ou se for o próprio funcionário.
            
            // Por enquanto, vamos pegar o barbeariaId do token se disponível, ou passar como parametro?
            // O ideal seria validar se o usuário tem permissão.
            
            // Vamos extrair o ID do usuário e verificar permissões (simplificado)
            // Long usuarioId = jwtService.extractUserId(token);
            // String role = jwtService.extractRole(token);
            
            // Para simplificar e atender o requisito "facilitar para a barbearia adicionar", 
            // vamos assumir que o frontend envia o token da barbearia.
            // Precisamos do ID da barbearia. Onde pegar?
            // O funcionário pertence a uma barbearia. Podemos buscar o funcionário e pegar o ID da barbearia dele.
            // Mas aqui no controller não tenho acesso ao repo de funcionário.
            // Vou passar um ID fixo ou extrair do token se for barbearia.
            
            // Melhor: O service pode buscar o funcionário e pegar o barbeariaId dele.
            // Mas o método salvarHorarioFuncionario pede barbeariaId.
            // Vou alterar o service para buscar o funcionário e pegar o barbeariaId dele, 
            // assim não preciso passar barbeariaId no controller.
            
            // Mas espere, eu já editei o service e ele pede barbeariaId.
            // Vou passar um valor dummy ou tentar extrair.
            // O correto é extrair do token.
            
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Token inválido");
            }
            String token = authHeader.substring(7);
            
            // Tenta pegar barbeariaId do token (se for logado como barbearia)
            // Se for logado como funcionário, talvez tenha barbeariaId no token também?
            // Se não, teria que buscar no banco.
            
            // Vou assumir que o token tem o claim "barbeariaId" ou que o "userId" é o barbeariaId se role=BARBEARIA.
            
            Long barbeariaId = null;
            String role = (String) jwtService.extractClaim(token, "role");
            Object userIdObj = jwtService.extractClaim(token, "userId");
            Long userId = userIdObj instanceof Number ? ((Number) userIdObj).longValue() : Long.parseLong(userIdObj.toString());

            if ("BARBEARIA".equals(role)) {
                barbeariaId = userId;
            } else {
                // Se for funcionário, precisaria buscar a barbearia dele.
                // Como não tenho acesso fácil aqui, vou lançar erro se não for barbearia por enquanto,
                // ou assumir que o service vai validar.
                // Mas o service pede barbeariaId.
                
                // Vou fazer um "hack" seguro: passar o userId como barbeariaId SE for barbearia.
                // Se não for, retorna erro 403.
                return ResponseEntity.status(403).body("Apenas a barbearia pode gerenciar horários (por enquanto)");
            }

            HorarioFuncionamentoResponseDto response = horarioGestaoService.salvarHorarioFuncionario(barbeariaId, funcionarioId, dto);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
