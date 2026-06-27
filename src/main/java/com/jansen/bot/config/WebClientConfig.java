package com.jansen.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuração dos WebClients para Evolution API e Claude API.
 */
@Configuration
public class WebClientConfig {

    @Value("${evolution.api.base-url}")
    private String evolutionBaseUrl;

    @Value("${evolution.api.key}")
    private String evolutionApiKey;

    @Bean
    public WebClient evolutionWebClient() {
        return WebClient.builder()
                .baseUrl(evolutionBaseUrl)
                .defaultHeader("apikey", evolutionApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public WebClient claudeWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.anthropic.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("anthropic-version", "2023-06-01")
                .build();
    }
}
