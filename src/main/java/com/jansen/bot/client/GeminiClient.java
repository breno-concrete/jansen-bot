package com.jansen.bot.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.ClaudeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Cliente para a Groq API (OpenAI-compatible format).
 * Envia mensagem do usuário + contexto e recebe JSON estruturado.
 */
@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    private final WebClient geminiWebClient;
    private final AppProperties properties;
    private final ObjectMapper objectMapper;
    private final String systemPromptTemplate;

    public GeminiClient(@org.springframework.beans.factory.annotation.Qualifier("geminiWebClient") WebClient geminiWebClient,
                        AppProperties properties, ObjectMapper objectMapper) throws IOException {
        this.geminiWebClient = geminiWebClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.systemPromptTemplate = loadSystemPrompt();
    }

    /**
     * Processa mensagem do membro e retorna ação estruturada.
     */
    public ClaudeAction processMessage(String userMessage, String contextJson) {
        String systemPrompt = systemPromptTemplate.replace("{{CONTEXTO}}", contextJson);

        // Formato OpenAI (Groq-compatible)
        Map<String, Object> requestBody = Map.of(
                "model", properties.getGeminiModel(),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userMessage)
                ),
                "max_tokens", properties.getGeminiMaxTokens(),
                "temperature", 0.3
        );

        try {
            String response = geminiWebClient.post()
                    .uri("/v1/chat/completions")
                    .header("Authorization", "Bearer " + properties.getGeminiApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(status -> status.isError(), resp ->
                            resp.bodyToMono(String.class)
                                    .flatMap(body -> {
                                        log.error("Groq API HTTP {}: {}", resp.statusCode().value(), body);
                                        return reactor.core.publisher.Mono.error(
                                                new RuntimeException("Groq HTTP " + resp.statusCode().value() + ": " + body));
                                    })
                    )
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)))
                    .block(Duration.ofSeconds(30));

            return parseResponse(response);
        } catch (Exception e) {
            log.error("Erro na Groq API: {}", e.getMessage());
            return new ClaudeAction(
                    "RESPONDER",
                    "Eita, deu um bug aqui 😅 Tenta de novo daqui a pouco!",
                    null
            );
        }
    }

    private ClaudeAction parseResponse(String response) throws IOException {
        JsonNode root = objectMapper.readTree(response);

        // OpenAI format: choices[0].message.content
        String text = root.path("choices")
                .path(0)
                .path("message")
                .path("content")
                .asText("");

        // Remove markdown se presente
        text = text.trim();
        if (text.startsWith("```")) {
            text = text.replaceAll("^```(?:json)?\\s*", "").replaceAll("\\s*```$", "");
        }

        return objectMapper.readValue(text, ClaudeAction.class);
    }

    private String loadSystemPrompt() throws IOException {
        ClassPathResource resource = new ClassPathResource("system-prompt.txt");
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
