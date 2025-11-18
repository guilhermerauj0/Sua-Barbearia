package com.barbearia.application.dto;

import com.barbearia.domain.enums.StatusAgendamento;
import java.time.LocalDateTime;

/**
 * DTO detalhado para listagem de agendamentos da barbearia.
 * 
 * Inclui informações completas do agendamento e dados relacionados:
 * - Dados do agendamento (id, data/hora, status, observações)
 * - Informações do cliente (id, nome, telefone)
 * - Informações do serviço (id, nome, tipo, preço, duração)
 * - Informações do funcionário (id, nome, profissão)
 * 
 * Usado pelo endpoint GET /api/barbearias/meus-agendamentos
 * 
 * @author Sua Barbearia Team
 */
public record AgendamentoBarbeariaDto(
    Long id,
    LocalDateTime dataHora,
    StatusAgendamento status,
    String observacoes,
    
    // Dados do cliente
    Long clienteId,
    String clienteNome,
    String clienteTelefone,
    
    // Dados do serviço
    Long servicoId,
    String servicoNome,
    String servicoTipo,
    Double servicoPreco,
    Integer servicoDuracao,
    
    // Dados do funcionário
    Long funcionarioId,
    String funcionarioNome,
    String funcionarioProfissao,
    
    // Metadados
    LocalDateTime dataCriacao,
    LocalDateTime dataAtualizacao
) {
}
