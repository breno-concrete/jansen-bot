package com.jansen.bot.rehearsal.ports;

/**
 * Out-port de autorização (contracts/rehearsal-ports.md; research.md D3): define quem é líder
 * sem que o domínio dependa de {@code AppProperties}.
 */
public interface LeaderPolicyPort {

    boolean isLider(String telefone);
}
