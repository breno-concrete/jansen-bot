package com.jansen.bot.controller;

import com.jansen.bot.model.EvolutionWebhookPayload;
import com.jansen.bot.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller que recebe webhooks da Evolution API.
 */
@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Endpoint principal: Evolution API envia POST aqui quando chega mensagem.
     */
    @PostMapping("/evolution")
    public ResponseEntity<Void> handleEvolutionWebhook(@RequestBody EvolutionWebhookPayload payload) {
        log.debug("Webhook recebido: event={}", payload.event());

        // Processa de forma assíncrona para responder rápido ao webhook
        java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor().submit(() -> webhookService.processWebhook(payload));

        return ResponseEntity.ok().build();
    }

    /**
     * Health check para Docker e Oracle Cloud.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Jansen Bot OK 🎸");
    }
}
