package com.barbearia.application.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa os detalhes completos de um cliente atendido.
 * 
 * <p>Inclui informações detalhadas do cliente e histórico de agendamentos.</p>
 * <p>Usado para visualização detalhada de um cliente específico.</p>
 * 
 * @param id ID do cliente
 * @param nome Nome do cliente
 * @param telefone Telefone do cliente
 * @param email Email do cliente
 * @param documento CPF do cliente
 * @param endereco Endereço do cliente
 * @param totalAgendamentos Total de agendamentos realizados
 * @param agendamentosConcluidos Total de agendamentos concluídos
 * @param agendamentosCancelados Total de agendamentos cancelados
 * @param primeiroAgendamento Data/hora do primeiro agendamento
 * @param ultimoAgendamento Data/hora do último agendamento
 * @param ativo Status de ativação do cliente
 * @param anonimizado Indica se os dados foram anonimizados (LGPD)
 * @param dataCriacao Data de criação do cadastro
 * @param dataAtualizacao Data da última atualização
 * @param agendamentos Lista de agendamentos do cliente
 * 
 * @author Sua Barbearia Team
 * @since 1.0
 */
public record ClienteDetalhesDto(
    Long id,
    String nome,
    String telefone,
    String email,
    String documento,
    String endereco,
    Long totalAgendamentos,
    Long agendamentosConcluidos,
    Long agendamentosCancelados,
    LocalDateTime primeiroAgendamento,
    LocalDateTime ultimoAgendamento,
    Boolean ativo,
    Boolean anonimizado,
    LocalDateTime dataCriacao,
    LocalDateTime dataAtualizacao,
    List<AgendamentoHistoricoDto> agendamentos
) {
    
    /**
     * DTO interno que representa um agendamento no histórico do cliente.
     * 
     * @param id ID do agendamento
     * @param dataHora Data/hora do agendamento
     * @param status Status do agendamento
     * @param servicoNome Nome do serviço realizado
     * @param funcionarioNome Nome do funcionário que atendeu
     * @param observacoes Observações do agendamento
     */
    public record AgendamentoHistoricoDto(
        Long id,
        LocalDateTime dataHora,
        String status,
        String servicoNome,
        String funcionarioNome,
        String observacoes
    ) {}
}
