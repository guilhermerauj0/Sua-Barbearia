package com.barbearia.application.services;

import com.barbearia.application.dto.FuncionarioLinkResponseDto;
import com.barbearia.infrastructure.persistence.entities.JpaFuncionario;
import com.barbearia.infrastructure.persistence.repositories.FuncionarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service para gerenciamento de Links de Acesso de Profissionais.
 * 
 * Responsabilidades:
 * - Geração de tokens UUID únicos
 * - Validação de tokens (ativo E não expirado)
 * - Controle de ativação/desativação
 * - Atualização de expiração
 */
@Service
@Transactional
public class ProfissionalLinkService {

    private final FuncionarioRepository funcionarioRepository;

    public ProfissionalLinkService(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    /**
     * Gera link de acesso para profissional.
     * 
     * @param barbeariaId   ID da barbearia (validação de propriedade)
     * @param funcionarioId ID do profissional
     * @param dataExpiracao Data de expiração (null = sem expiração)
     * @return DTO com link gerado
     */
    @SuppressWarnings("null")
    public FuncionarioLinkResponseDto gerarLinkAcesso(Long barbeariaId, Long funcionarioId,
            LocalDateTime dataExpiracao) {
        JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        // Valida que funcionário pertence à barbearia
        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Profissional não pertence a esta barbearia");
        }

        // Gera novo token UUID
        String token = UUID.randomUUID().toString();
        funcionario.setAccessToken(token);
        funcionario.setTokenAtivo(true);
        funcionario.setTokenGeradoEm(LocalDateTime.now());
        funcionario.setTokenExpiraEm(dataExpiracao);

        funcionarioRepository.save(funcionario);

        String linkAcesso = "/api/profissional/" + token + "/dashboard";

        return new FuncionarioLinkResponseDto(
                funcionario.getId(),
                funcionario.getNome(),
                linkAcesso,
                funcionario.getTokenAtivo(),
                funcionario.getTokenGeradoEm(),
                funcionario.getTokenExpiraEm());
    }

    /**
     * Valida se token é válido (existe, ativo E não expirado).
     * 
     * @param accessToken Token a validar
     * @return Funcionário se token válido
     * @throws IllegalArgumentException se token inválido
     */
    @Transactional(readOnly = true)
    public JpaFuncionario validarToken(String accessToken) {
        return funcionarioRepository.findByTokenValidoComExpiracao(accessToken, LocalDateTime.now())
                .orElseThrow(() -> new IllegalArgumentException("Token inválido, expirado ou inativo"));
    }

    /**
     * Desativa link de acesso.
     */
    @SuppressWarnings("null")
    public void desativarLink(Long barbeariaId, Long funcionarioId) {
        JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Profissional não pertence a esta barbearia");
        }

        funcionario.setTokenAtivo(false);
        funcionarioRepository.save(funcionario);
    }

    /**
     * Reativa link de acesso existente.
     */
    @SuppressWarnings("null")
    public FuncionarioLinkResponseDto reativarLink(Long barbeariaId, Long funcionarioId,
            LocalDateTime novaDataExpiracao) {
        JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Profissional não pertence a esta barbearia");
        }

        if (funcionario.getAccessToken() == null) {
            throw new IllegalStateException("Profissional não possui link gerado. Use gerarLinkAcesso()");
        }

        funcionario.setTokenAtivo(true);
        funcionario.setTokenExpiraEm(novaDataExpiracao);
        funcionarioRepository.save(funcionario);

        String linkAcesso = "/api/profissional/" + funcionario.getAccessToken() + "/dashboard";

        return new FuncionarioLinkResponseDto(
                funcionario.getId(),
                funcionario.getNome(),
                linkAcesso,
                funcionario.getTokenAtivo(),
                funcionario.getTokenGeradoEm(),
                funcionario.getTokenExpiraEm());
    }

    /**
     * Atualiza data de expiração do token.
     */
    @SuppressWarnings("null")
    public void atualizarExpiracao(Long barbeariaId, Long funcionarioId, LocalDateTime dataExpiracao) {
        JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Profissional não pertence a esta barbearia");
        }

        if (funcionario.getAccessToken() == null) {
            throw new IllegalStateException("Profissional não possui link gerado");
        }

        funcionario.setTokenExpiraEm(dataExpiracao);
        funcionarioRepository.save(funcionario);
    }

    /**
     * Obtém dados do profissional por token (após validação).
     */
    @Transactional(readOnly = true)
    public JpaFuncionario obterDadosPorToken(String accessToken) {
        return validarToken(accessToken);
    }

    /**
     * Consulta status do link de um profissional.
     */
    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public FuncionarioLinkResponseDto consultarStatusLink(Long barbeariaId, Long funcionarioId) {
        JpaFuncionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException("Profissional não encontrado"));

        if (!funcionario.getBarbeariaId().equals(barbeariaId)) {
            throw new IllegalArgumentException("Profissional não pertence a esta barbearia");
        }

        if (funcionario.getAccessToken() == null) {
            return null; // Sem link gerado
        }

        String linkAcesso = "/api/profissional/" + funcionario.getAccessToken() + "/dashboard";

        return new FuncionarioLinkResponseDto(
                funcionario.getId(),
                funcionario.getNome(),
                linkAcesso,
                funcionario.getTokenAtivo(),
                funcionario.getTokenGeradoEm(),
                funcionario.getTokenExpiraEm());
    }
}
