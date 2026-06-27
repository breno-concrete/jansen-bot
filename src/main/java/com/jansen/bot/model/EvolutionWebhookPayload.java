package com.jansen.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Payload recebido do webhook da Evolution API (evento messages.upsert).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EvolutionWebhookPayload(
        String event,
        String instance,
        EvolutionData data
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EvolutionData(
            EvolutionKey key,
            EvolutionMessage message,
            String pushName
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EvolutionKey(
            String remoteJid,
            boolean fromMe
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EvolutionMessage(
            String conversation,
            @JsonProperty("extendedTextMessage") ExtendedTextMessage extendedTextMessage
    ) {
        /**
         * Extrai o texto da mensagem, seja texto simples ou resposta estendida.
         */
        public String extractText() {
            if (conversation != null && !conversation.isBlank()) {
                return conversation.trim();
            }
            if (extendedTextMessage != null && extendedTextMessage.text() != null) {
                return extendedTextMessage.text().trim();
            }
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExtendedTextMessage(String text) {}

    /**
     * Verifica se é uma mensagem recebida (não enviada pelo bot).
     */
    public boolean isIncomingMessage() {
        return "messages.upsert".equals(event)
                && data != null
                && data.key() != null
                && !data.key().fromMe()
                && extractMessageText() != null;
    }

    public String extractMessageText() {
        if (data == null || data.message() == null) {
            return null;
        }
        return data.message().extractText();
    }

    /**
     * Extrai o número de telefone do remetente (sem @s.whatsapp.net).
     */
    public String extractSenderPhone() {
        if (data == null || data.key() == null || data.key().remoteJid() == null) {
            return null;
        }
        return data.key().remoteJid().replace("@s.whatsapp.net", "").replace("@g.us", "");
    }

    public String extractSenderName() {
        return data != null ? data.pushName() : null;
    }
}
