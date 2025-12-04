package com.barbearia.adapters.observers;

import com.barbearia.application.ports.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WhatsAppNotificacaoObserverTest {

    @Mock
    private NotificacaoService notificacaoService;

    private WhatsAppNotificacaoObserver observer;

    @BeforeEach
    void setUp() {
        observer = new WhatsAppNotificacaoObserver(notificacaoService);
    }

    @Test
    void deveEnviarNotificacaoQuandoAgendamentoCriado() {
        // Arrange
        when(notificacaoService.enviarMensagemWhatsApp(anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        observer.onAgendamentoCriado(
            1L,
            "João Silva",
            "+5511999999999",
            "Corte de Cabelo",
            "15/12/2024 às 14:00",
            "Barbearia do João"
        );

        // Assert
        verify(notificacaoService, timeout(1000)).enviarMensagemWhatsApp(
            eq("+5511999999999"),
            contains("Olá João Silva! 🎉")
        );
    }

    @Test
    void deveEnviarNotificacaoQuandoAgendamentoConfirmado() {
        // Arrange
        when(notificacaoService.enviarMensagemWhatsApp(anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        observer.onAgendamentoConfirmado(
            1L,
            "João Silva",
            "+5511999999999",
            "Corte de Cabelo",
            "15/12/2024 às 14:00",
            "Barbearia do João"
        );

        // Assert
        verify(notificacaoService, timeout(1000)).enviarMensagemWhatsApp(
            eq("+5511999999999"),
            contains("Olá João Silva! ✅")
        );
    }

    @Test
    void deveEnviarNotificacaoQuandoAgendamentoCancelado() {
        // Arrange
        when(notificacaoService.enviarMensagemWhatsApp(anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        observer.onAgendamentoCancelado(
            1L,
            "João Silva",
            "+5511999999999",
            "Corte de Cabelo",
            "15/12/2024 às 14:00",
            "Barbearia do João",
            "Cliente solicitou cancelamento"
        );

        // Assert
        verify(notificacaoService, timeout(1000)).enviarMensagemWhatsApp(
            eq("+5511999999999"),
            contains("Olá João Silva! ❌")
        );
    }

    @Test
    void deveEnviarNotificacaoQuandoAgendamentoReagendado() {
        // Arrange
        when(notificacaoService.enviarMensagemWhatsApp(anyString(), anyString()))
            .thenReturn(CompletableFuture.completedFuture(null));

        // Act
        observer.onAgendamentoReagendado(
            1L,
            "João Silva",
            "+5511999999999",
            "Corte de Cabelo",
            "15/12/2024 às 14:00",
            "16/12/2024 às 15:00",
            "Barbearia do João"
        );

        // Assert
        verify(notificacaoService, timeout(1000)).enviarMensagemWhatsApp(
            eq("+5511999999999"),
            contains("Olá João Silva! 🔄")
        );
    }
}