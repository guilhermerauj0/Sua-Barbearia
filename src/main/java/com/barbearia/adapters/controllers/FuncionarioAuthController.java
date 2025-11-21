package com.barbearia.adapters.controllers;

import com.barbearia.application.security.JwtService;
import com.barbearia.application.services.MagicLinkService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/barbearias")
@CrossOrigin(origins = "*")
public class FuncionarioAuthController {

    private final MagicLinkService magicLinkService;
    private final JwtService jwtService;

    public FuncionarioAuthController(MagicLinkService magicLinkService, JwtService jwtService) {
        this.magicLinkService = magicLinkService;
        this.jwtService = jwtService;
    }

    @PostMapping("/funcionarios/{funcionarioId}/link-acesso")
    @PreAuthorize("hasRole('BARBEARIA')")
    public ResponseEntity<?> gerarLinkAcesso(
            @PathVariable Long funcionarioId,
            HttpServletRequest request) {
        try {
            Long barbeariaId = extrairBarbeariaId(request);
            String link = magicLinkService.gerarLinkAcesso(funcionarioId, barbeariaId);
            return ResponseEntity.ok(Map.of("link", link));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao gerar link: " + e.getMessage());
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
