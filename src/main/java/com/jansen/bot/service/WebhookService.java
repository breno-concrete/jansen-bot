package com.jansen.bot.service;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.model.ClaudeAction;
import com.jansen.bot.model.EvolutionWebhookPayload;
import com.jansen.bot.rehearsal.adapters.in.InterpretadorDeVoto;
import com.jansen.bot.rehearsal.adapters.in.VotoInterpretado;
import com.jansen.bot.rehearsal.application.RehearsalVotingService;
import com.jansen.bot.rehearsal.ports.RehearsalRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Orquestra o fluxo completo: webhook → Gemini → ação → resposta WhatsApp.
 */
@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final GeminiService geminiService;
    private final ActionDispatcher actionDispatcher;
    private final EvolutionClient evolutionClient;
    private final InterpretadorDeVoto interpretadorDeVoto;
    private final RehearsalVotingService votacao;
    private final RehearsalRepositoryPort ensaios;

    public WebhookService(GeminiService geminiService,
                          ActionDispatcher actionDispatcher,
                          EvolutionClient evolutionClient,
                          InterpretadorDeVoto interpretadorDeVoto,
                          RehearsalVotingService votacao,
                          RehearsalRepositoryPort ensaios) {
        this.geminiService = geminiService;
        this.actionDispatcher = actionDispatcher;
        this.evolutionClient = evolutionClient;
        this.interpretadorDeVoto = interpretadorDeVoto;
        this.votacao = votacao;
        this.ensaios = ensaios;
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

        // 0. Atalho sem IA (T024, P-031): com ensaio aberto, "sim"/"não" (+ tipo) é voto direto.
        // O serviço já responde ao integrante (FR-005) ou o ignora em silêncio (FR-020).
        Optional<VotoInterpretado> voto = ensaios.buscarComVotacaoAberta().isEmpty()
                ? Optional.empty()
                : interpretadorDeVoto.interpretar(message);
        if (voto.isPresent()) {
            log.info("Voto reconhecido sem IA: {} para a mensagem '{}'", voto.get(), message);
            votacao.registrarVoto(phone, voto.get().escolha(), voto.get().tipo());
            return;
        }

        // 1. Envia para Gemini interpretar
        ClaudeAction action = geminiService.interpret(phone, message);

        // 2. Executa a ação e obtém resposta
        String response = actionDispatcher.dispatch(phone, action);

        // 3. Usa resposta da Gemini se o dispatcher não gerou uma específica (null).
        // Texto vazio = "não envie nada": o serviço de votação já respondeu (T024).
        if (response == null) {
            response = action.resposta();
        }
        if (response == null || response.isBlank()) {
            return;
        }

        // 4. Envia resposta via Evolution API
        evolutionClient.sendTextMessage(phone, response);
    }
}
