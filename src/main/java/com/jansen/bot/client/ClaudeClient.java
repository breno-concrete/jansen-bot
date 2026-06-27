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
 * Cliente para a Claude API (Anthropic Messages API).
 * Envia mensagem do usuário + contexto e recebe JSON estruturado.
 */
@Component
public class ClaudeClient {

    private static final Logger log = LoggerFactory.getLogger(ClaudeClient.class);

    private final WebClient claudeWebClient;
    private final AppProperties properties;
    private final ObjectMapper objectMapper;
    private final String systemPromptTemplate;

    public ClaudeClient(WebClient claudeWebClient, AppProperties properties, ObjectMapper objectMapper)
            throws IOException {
        this.claudeWebClient = claudeWebClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.systemPromptTemplate = loadSystemPrompt();
    }

    /**
     * Processa mensagem do membro e retorna ação estruturada.
     */
    public ClaudeAction processMessage(String userMessage, String contextJson) {
        String systemPrompt = systemPromptTemplate.replace("{{CONTEXTO}}", contextJson);

        Map<String, Object> requestBody = Map.of(
                "model", properties.getClaudeModel(),
                "max_tokens", properties.getClaudeMaxTokens(),
                "system", systemPrompt,
                "messages", List.of(
                        Map.of("role", "user", "content", userMessage)
                )
        );

        try {
            String response = claudeWebClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", properties.getClaudeApiKey())
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(2)))
                    .block(Duration.ofSeconds(30));

            return parseClaudeResponse(response);
        } catch (Exception e) {
            log.error("Erro na Claude API: {}", e.getMessage());
            return new ClaudeAction(
                    "RESPONDER",
                    "Eita, deu um bug aqui 😅 Tenta de novo daqui a pouco!",
                    null
            );
        }
    }

    private ClaudeAction parseClaudeResponse(String response) throws IOException {
        JsonNode root = objectMapper.readTree(response);
        JsonNode content = root.path("content");

        String text = "";
        if (content.isArray() && !content.isEmpty()) {
            text = content.get(0).path("text").asText("");
        }

        // Claude deve retornar JSON puro; remove markdown se presente
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
