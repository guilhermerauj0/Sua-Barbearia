package com.barbearia.application.dto;

import com.barbearia.domain.enums.StatusAgendamento;
import java.time.LocalDateTime;

/**
 * DTO resumido para listagem de agendamentos.
 * 
 * Contém apenas informações essenciais para exibição em listas:
 * - Dados do agendamento (id, data/hora, status)
 * - Nomes das entidades relacionadas (para evitar buscas adicionais no front-end)
 * - Valor do serviço (futuro)
 * 
 * Este DTO é otimizado para transferência de dados, economizando banda.
 * 
 * @author Sua Barbearia Team
 */
public record AgendamentoBriefDto(
        Long id,
        LocalDateTime dataHora,
        StatusAgendamento status,
        String nomeBarbearia,
        String nomeBarbeiro,  // Pode ser null se ainda não atribuído
        String nomeServico,
        String observacoes
) {
}
