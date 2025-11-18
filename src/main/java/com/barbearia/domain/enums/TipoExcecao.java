package com.barbearia.domain.enums;

/**
 * Enum que representa os tipos de exceções aos horários normais de funcionamento.
 */
public enum TipoExcecao {
    /**
     * Barbearia fechada nesta data (feriado, férias, etc.)
     */
    FECHADO("Fechado"),
    
    /**
     * Horário diferenciado nesta data (ex: véspera de feriado, evento especial)
     */
    HORARIO_ESPECIAL("Horário Especial");

    private final String descricao;

    TipoExcecao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
