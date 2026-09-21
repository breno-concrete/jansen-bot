package com.jansen.bot.rehearsal.ports;

import java.util.List;

/**
 * Out-port de consulta dos integrantes que podem votar (FR-003, FR-018): telefones do cadastro
 * vigente, já sem quem nunca recebe mensagem de ensaio (ex.: projeção). A exclusão de quem
 * solicitou o ensaio (a líder) é feita pelo {@code RehearsalVotingService}.
 */
public interface IntegranteRepositoryPort {

    List<String> buscarTelefonesElegiveis();
}
