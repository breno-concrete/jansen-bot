package com.jansen.bot.service;

import com.jansen.bot.client.GeminiClient;
import com.jansen.bot.model.BandContext;
import com.jansen.bot.model.ClaudeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orquestra chamada à Gemini API com contexto da banda.
 */
@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final GeminiClient geminiClient;
    private final ContextService contextService;

    public GeminiService(GeminiClient geminiClient, ContextService contextService) {
        this.geminiClient = geminiClient;
        this.contextService = contextService;
    }

    /**
     * Interpreta mensagem do membro e retorna ação estruturada.
     */
    public ClaudeAction interpret(String memberPhone, String message) {
        BandContext context = contextService.buildContext(memberPhone);
        String contextJson = contextService.toJson(context);

        log.info("Interpretando mensagem de {}: {}", memberPhone, message);
        ClaudeAction action = geminiClient.processMessage(message, contextJson);
        log.info("Ação detectada: {}", action.acao());

        return action;
    }
}
