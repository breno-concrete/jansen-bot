package com.jansen.bot.rehearsal.adapters.out.messaging;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.rehearsal.ports.NotificationPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter de {@link NotificationPort} sobre o {@link EvolutionClient} existente.
 * Delay de 20s entre mensagens individuais em sequência (AGENTS.md, Integrações externas).
 */
@Component
public class EvolutionNotificationAdapter implements NotificationPort {

    private static final long DELAY_ENTRE_MENSAGENS_MS = 20_000;

    private final EvolutionClient evolutionClient;
    private final AppProperties properties;

    public EvolutionNotificationAdapter(EvolutionClient evolutionClient, AppProperties properties) {
        this.evolutionClient = evolutionClient;
        this.properties = properties;
    }

    @Override
    public void notificarIntegrante(String telefone, String mensagem) {
        evolutionClient.sendTextMessage(telefone, mensagem);
    }

    @Override
    public void notificarTodos(List<String> telefones, String mensagem) {
        for (int i = 0; i < telefones.size(); i++) {
            evolutionClient.sendTextMessage(telefones.get(i), mensagem);
            if (i < telefones.size() - 1) {
                evolutionClient.sleepDelay(DELAY_ENTRE_MENSAGENS_MS);
            }
        }
    }

    @Override
    public void notificarLider(String mensagem) {
        evolutionClient.sendTextMessage(properties.getPrimaryAdminPhone(), mensagem);
    }
}
