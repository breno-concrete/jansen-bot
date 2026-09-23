package com.jansen.bot.rehearsal.domain;

import com.jansen.bot.util.PhoneUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private final TipoEnsaio tipo;
    private final String dataHora;
    private final String local;
    private final Instant criadoEm;
    private final Instant prazoVotacaoEm;
    private final Status status;
    private final DecisaoFinal decisaoFinal;
    private final List<Remarcacao> historicoRemarcacoes = new ArrayList<>();
    /** Um voto por integrante (chave: integranteId); um novo voto substitui o anterior. */
    private final Map<String, Voto> votos = new LinkedHashMap<>();

    private Ensaio(String id, TipoEnsaio tipo, String dataHora, String local, Instant criadoEm, Instant prazoVotacaoEm,
                   Status status, DecisaoFinal decisaoFinal, List<Remarcacao> historicoRemarcacoes,
                   List<Voto> votos) {
        this.id = id;
        this.tipo = Objects.requireNonNull(tipo, "tipo do ensaio é obrigatório (FR-019)");
        this.dataHora = dataHora;
        this.local = local;
        this.criadoEm = criadoEm;
        this.prazoVotacaoEm = prazoVotacaoEm;
        this.status = status;
        this.decisaoFinal = decisaoFinal;
        this.historicoRemarcacoes.addAll(historicoRemarcacoes);
        votos.forEach(voto -> this.votos.put(voto.integranteId(), voto));
    }

    public static Ensaio criar(TipoEnsaio tipo, String dataHora, String local, Instant criadoEm) {
        return new Ensaio(PhoneUtils.generateId(), tipo, dataHora, local != null ? local : "A definir", criadoEm,
                criadoEm.plus(PRAZO_VOTACAO), Status.VOTACAO_ABERTA, DecisaoFinal.PENDENTE, List.of(),
                List.of());
    }

    /** Reconstrói um Ensaio já existente (ex.: lido da persistência), sem aplicar regras de criação. */
    public static Ensaio reconstituir(String id, TipoEnsaio tipo, String dataHora, String local, Instant criadoEm,
                                      Instant prazoVotacaoEm, Status status, DecisaoFinal decisaoFinal,
                                      List<Remarcacao> historicoRemarcacoes, List<Voto> votos) {
        return new Ensaio(id, tipo, dataHora, local, criadoEm, prazoVotacaoEm, status, decisaoFinal,
                historicoRemarcacoes, votos);
    }

    /** Registra o voto do integrante; se ele já votou, a nova resposta substitui a anterior. */
    public void registrarVoto(String integranteId, Voto.Escolha escolha, Instant respondidoEm) {
        votos.put(integranteId, new Voto(integranteId, id, escolha, respondidoEm));
    }

    public String id() { return id; }

    public TipoEnsaio tipo() { return tipo; }

    public String dataHora() { return dataHora; }

    public String local() { return local; }

    public Instant criadoEm() { return criadoEm; }

    public Instant prazoVotacaoEm() { return prazoVotacaoEm; }

    public Status status() { return status; }

    public DecisaoFinal decisaoFinal() { return decisaoFinal; }

    public List<Remarcacao> historicoRemarcacoes() { return Collections.unmodifiableList(historicoRemarcacoes); }

    public List<Voto> votos() { return List.copyOf(votos.values()); }
}
