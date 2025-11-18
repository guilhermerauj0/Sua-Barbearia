package com.barbearia.application.observers;

/**
 * Observer detalhado para eventos específicos de agendamento.
 * Extensão da interface AgendamentoObserver com informações mais ricas.
 */
public interface AgendamentoEventObserver {

    /**
     * Chamado quando um novo agendamento é criado.
     *
     * @param agendamentoId ID do agendamento
     * @param clienteNome Nome do cliente
     * @param clienteTelefone Telefone do cliente
     * @param servicoNome Nome do serviço
     * @param dataHora Data e hora do agendamento
     * @param barbeariaNome Nome da barbearia
     */
    void onAgendamentoCriado(Long agendamentoId, String clienteNome, String clienteTelefone,
                           String servicoNome, String dataHora, String barbeariaNome);

    /**
     * Chamado quando um agendamento é confirmado.
     *
     * @param agendamentoId ID do agendamento
     * @param clienteNome Nome do cliente
     * @param clienteTelefone Telefone do cliente
     * @param servicoNome Nome do serviço
     * @param dataHora Data e hora do agendamento
     * @param barbeariaNome Nome da barbearia
     */
    void onAgendamentoConfirmado(Long agendamentoId, String clienteNome, String clienteTelefone,
                               String servicoNome, String dataHora, String barbeariaNome);

    /**
     * Chamado quando um agendamento é cancelado.
     *
     * @param agendamentoId ID do agendamento
     * @param clienteNome Nome do cliente
     * @param clienteTelefone Telefone do cliente
     * @param servicoNome Nome do serviço
     * @param dataHora Data e hora do agendamento
     * @param barbeariaNome Nome da barbearia
     * @param motivoCancelamento Motivo do cancelamento (opcional)
     */
    void onAgendamentoCancelado(Long agendamentoId, String clienteNome, String clienteTelefone,
                              String servicoNome, String dataHora, String barbeariaNome,
                              String motivoCancelamento);

    /**
     * Chamado quando um agendamento é reagendado.
     *
     * @param agendamentoId ID do agendamento
     * @param clienteNome Nome do cliente
     * @param clienteTelefone Telefone do cliente
     * @param servicoNome Nome do serviço
     * @param dataHoraAntiga Data e hora antiga
     * @param dataHoraNova Data e hora nova
     * @param barbeariaNome Nome da barbearia
     */
    void onAgendamentoReagendado(Long agendamentoId, String clienteNome, String clienteTelefone,
                               String servicoNome, String dataHoraAntiga, String dataHoraNova,
                               String barbeariaNome);
}