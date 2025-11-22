package com.barbearia.application.dto;

import java.time.LocalDateTime;

/**
 * DTO para requisição de geração/atualização de link de acesso para
 * profissional.
 * 
 * Campo de expiração opcional: se null, o link não expira.
 */
public class FuncionarioLinkRequestDto {

    private LocalDateTime dataExpiracao; // opcional, null = sem expiração

    public FuncionarioLinkRequestDto() {
    }

    public FuncionarioLinkRequestDto(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    // Getters e Setters

    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }
}
