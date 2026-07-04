package com.jansen.bot.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.jansen.bot.config.AppProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import com.jansen.bot.model.Member;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para envio de mensagens via Evolution API.
 */
@Component
public class EvolutionClient {

    private static final Logger log = LoggerFactory.getLogger(EvolutionClient.class);

    private final WebClient evolutionWebClient;
    private final AppProperties properties;

    public EvolutionClient(@org.springframework.beans.factory.annotation.Qualifier("evolutionWebClient") WebClient evolutionWebClient, AppProperties properties) {
        this.evolutionWebClient = evolutionWebClient;
        this.properties = properties;
    }

    /**
     * Envia mensagem de texto para um número de telefone.
     */
    public void sendTextMessage(String phone, String message) {
        String normalizedPhone = normalizeBrazilianPhone(phone);
        String jid = normalizedPhone.contains("@") ? normalizedPhone : normalizedPhone + "@s.whatsapp.net";

        // Enviamos tanto "text" quanto "textMessage" para garantir compatibilidade com Evolution API v1 e v2
        Map<String, Object> body = Map.of(
                "number", jid.replace("@s.whatsapp.net", ""),
                "textMessage", Map.of("text", message),
                "text", message
        );

        try {
            evolutionWebClient.post()
                    .uri("/message/sendText/{instance}", properties.getEvolutionInstance())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)))
                    .doOnSuccess(r -> log.info("Mensagem enviada para {} (normalizado: {}). Resposta da Evolution API: {}", phone, normalizedPhone, r))
                    .doOnError(e -> log.error("Erro ao enviar mensagem para {} (normalizado: {}): {}", phone, normalizedPhone, e.getMessage()))
                    .block(Duration.ofSeconds(15));
        } catch (Exception e) {
            log.error("Falha no envio para {}: {}", phone, e.getMessage());
        }
    }

    /**
     * Normaliza números de telefone do Brasil para garantir o 9º dígito.
     * Resolve o problema clássico do Baileys/WhatsApp onde webhooks chegam com 8 dígitos
     * (ex: 556182744166), mas o envio exige 9 dígitos (ex: 5561982744166).
     */
    private String normalizeBrazilianPhone(String phone) {
        if (phone == null) return null;
        String clean = phone.replace("@s.whatsapp.net", "").replace("@g.us", "").replaceAll("\\D", "");
        
        // Se for um número brasileiro (começa com 55) de celular com 8 dígitos (total 12 dígitos: 55 + DDD + 8)
        if (clean.startsWith("55") && clean.length() == 12) {
            String ddd = clean.substring(2, 4);
            String number = clean.substring(4);
            // Celulares no Brasil começam com 6, 7, 8 ou 9
            if (number.charAt(0) >= '6' && number.charAt(0) <= '9') {
                String normalized = "55" + ddd + "9" + number;
                log.info("Normalizando telefone BR de 8 dígitos ({}) para 9 dígitos ({})", clean, normalized);
                return normalized;
            }
        }
        return clean;
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

    /**
     * Envia mensagem para uma lista de membros com delay de 15 segundos entre cada envio (anti-spam / anti-ban).
     */
    public void sendTextMessageSeries(List<Member> members, String message) {
        for (int i = 0; i < members.size(); i++) {
            sendTextMessage(members.get(i).telefone(), message);
            if (i < members.size() - 1) {
                sleepDelay(15000);
            }
        }
    }

    /**
     * Aguarda delay especificado (em milissegundos) para camuflar comportamento de robô.
     */
    public void sleepDelay(long millis) {
        try {
            log.info("Aguardando {}ms (delay anti-spam) antes da próxima mensagem...", millis);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
