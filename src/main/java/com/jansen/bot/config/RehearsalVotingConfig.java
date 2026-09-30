package com.jansen.bot.config;

import com.jansen.bot.rehearsal.application.RehearsalVotingService;
import com.jansen.bot.rehearsal.ports.ClockPort;
import com.jansen.bot.rehearsal.ports.IntegranteRepositoryPort;
import com.jansen.bot.rehearsal.ports.LeaderPolicyPort;
import com.jansen.bot.rehearsal.ports.NotificationPort;
import com.jansen.bot.rehearsal.ports.RehearsalRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Registra o {@link RehearsalVotingService} (sem anotações Spring, ver D1) como bean. */
@Configuration
public class RehearsalVotingConfig {

    @Bean
    public RehearsalVotingService rehearsalVotingService(RehearsalRepositoryPort repositorio,
                                                         NotificationPort notificacao,
                                                         ClockPort relogio,
                                                         LeaderPolicyPort politicaDeLider,
                                                         IntegranteRepositoryPort integrantes) {
        return new RehearsalVotingService(repositorio, notificacao, relogio, politicaDeLider, integrantes);
    }
}
