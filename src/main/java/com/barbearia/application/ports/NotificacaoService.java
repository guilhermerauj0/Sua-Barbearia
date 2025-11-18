package com.barbearia.application.ports;

import java.util.concurrent.CompletableFuture;

/**
 * Porta para abstração do serviço de notificações.
 * Define o contrato para envio de mensagens via diferentes canais.
 */
public interface NotificacaoService {

    /**
     * Envia uma mensagem de texto para um número WhatsApp.
     *
     * @param numeroDestino Número de destino no formato internacional (ex: +5511999999999)
     * @param mensagem Conteúdo da mensagem
     * @return CompletableFuture com o resultado do envio
     */
    CompletableFuture<Void> enviarMensagemWhatsApp(String numeroDestino, String mensagem);

    /**
     * Verifica se o serviço de notificação está disponível.
     *
     * @return true se o serviço está operacional
     */
    boolean isDisponivel();
}