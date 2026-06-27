package com.jansen.bot.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.jansen.bot.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

/**
 * Cliente HTTP para envio de mensagens via Evolution API.
 */
@Component
public class EvolutionClient {

    private static final Logger log = LoggerFactory.getLogger(EvolutionClient.class);

    private final WebClient evolutionWebClient;
    private final AppProperties properties;

    public EvolutionClient(WebClient evolutionWebClient, AppProperties properties) {
        this.evolutionWebClient = evolutionWebClient;
        this.properties = properties;
    }

    /**
     * Envia mensagem de texto para um número de telefone.
     */
    public void sendTextMessage(String phone, String message) {
        String jid = phone.contains("@") ? phone : phone + "@s.whatsapp.net";

        Map<String, Object> body = Map.of(
                "number", jid.replace("@s.whatsapp.net", ""),
                "text", message
        );

        try {
            evolutionWebClient.post()
                    .uri("/message/sendText/{instance}", properties.getEvolutionInstance())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)))
                    .doOnSuccess(r -> log.info("Mensagem enviada para {}", phone))
                    .doOnError(e -> log.error("Erro ao enviar mensagem para {}: {}", phone, e.getMessage()))
                    .block(Duration.ofSeconds(15));
        } catch (Exception e) {
            log.error("Falha no envio para {}: {}", phone, e.getMessage());
        }
    }

    /**
     * Configura o webhook da instância Evolution para apontar ao Spring Boot.
     */
    public void configureWebhook(String webhookUrl) {
        Map<String, Object> body = Map.of(
                "webhook", Map.of(
                        "enabled", true,
                        "url", webhookUrl,
                        "webhookByEvents", false,
                        "webhookBase64", false,
                        "events", new String[]{"MESSAGES_UPSERT"}
                )
        );

        evolutionWebClient.post()
                .uri("/webhook/set/{instance}", properties.getEvolutionInstance())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block(Duration.ofSeconds(10));

        log.info("Webhook configurado: {}", webhookUrl);
    }
}
