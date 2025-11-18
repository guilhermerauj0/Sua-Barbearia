package com.barbearia.application.dto;

import com.barbearia.domain.enums.StatusAgendamento;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para atualização de status de agendamento.
 * 
 * Usado pelo endpoint PATCH /api/barbearias/agendamentos/{id}
 * para permitir que a barbearia altere o status do agendamento.
 * 
 * Apenas o status pode ser atualizado via PATCH.
 * 
 * @author Sua Barbearia Team
 */
public record AgendamentoUpdateDto(
    @NotNull(message = "Status é obrigatório")
    StatusAgendamento status
) {
}
