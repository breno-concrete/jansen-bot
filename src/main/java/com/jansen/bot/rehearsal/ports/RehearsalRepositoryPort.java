package com.jansen.bot.rehearsal.ports;

import com.jansen.bot.rehearsal.domain.Ensaio;

import java.util.List;
import java.util.Optional;

/**
 * Out-port de persistência de {@link Ensaio} (contracts/rehearsal-ports.md).
 * Implementado por um adapter em {@code adapters/out/persistence}.
 */
public interface RehearsalRepositoryPort {

    void salvar(Ensaio ensaio);

    Optional<Ensaio> buscarPorId(String ensaioId);

    List<Ensaio> buscarComVotacaoAberta();
}
