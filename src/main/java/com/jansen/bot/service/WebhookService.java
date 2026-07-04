package com.jansen.bot.service;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.model.ClaudeAction;
import com.jansen.bot.model.EvolutionWebhookPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orquestra o fluxo completo: webhook → Gemini → ação → resposta WhatsApp.
 */
@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final GeminiService geminiService;
    private final ActionDispatcher actionDispatcher;
    private final EvolutionClient evolutionClient;

    public WebhookService(GeminiService geminiService,
                          ActionDispatcher actionDispatcher,
                          EvolutionClient evolutionClient) {
        this.geminiService = geminiService;
        this.actionDispatcher = actionDispatcher;
        this.evolutionClient = evolutionClient;
    }

    /**
     * Processa payload do webhook da Evolution API.
     */
    public void processWebhook(EvolutionWebhookPayload payload) {
        if (!payload.isIncomingMessage()) {
            log.debug("Ignorando evento: {}", payload.event());
            return;
        }

        String phone = payload.extractSenderPhone();
        String message = payload.extractMessageText();
        String senderName = payload.extractSenderName();

        log.info("Mensagem recebida de {} ({}): {}", senderName, phone, message);

        // 1. Envia para Gemini interpretar
        ClaudeAction action = geminiService.interpret(phone, message);

        // 2. Executa a ação e obtém resposta
        String response = actionDispatcher.dispatch(phone, action);

        // 3. Usa resposta da Gemini se o dispatcher não gerou uma específica
        if (response == null || response.isBlank()) {
            response = action.resposta();
        }

        // 4. Envia resposta via Evolution API
        evolutionClient.sendTextMessage(phone, response);
    }
}
