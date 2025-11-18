package com.barbearia.application.dto;

import java.time.LocalDateTime;

/**
 * DTO que representa um cliente atendido pela barbearia.
 * 
 * <p>Utilizado para listagens de clientes que já foram atendidos ao menos uma vez.</p>
 * <p>Contém informações básicas do cliente e estatísticas de atendimento.</p>
 * 
 * @param id ID do cliente
 * @param nome Nome do cliente
 * @param telefone Telefone do cliente
 * @param email Email do cliente
 * @param totalAgendamentos Quantidade total de agendamentos realizados
 * @param ultimoAgendamento Data/hora do último agendamento
 * @param ativo Status de ativação do cliente
 * @param anonimizado Indica se os dados foram anonimizados (LGPD)
 * 
 * @author Sua Barbearia Team
 * @since 1.0
 */
public record ClienteAtendidoDto(
    Long id,
    String nome,
    String telefone,
    String email,
    Long totalAgendamentos,
    LocalDateTime ultimoAgendamento,
    Boolean ativo,
    Boolean anonimizado
) {}
