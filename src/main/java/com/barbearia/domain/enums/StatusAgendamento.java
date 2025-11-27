package com.barbearia.domain.enums;

/**
 * Enum que representa os possíveis status de um agendamento.
 * 
 * Estados do ciclo de vida de um agendamento:
 * - PENDENTE: Agendamento criado, aguardando confirmação da barbearia
 * - CONFIRMADO: Barbearia confirmou o agendamento
 * - CONCLUIDO: Serviço foi realizado
 * - CANCELADO: Agendamento foi cancelado (por cliente ou barbearia)
 * 
 * @author Sua Barbearia Team
 */
public enum StatusAgendamento {
    /**
     * Agendamento criado, aguardando confirmação
     */
    PENDENTE("Pendente"),

    /**
     * Agendamento confirmado pela barbearia
     */
    CONFIRMADO("Confirmado"),

    /**
     * Serviço realizado com sucesso
     */
    CONCLUIDO("Concluído"),

    /**
     * Agendamento cancelado
     */
    CANCELADO("Cancelado"),

    /**
     * Cliente faltou ao agendamento confirmado
     */
    FALTOU("Faltou");

    private final String descricao;

    StatusAgendamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
