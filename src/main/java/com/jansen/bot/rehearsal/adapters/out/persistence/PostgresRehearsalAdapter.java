package com.jansen.bot.rehearsal.adapters.out.persistence;

import com.jansen.bot.rehearsal.domain.Ensaio;
import com.jansen.bot.rehearsal.ports.RehearsalRepositoryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** Adapter de {@link RehearsalRepositoryPort} sobre Postgres via JPA (research.md D4). */
@Component
public class PostgresRehearsalAdapter implements RehearsalRepositoryPort {

    private final EnsaioJpaRepository repository;

    public PostgresRehearsalAdapter(EnsaioJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void salvar(Ensaio ensaio) {
        repository.save(EnsaioJpaEntity.de(ensaio));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ensaio> buscarPorId(String ensaioId) {
        return repository.findById(ensaioId).map(EnsaioJpaEntity::paraDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ensaio> buscarComVotacaoAberta() {
        return repository.findByStatus(Ensaio.Status.VOTACAO_ABERTA).stream()
                .map(EnsaioJpaEntity::paraDominio)
                .toList();
    }
}
