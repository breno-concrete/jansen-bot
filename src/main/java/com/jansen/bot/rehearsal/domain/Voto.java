package com.jansen.bot.rehearsal.domain;

import java.time.Instant;

/**
 * Resposta de um integrante a um ensaio (data-model.md § Voto).
 * {@code respondidoEm} é null enquanto o voto está pendente.
 */
public record Voto(String integranteId, String ensaioId, Escolha escolha, Instant respondidoEm) {

    /**
     * {@code NAO_RESPONDEU} só é atribuído pelo sistema ao encerrar por prazo (FR-009),
     * nunca escolhido pelo integrante.
     */
    public enum Escolha { SIM, NAO, NAO_RESPONDEU }
}
