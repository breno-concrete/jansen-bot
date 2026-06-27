package com.jansen.bot.service;

import com.jansen.bot.client.ClaudeClient;
import com.jansen.bot.model.BandContext;
import com.jansen.bot.model.ClaudeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Orquestra chamada à Claude API com contexto da banda.
 */
@Service
public class ClaudeService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeService.class);

    private final ClaudeClient claudeClient;
    private final ContextService contextService;

    public ClaudeService(ClaudeClient claudeClient, ContextService contextService) {
        this.claudeClient = claudeClient;
        this.contextService = contextService;
    }

    /**
     * Interpreta mensagem do membro e retorna ação estruturada.
     */
    public ClaudeAction interpret(String memberPhone, String message) {
        BandContext context = contextService.buildContext(memberPhone);
        String contextJson = contextService.toJson(context);

        log.info("Interpretando mensagem de {}: {}", memberPhone, message);
        ClaudeAction action = claudeClient.processMessage(message, contextJson);
        log.info("Ação detectada: {}", action.acao());

        return action;
    }
}
