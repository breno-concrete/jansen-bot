package com.jansen.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Ponto de entrada da aplicação Jansen Bot.
 * Bot de WhatsApp para gerenciamento de banda musical.
 */
@SpringBootApplication
@EnableScheduling
public class JansenBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(JansenBotApplication.class, args);
    }
}
