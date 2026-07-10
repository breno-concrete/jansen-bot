package com.jansen.bot.service;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.model.BotAction;
import com.jansen.bot.model.ClaudeAction;
import com.jansen.bot.model.EvolutionWebhookPayload;
import com.jansen.bot.repository.GoogleSheetsRepository;
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
    private final GoogleSheetsRepository repository;

    public WebhookService(GeminiService geminiService,
                          ActionDispatcher actionDispatcher,
                          EvolutionClient evolutionClient,
                          GoogleSheetsRepository repository) {
        this.geminiService = geminiService;
        this.actionDispatcher = actionDispatcher;
        this.evolutionClient = evolutionClient;
        this.repository = repository;
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

        // 0. Atalho determinístico (Fast-path) para Sim/Não quando há ensaio agendado
        ClaudeAction action = checkFastPresenceShortcut(message);
        if (action == null) {
            // 1. Envia para Gemini interpretar
            action = geminiService.interpret(phone, message);
        } else {
            log.info("Atalho de presença detectado: {} para a mensagem '{}'", action.acao(), message);
        }

        // 2. Executa a ação e obtém resposta
        String response = actionDispatcher.dispatch(phone, action);

        // 3. Usa resposta da Gemini se o dispatcher não gerou uma específica
        if (response == null || response.isBlank()) {
            response = action.resposta();
        }

        // 4. Envia resposta via Evolution API
        evolutionClient.sendTextMessage(phone, response);
    }

    private ClaudeAction checkFastPresenceShortcut(String msg) {
        if (msg == null || msg.isBlank()) return null;
        boolean temEnsaioAgendado = repository.findNextScheduledRehearsal()
                .filter(r -> "AGENDADO".equalsIgnoreCase(r.status()))
                .isPresent();
        if (!temEnsaioAgendado) return null;

        String clean = msg.trim().toLowerCase();
        if (clean.equals("sim") || clean.equals("s") || clean.equals("vou") || clean.equals("tô dentro") || clean.equals("to dentro") || clean.equals("confirmo")) {
            return new ClaudeAction(BotAction.CONFIRMAR_PRESENCA, "Show! Te espero no ensaio 🎸", null);
        }
        if (clean.equals("não") || clean.equals("nao") || clean.equals("n") || clean.equals("não vou") || clean.equals("nao vou") || clean.equals("tô fora") || clean.equals("to fora") || clean.equals("não posso") || clean.equals("nao posso")) {
            return new ClaudeAction(BotAction.NEGAR_PRESENCA, "Beleza, anotei que você não vai 👍", null);
        }
        return null;
    }
}
