package com.jansen.bot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Configuração do cliente Google Sheets API.
 */
@Configuration
public class GoogleSheetsConfig {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetsConfig.class);

    @Value("${google.sheets.credentials-path}")
    private String credentialsPath;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public Sheets googleSheetsClient() throws GeneralSecurityException, IOException {
        InputStream credentialsStream = openCredentialsStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials)
        ).setApplicationName("jansen-bot").build();
    }

    private InputStream openCredentialsStream() throws IOException {
        // Tenta abrir como arquivo local (Docker/produção)
        try {
            return new FileInputStream(credentialsPath);
        } catch (IOException e) {
            log.warn("Credenciais não encontradas em {}, tentando classpath", credentialsPath);
            Resource resource = new org.springframework.core.io.ClassPathResource("google-credentials.json");
            if (resource.exists()) {
                return resource.getInputStream();
            }
            throw new IOException(
                    "Arquivo de credenciais Google não encontrado em: " + credentialsPath
                            + ". Veja docs/CONFIGURACAO.md para instruções."
            );
        }
    }
}
