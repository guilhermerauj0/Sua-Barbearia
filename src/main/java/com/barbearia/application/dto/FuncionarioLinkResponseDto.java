package com.barbearia.application.dto;

import java.time.LocalDateTime;

/**
 * DTO para resposta de link de acesso do profissional.
 * 
 * Retorna dados do link incluindo token completo, status e validade.
 */
public class FuncionarioLinkResponseDto {

    private Long funcionarioId;
    private String nome;
    private String linkAcesso; // URL completa: /api/profissional/{token}/dashboard
    private Boolean tokenAtivo;
    private LocalDateTime tokenGeradoEm;
    private LocalDateTime tokenExpiraEm; // null = não expira

    public FuncionarioLinkResponseDto() {
    }

    public FuncionarioLinkResponseDto(Long funcionarioId, String nome, String linkAcesso,
            Boolean tokenAtivo, LocalDateTime tokenGeradoEm,
            LocalDateTime tokenExpiraEm) {
        this.funcionarioId = funcionarioId;
        this.nome = nome;
        this.linkAcesso = linkAcesso;
        this.tokenAtivo = tokenAtivo;
        this.tokenGeradoEm = tokenGeradoEm;
        this.tokenExpiraEm = tokenExpiraEm;
    }

    // Getters e Setters

    public Long getFuncionarioId() {
        return funcionarioId;
    }

    public void setFuncionarioId(Long funcionarioId) {
        this.funcionarioId = funcionarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLinkAcesso() {
        return linkAcesso;
    }

    public void setLinkAcesso(String linkAcesso) {
        this.linkAcesso = linkAcesso;
    }

    public Boolean getTokenAtivo() {
        return tokenAtivo;
    }

    public void setTokenAtivo(Boolean tokenAtivo) {
        this.tokenAtivo = tokenAtivo;
    }

    public LocalDateTime getTokenGeradoEm() {
        return tokenGeradoEm;
    }

    public void setTokenGeradoEm(LocalDateTime tokenGeradoEm) {
        this.tokenGeradoEm = tokenGeradoEm;
    }

    public LocalDateTime getTokenExpiraEm() {
        return tokenExpiraEm;
    }

    public void setTokenExpiraEm(LocalDateTime tokenExpiraEm) {
        this.tokenExpiraEm = tokenExpiraEm;
    }
}
