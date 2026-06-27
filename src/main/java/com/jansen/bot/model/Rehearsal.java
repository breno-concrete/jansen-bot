package com.jansen.bot.model;

/**
 * Representa um ensaio na aba Rehearsals.
 * Status possíveis: PROPOSTO, VOTACAO, AGENDADO, CANCELADO, REALIZADO
 */
public record Rehearsal(
        String id,
        String dataHora,
        String local,
        String status,
        String opcoesVoto,
        String vencedor,
        boolean lembreteEnviado
) {}
