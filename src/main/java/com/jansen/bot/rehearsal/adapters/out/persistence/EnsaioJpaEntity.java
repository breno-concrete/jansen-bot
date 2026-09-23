package com.jansen.bot.rehearsal.adapters.out.persistence;

import com.jansen.bot.rehearsal.domain.Ensaio;
import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.rehearsal.domain.Voto;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Entidade JPA de {@link Ensaio} (só do adapter; o domínio não tem anotações JPA). */
@Entity
@Table(name = "ensaio")
class EnsaioJpaEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEnsaio tipo;

    @Column(name = "data_hora", nullable = false)
    private String dataHora;

    @Column(nullable = false)
    private String local;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "prazo_votacao_em", nullable = false)
    private Instant prazoVotacaoEm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ensaio.Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "decisao_final", nullable = false)
    private Ensaio.DecisaoFinal decisaoFinal;

    @ElementCollection
    @CollectionTable(name = "ensaio_remarcacao", joinColumns = @JoinColumn(name = "ensaio_id"))
    @OrderBy("remarcadoEm ASC")
    private List<RemarcacaoEmbeddable> historicoRemarcacoes = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "voto", joinColumns = @JoinColumn(name = "ensaio_id"))
    @OrderBy("respondidoEm ASC")
    private List<VotoEmbeddable> votos = new ArrayList<>();

    protected EnsaioJpaEntity() {
    }

    static EnsaioJpaEntity de(Ensaio ensaio) {
        EnsaioJpaEntity e = new EnsaioJpaEntity();
        e.id = ensaio.id();
        e.tipo = ensaio.tipo();
        e.dataHora = ensaio.dataHora();
        e.local = ensaio.local();
        e.criadoEm = ensaio.criadoEm();
        e.prazoVotacaoEm = ensaio.prazoVotacaoEm();
        e.status = ensaio.status();
        e.decisaoFinal = ensaio.decisaoFinal();
        ensaio.historicoRemarcacoes().forEach(r ->
                e.historicoRemarcacoes.add(new RemarcacaoEmbeddable(r.dataHora(), r.remarcadoEm())));
        ensaio.votos().forEach(v ->
                e.votos.add(new VotoEmbeddable(v.integranteId(), v.escolha(), v.respondidoEm())));
        return e;
    }

    Ensaio paraDominio() {
        List<Ensaio.Remarcacao> historico = historicoRemarcacoes.stream()
                .map(r -> new Ensaio.Remarcacao(r.dataHora, r.remarcadoEm))
                .toList();
        List<Voto> votosDominio = votos.stream()
                .map(v -> new Voto(v.integranteId, id, v.escolha, v.respondidoEm))
                .toList();
        return Ensaio.reconstituir(id, tipo, dataHora, local, criadoEm, prazoVotacaoEm, status, decisaoFinal,
                historico, votosDominio);
    }

    @Embeddable
    static class VotoEmbeddable {

        @Column(name = "integrante_id", nullable = false)
        private String integranteId;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Voto.Escolha escolha;

        @Column(name = "respondido_em")
        private Instant respondidoEm;

        protected VotoEmbeddable() {
        }

        VotoEmbeddable(String integranteId, Voto.Escolha escolha, Instant respondidoEm) {
            this.integranteId = integranteId;
            this.escolha = escolha;
            this.respondidoEm = respondidoEm;
        }
    }

    @Embeddable
    static class RemarcacaoEmbeddable {

        @Column(name = "data_hora", nullable = false)
        private String dataHora;

        @Column(name = "remarcado_em", nullable = false)
        private Instant remarcadoEm;

        protected RemarcacaoEmbeddable() {
        }

        RemarcacaoEmbeddable(String dataHora, Instant remarcadoEm) {
            this.dataHora = dataHora;
            this.remarcadoEm = remarcadoEm;
        }
    }
}
