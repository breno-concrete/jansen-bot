package com.jansen.bot.rehearsal.ports;

import java.util.List;

/**
 * Out-port de envio de mensagens (contracts/rehearsal-ports.md).
 * Implementado por um adapter em {@code adapters/out/messaging}.
 */
public interface NotificationPort {

    void notificarIntegrante(String telefone, String mensagem);

    /** Respeita o rate limit de 20s entre mensagens (AGENTS.md). */
    void notificarTodos(List<String> telefones, String mensagem);

    void notificarLider(String mensagem);
}
