package com.barbearia.application.dto;

import com.barbearia.domain.enums.StatusAgendamento;
import java.time.LocalDateTime;

/**
 * DTO estendido para o dashboard do profissional.
 * Inclui duração e hora de término para visualização correta na agenda.
 */
public record AgendamentoProfissionalDto(
        Long id,
        Long clienteId,
        Long barbeariaId,
        Long servicoId,
        Long funcionarioId,
        LocalDateTime dataHora,
        LocalDateTime dataHoraFim, // Calculado: dataHora + duracao
        Integer duracao, // Em minutos (do serviço)
        StatusAgendamento status,
        String observacoes,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        Boolean avaliado) {
}
