package com.barbearia.application.observers;

import com.barbearia.domain.enums.StatusAgendamento;

/**
 * Interface Observer para notificações de mudança de status de agendamento.
 * 
 * Implementa o padrão Observer para desacoplar a lógica de notificação
 * do serviço de agendamento.
 * 
 * Quando um agendamento muda de status, todos os observadores registrados
 * são notificados e podem executar ações específicas (enviar email, SMS, etc).
 * 
 * Exemplo de implementações:
 * - EmailNotificationObserver: Envia email para o cliente
 * - SMSNotificationObserver: Envia SMS para o cliente
 * - PushNotificationObserver: Envia notificação push
 * 
 * @author Sua Barbearia Team
 */
public interface AgendamentoObserver {
    
    /**
     * Notifica sobre mudança de status de um agendamento.
     * 
     * @param agendamentoId ID do agendamento que mudou
     * @param statusAnterior Status anterior do agendamento
     * @param statusNovo Novo status do agendamento
     * @param clienteId ID do cliente proprietário do agendamento
     * @param barbeariaId ID da barbearia
     */
    void onStatusChanged(
        Long agendamentoId, 
        StatusAgendamento statusAnterior, 
        StatusAgendamento statusNovo,
        Long clienteId,
        Long barbeariaId
    );
}
