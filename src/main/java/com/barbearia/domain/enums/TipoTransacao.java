package com.barbearia.domain.enums;

/**
 * Tipo de transação financeira.
 */
public enum TipoTransacao {
    DESPESA("Despesa"),
    RECEITA_EXTRA("Receita Extra");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
