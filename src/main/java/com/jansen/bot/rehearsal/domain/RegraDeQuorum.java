package com.jansen.bot.rehearsal.domain;

/**
 * Regra de quórum da votação de ensaio (FR-011/FR-012; research.md D6).
 * Aritmética inteira: exatamente 50% conta como quórum atingido.
 */
public class RegraDeQuorum {

    public boolean abaixoDoQuorum(int votosSim, int totalVotantes) {
        return votosSim * 2 < totalVotantes;
    }
}
