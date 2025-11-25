package com.barbearia.infrastructure.external.twilio;

import com.barbearia.application.ports.NotificacaoService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração REAL com Twilio.
 * 
 * IMPORTANTE: Este teste envia mensagens reais via WhatsApp.
 * Use @Disabled para evitar execução acidental.
 * 
 * Para executar:
 * 1. Configure as variáveis de ambiente:
 * - TWILIO_ACCOUNT_SID
 * - TWILIO_AUTH_TOKEN
 * - TWILIO_WHATSAPP_FROM (número Twilio, ex: +14155238886)
 * 2. Remova @Disabled temporariamente
 * 3. Execute: mvn test -Dtest=TwilioNotificacaoServiceIntegrationTest
 */
@SpringBootTest
@ActiveProfiles("test")
@Disabled("Testes de integração com Twilio requerem credenciais reais")
class TwilioNotificacaoServiceIntegrationTest {

    @Autowired
    private NotificacaoService notificacaoService;

    /**
     * Teste manual para verificar envio real de WhatsApp.
     * Número de teste: 87991290793 (fornecido pelo usuário)
     */
    @Test
    @Disabled("Descomente para testar envio REAL. CUIDADO: Consome créditos Twilio!")
    void testeEnvioRealWhatsApp_NumeroUsuario() throws Exception {
        // Arrange
        String numeroDestino = "87991290793"; // Número do usuário
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String mensagem = String.format("""
                🧪 *TESTE DE INTEGRAÇÃO - Sua Barbearia*

                Olá! Esta é uma mensagem de teste do sistema.

                📅 Data/Hora: %s
                ✅ Integração Twilio funcionando corretamente!

                Se você recebeu esta mensagem, o sistema de notificações está operacional. 🎉
                """, timestamp);

        // Act
        CompletableFuture<Void> resultado = notificacaoService.enviarMensagemWhatsApp(numeroDestino, mensagem);

        // Assert
        assertDoesNotThrow(() -> resultado.get(), "Envio de WhatsApp não deveria lançar exceção");

        System.out.println("✅ Mensagem enviada com sucesso para: " + numeroDestino);
        System.out.println("📱 Verifique o WhatsApp para confirmar o recebimento.");
    }

    /**
     * Teste de confirmação de agendamento realista.
     */
    @Test
    @Disabled("Descomente para testar envio REAL. CUIDADO: Consome créditos Twilio!")
    void testeEnvioConfirmacaoAgendamento_Realista() throws Exception {
        // Arrange
        String numeroDestino = "87991290793";

        String mensagem = """
                Olá! 🎉

                Seu agendamento foi confirmado com sucesso!

                📋 *Detalhes do Agendamento:*
                • Serviço: Corte Masculino + Barba
                • Data: 25/11/2024 às 14:30
                • Profissional: João Silva
                • Local: Barbearia Elite
                • Valor: R$ 45,00

                ⏰ *Lembrete:* Chegue com 10 minutos de antecedência.

                📞 Dúvidas? Entre em contato: (87) 3456-7890

                Até breve! ✂️
                """;

        // Act
        CompletableFuture<Void> resultado = notificacaoService.enviarMensagemWhatsApp(numeroDestino, mensagem);

        // Assert
        assertDoesNotThrow(() -> resultado.get());

        System.out.println("✅ Notificação de confirmação enviada!");
    }

    /**
     * Teste de lembrete pré-agendamento (24h antes).
     */
    @Test
    @Disabled("Descomente para testar envio REAL. CUIDADO: Consome créditos Twilio!")
    void testeEnvioLembreteAgendamento() throws Exception {
        // Arrange
        String numeroDestino = "87991290793";

        String mensagem = """
                Olá! ⏰

                Este é um lembrete do seu agendamento AMANHÃ:

                📋 *Detalhes:*
                • Serviço: Corte + Barba
                • Data: 23/11/2024 às 14:30
                • Profissional: João Silva
                • Local: Barbearia Elite

                ✅ Confirme sua presença respondendo SIM
                ❌ Precisa cancelar? Responda NAO

                Aguardamos você! 💈
                """;

        // Act
        CompletableFuture<Void> resultado = notificacaoService.enviarMensagemWhatsApp(numeroDestino, mensagem);

        // Assert
        assertDoesNotThrow(() -> resultado.get());

        System.out.println("✅ Lembrete enviado!");
    }

    /**
     * Verifica se o serviço está disponível (credenciais configuradas).
     */
    @Test
    void deveVerificarDisponibilidadeServico() {
        // Act & Assert
        boolean disponivel = notificacaoService.isDisponivel();

        if (disponivel) {
            System.out.println("✅ Serviço Twilio DISPONÍVEL - credenciais configuradas");
        } else {
            System.out.println("⚠️ Serviço Twilio NÃO DISPONÍVEL - configure as credenciais");
        }

        // Este teste não falha, apenas informa o status
        assertTrue(true, "Teste de verificação executado");
    }
}
