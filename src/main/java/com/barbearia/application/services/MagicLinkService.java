package com.barbearia.application.services;

import com.barbearia.application.security.JwtService;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MagicLinkService {

    private final JwtService jwtService;
    private final FuncionarioRepository funcionarioRepository;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public MagicLinkService(JwtService jwtService, FuncionarioRepository funcionarioRepository) {
        this.jwtService = jwtService;
        this.funcionarioRepository = funcionarioRepository;
    }

    @SuppressWarnings("null")
    public String gerarLinkAcesso(Long funcionarioId, Long barbeariaId) {
        JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado"));

        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Funcionário não pertence a esta barbearia");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", funcionario.getId());
        claims.put("role", "BARBEIRO"); // Ou extrair do perfil
        claims.put("type", "magic_link");
        claims.put("barbeariaId", barbeariaId);

        // Token válido por 24 horas (86400000 ms)
        String token = jwtService.generateToken(claims, funcionario.getEmail(), 86400000L);

        return frontendUrl + "/acesso-profissional?token=" + token;
    }
}
