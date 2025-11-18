package com.barbearia.infrastructure.external.twilio;

import com.barbearia.application.ports.NotificacaoService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Implementação do serviço de notificações usando Twilio WhatsApp API.
 * Implementa retry automático e execução assíncrona.
 */
@Slf4j
@Service
public class TwilioNotificacaoService implements NotificacaoService {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000; // 1 segundo

    @Value("${twilio.account.sid:${TWILIO_ACCOUNT_SID}}")
    private String accountSid;

    @Value("${twilio.auth.token:${TWILIO_AUTH_TOKEN}}")
    private String authToken;

    @Value("${twilio.whatsapp.from:${TWILIO_WHATSAPP_FROM:+14155238886}}")
    private String whatsappFrom;

    @Value("${twilio.enabled:true}")
    private boolean enabled;

    /**
     * Inicializa o Twilio SDK se as credenciais estiverem disponíveis.
     */
    private void initializeTwilio() {
        if (accountSid != null && !accountSid.isEmpty() &&
            authToken != null && !authToken.isEmpty()) {
            Twilio.init(accountSid, authToken);
            log.info("Twilio SDK inicializado com sucesso");
        } else {
            log.warn("Credenciais Twilio não configuradas. Notificações serão simuladas.");
        }
    }

    @Override
    @Async
    public CompletableFuture<Void> enviarMensagemWhatsApp(String numeroDestino, String mensagem) {
        return CompletableFuture.runAsync(() -> {
            if (!enabled) {
                log.info("Notificações desabilitadas. Simulando envio para {}: {}", numeroDestino, mensagem);
                return;
            }

            if (!isDisponivel()) {
                log.warn("Serviço Twilio não disponível. Credenciais não configuradas.");
                return;
            }

            String numeroFormatado = formatarNumeroWhatsApp(numeroDestino);

            for (int tentativa = 1; tentativa <= MAX_RETRIES; tentativa++) {
                try {
                    log.info("Tentativa {} de {} para enviar WhatsApp para {}", tentativa, MAX_RETRIES, numeroFormatado);

                    initializeTwilio();

                    Message message = Message.creator(
                        new PhoneNumber("whatsapp:" + numeroFormatado),
                        new PhoneNumber("whatsapp:" + whatsappFrom),
                        mensagem
                    ).create();

                    log.info("WhatsApp enviado com sucesso. SID: {}", message.getSid());
                    return;

                } catch (Exception e) {
                    log.error("Erro ao enviar WhatsApp na tentativa {}: {}", tentativa, e.getMessage());

                    if (tentativa == MAX_RETRIES) {
                        log.error("Falha definitiva ao enviar WhatsApp para {} após {} tentativas", numeroFormatado, MAX_RETRIES);
                        throw new RuntimeException("Falha ao enviar notificação WhatsApp", e);
                    }

                    // Espera exponencial entre tentativas
                    try {
                        Thread.sleep(RETRY_DELAY_MS * tentativa);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread interrompida durante retry", ie);
                    }
                }
            }
        });
    }

    @Override
    public boolean isDisponivel() {
        return accountSid != null && !accountSid.isEmpty() &&
               authToken != null && !authToken.isEmpty() &&
               enabled;
    }

    /**
     * Formata o número de telefone para o padrão WhatsApp.
     * Remove espaços, traços e garante que comece com código do país.
     *
     * @param numero Número a ser formatado
     * @return Número formatado para WhatsApp
     */
    private String formatarNumeroWhatsApp(String numero) {
        if (numero == null) {
            throw new IllegalArgumentException("Número de destino não pode ser nulo");
        }

        // Remove todos os caracteres não numéricos
        String numeroLimpo = numero.replaceAll("[^0-9]", "");

        // Se não começa com código do país, assume Brasil (+55)
        if (!numeroLimpo.startsWith("55")) {
            numeroLimpo = "55" + numeroLimpo;
        }

        log.debug("Número formatado de '{}' para '{}'", numero, numeroLimpo);
        return numeroLimpo;
    }
}