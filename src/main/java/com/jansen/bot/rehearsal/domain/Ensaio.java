package com.jansen.bot.rehearsal.domain;

import com.jansen.bot.util.PhoneUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate root da votação de ensaio (data-model.md § Ensaio).
 * Por enquanto só o estado inicial (FR-007); as transições entram nas tasks seguintes.
 */
public class Ensaio {

    /** Duração da votação a partir da criação (FR-007). */
    private static final Duration PRAZO_VOTACAO = Duration.ofHours(12);

    public enum Status { VOTACAO_ABERTA, ENCERRADA }

    public enum DecisaoFinal { PENDENTE, CONFIRMADO, CANCELADO }

    /** Uma remarcação: novo dataHora + timestamp (data-model.md). */
    public record Remarcacao(String dataHora, Instant remarcadoEm) {}

    private final String id;
    private final String dataHora;
    private final String local;
    private final Instant criadoEm;
    private final Instant prazoVotacaoEm;
    private final Status status;
    private final DecisaoFinal decisaoFinal;
    private final List<Remarcacao> historicoRemarcacoes = new ArrayList<>();

    private Ensaio(String id, String dataHora, String local, Instant criadoEm) {
        this.id = id;
        this.dataHora = dataHora;
        this.local = local;
        this.criadoEm = criadoEm;
        this.prazoVotacaoEm = criadoEm.plus(PRAZO_VOTACAO);
        this.status = Status.VOTACAO_ABERTA;
        this.decisaoFinal = DecisaoFinal.PENDENTE;
    }

    public static Ensaio criar(String dataHora, String local, Instant criadoEm) {
        return new Ensaio(PhoneUtils.generateId(), dataHora, local != null ? local : "A definir", criadoEm);
    }

    public String id() { return id; }

    public String dataHora() { return dataHora; }

    public String local() { return local; }

    public Instant criadoEm() { return criadoEm; }

    public Instant prazoVotacaoEm() { return prazoVotacaoEm; }

    public Status status() { return status; }

    public DecisaoFinal decisaoFinal() { return decisaoFinal; }

    public List<Remarcacao> historicoRemarcacoes() { return Collections.unmodifiableList(historicoRemarcacoes); }
}
