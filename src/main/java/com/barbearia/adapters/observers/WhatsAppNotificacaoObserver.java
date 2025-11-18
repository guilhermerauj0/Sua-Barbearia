package com.barbearia.adapters.observers;

import com.barbearia.application.observers.AgendamentoEventObserver;
import com.barbearia.application.observers.AgendamentoObserver;
import com.barbearia.application.ports.NotificacaoService;
import com.barbearia.domain.enums.StatusAgendamento;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Observer que envia notificações WhatsApp para eventos de agendamento.
 * Implementa tanto a interface genérica quanto a específica para eventos detalhados.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsAppNotificacaoObserver implements AgendamentoObserver, AgendamentoEventObserver {

    private final NotificacaoService notificacaoService;

    @Override
    @Async
    public void onStatusChanged(Long agendamentoId, StatusAgendamento statusAnterior,
                              StatusAgendamento statusNovo, Long clienteId, Long barbeariaId) {
        log.info("Mudança de status detectada - Agendamento: {}, De: {} Para: {}",
                agendamentoId, statusAnterior, statusNovo);

        // Mapeia mudança de status para eventos específicos
        switch (statusNovo) {
            case CONFIRMADO:
                // Para mudança de status genérica, não temos dados detalhados
                // Apenas logamos que uma mudança ocorreu
                log.info("Agendamento {} confirmado via mudança de status", agendamentoId);
                break;
            case CANCELADO:
                log.info("Agendamento {} cancelado via mudança de status", agendamentoId);
                break;
            default:
                log.debug("Mudança de status para {} não requer notificação específica", statusNovo);
        }
    }

    @Override
    @Async
    public void onAgendamentoCriado(Long agendamentoId, String clienteNome, String clienteTelefone,
                                  String servicoNome, String dataHora, String barbeariaNome) {
        String mensagem = criarMensagemAgendamentoCriado(clienteNome, servicoNome, dataHora, barbeariaNome);

        log.info("Enviando notificação de agendamento criado para {} - Agendamento: {}",
                clienteTelefone, agendamentoId);

        notificacaoService.enviarMensagemWhatsApp(clienteTelefone, mensagem)
            .exceptionally(throwable -> {
                log.error("Falha ao enviar notificação de agendamento criado: {}", throwable.getMessage());
                return null;
            });
    }

    @Override
    @Async
    public void onAgendamentoConfirmado(Long agendamentoId, String clienteNome, String clienteTelefone,
                                      String servicoNome, String dataHora, String barbeariaNome) {
        String mensagem = criarMensagemAgendamentoConfirmado(clienteNome, servicoNome, dataHora, barbeariaNome);

        log.info("Enviando notificação de agendamento confirmado para {} - Agendamento: {}",
                clienteTelefone, agendamentoId);

        notificacaoService.enviarMensagemWhatsApp(clienteTelefone, mensagem)
            .exceptionally(throwable -> {
                log.error("Falha ao enviar notificação de agendamento confirmado: {}", throwable.getMessage());
                return null;
            });
    }

    @Override
    @Async
    public void onAgendamentoCancelado(Long agendamentoId, String clienteNome, String clienteTelefone,
                                     String servicoNome, String dataHora, String barbeariaNome,
                                     String motivoCancelamento) {
        String mensagem = criarMensagemAgendamentoCancelado(clienteNome, servicoNome, dataHora,
                                                          barbeariaNome, motivoCancelamento);

        log.info("Enviando notificação de agendamento cancelado para {} - Agendamento: {}",
                clienteTelefone, agendamentoId);

        notificacaoService.enviarMensagemWhatsApp(clienteTelefone, mensagem)
            .exceptionally(throwable -> {
                log.error("Falha ao enviar notificação de agendamento cancelado: {}", throwable.getMessage());
                return null;
            });
    }

    @Override
    @Async
    public void onAgendamentoReagendado(Long agendamentoId, String clienteNome, String clienteTelefone,
                                      String servicoNome, String dataHoraAntiga, String dataHoraNova,
                                      String barbeariaNome) {
        String mensagem = criarMensagemAgendamentoReagendado(clienteNome, servicoNome, dataHoraAntiga,
                                                           dataHoraNova, barbeariaNome);

        log.info("Enviando notificação de agendamento reagendado para {} - Agendamento: {}",
                clienteTelefone, agendamentoId);

        notificacaoService.enviarMensagemWhatsApp(clienteTelefone, mensagem)
            .exceptionally(throwable -> {
                log.error("Falha ao enviar notificação de agendamento reagendado: {}", throwable.getMessage());
                return null;
            });
    }

    /**
     * Cria mensagem para agendamento criado.
     */
    private String criarMensagemAgendamentoCriado(String clienteNome, String servicoNome,
                                                 String dataHora, String barbeariaNome) {
        return String.format(
            "Olá %s! 🎉\n\n" +
            "Seu agendamento foi criado com sucesso!\n\n" +
            "📅 Serviço: %s\n" +
            "📆 Data/Hora: %s\n" +
            "🏪 Barbearia: %s\n\n" +
            "Aguarde a confirmação da barbearia. Você será notificado quando seu horário for confirmado!\n\n" +
            "Qualquer dúvida, entre em contato conosco.",
            clienteNome, servicoNome, dataHora, barbeariaNome
        );
    }

    /**
     * Cria mensagem para agendamento confirmado.
     */
    private String criarMensagemAgendamentoConfirmado(String clienteNome, String servicoNome,
                                                     String dataHora, String barbeariaNome) {
        return String.format(
            "Olá %s! ✅\n\n" +
            "Seu agendamento foi CONFIRMADO!\n\n" +
            "✂️ Serviço: %s\n" +
            "📆 Data/Hora: %s\n" +
            "🏪 Barbearia: %s\n\n" +
            "Estamos te esperando! Chegue alguns minutos antes para ser atendido no horário marcado.\n\n" +
            "Até logo! 💇‍♂️",
            clienteNome, servicoNome, dataHora, barbeariaNome
        );
    }

    /**
     * Cria mensagem para agendamento cancelado.
     */
    private String criarMensagemAgendamentoCancelado(String clienteNome, String servicoNome,
                                                    String dataHora, String barbeariaNome,
                                                    String motivoCancelamento) {
        String motivo = motivoCancelamento != null && !motivoCancelamento.isEmpty()
            ? "\n📝 Motivo: " + motivoCancelamento
            : "";

        return String.format(
            "Olá %s! ❌\n\n" +
            "Infelizmente seu agendamento foi CANCELADO.\n\n" +
            "✂️ Serviço: %s\n" +
            "📆 Data/Hora: %s\n" +
            "🏪 Barbearia: %s%s\n\n" +
            "Entre em contato conosco para reagendar seu atendimento.\n\n" +
            "Desculpe pelo inconveniente!",
            clienteNome, servicoNome, dataHora, barbeariaNome, motivo
        );
    }

    /**
     * Cria mensagem para agendamento reagendado.
     */
    private String criarMensagemAgendamentoReagendado(String clienteNome, String servicoNome,
                                                     String dataHoraAntiga, String dataHoraNova,
                                                     String barbeariaNome) {
        return String.format(
            "Olá %s! 🔄\n\n" +
            "Seu agendamento foi REAGENDADO!\n\n" +
            "✂️ Serviço: %s\n" +
            "📆 De: %s\n" +
            "📆 Para: %s\n" +
            "🏪 Barbearia: %s\n\n" +
            "Seu novo horário foi confirmado. Estamos te esperando!\n\n" +
            "Qualquer dúvida, entre em contato conosco.",
            clienteNome, servicoNome, dataHoraAntiga, dataHoraNova, barbeariaNome
        );
    }
}
