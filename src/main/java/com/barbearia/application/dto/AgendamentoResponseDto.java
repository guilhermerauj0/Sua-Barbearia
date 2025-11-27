package com.barbearia.application.dto;

import com.barbearia.domain.enums.StatusAgendamento;
import java.time.LocalDateTime;

/**
 * DTO para resposta de agendamento (criação e consulta).
 * 
 * Retorna dados completos do agendamento:
 * - Informações básicas do agendamento (id, data/hora, status, observações)
 * - IDs das entidades relacionadas (cliente, barbearia, serviço, funcionário)
 * - Metadados (dataCriação, dataAtualização)
 * 
 * Usado tanto para:
 * - POST /api/agendamentos (criar)
 * - GET /api/agendamentos/{id} (consultar)
 * 
 * @author Sua Barbearia Team
 */
public record AgendamentoResponseDto(
        Long id,
        Long clienteId,
        Long barbeariaId,
        Long servicoId,
        Long funcionarioId,
        LocalDateTime dataHora,
        StatusAgendamento status,
        String observacoes,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao,
        Boolean avaliado) {
}
