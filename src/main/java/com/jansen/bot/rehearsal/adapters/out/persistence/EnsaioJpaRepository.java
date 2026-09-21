package com.jansen.bot.rehearsal.adapters.out.persistence;

import com.jansen.bot.rehearsal.domain.Ensaio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface EnsaioJpaRepository extends JpaRepository<EnsaioJpaEntity, String> {

    List<EnsaioJpaEntity> findByStatus(Ensaio.Status status);
}
