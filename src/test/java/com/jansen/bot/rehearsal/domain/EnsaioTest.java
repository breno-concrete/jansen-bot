package com.jansen.bot.rehearsal.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnsaioTest {

    private static final Instant CRIADO_EM = Instant.parse("2026-09-20T10:00:00Z");

    @Test
    @DisplayName("FR-007: ensaio criado nasce com votação aberta")
    void criar_nasceComVotacaoAberta() {
        Ensaio ensaio = Ensaio.criar("2026-09-25 19:00", "Estúdio X", CRIADO_EM);

        assertEquals(Ensaio.Status.VOTACAO_ABERTA, ensaio.status());
    }

    @Test
    @DisplayName("FR-007: prazo de votação é criadoEm + 12h")
    void criar_prazoDeVotacaoEhCriadoEmMais12Horas() {
        Ensaio ensaio = Ensaio.criar("2026-09-25 19:00", "Estúdio X", CRIADO_EM);

        assertEquals(CRIADO_EM, ensaio.criadoEm());
        assertEquals(CRIADO_EM.plus(Duration.ofHours(12)), ensaio.prazoVotacaoEm());
    }

    @Test
    @DisplayName("FR-007: ensaio criado não tem remarcações no histórico")
    void criar_historicoDeRemarcacoesVazio() {
        Ensaio ensaio = Ensaio.criar("2026-09-25 19:00", "Estúdio X", CRIADO_EM);

        assertTrue(ensaio.historicoRemarcacoes().isEmpty());
    }

    @Test
    @DisplayName("data-model.md: dataHora e local informados são preservados, id é gerado e decisão final é PENDENTE")
    void criar_preservaDadosInformadosEGeraId() {
        Ensaio ensaio = Ensaio.criar("2026-09-25 19:00", "Estúdio X", CRIADO_EM);

        assertEquals("2026-09-25 19:00", ensaio.dataHora());
        assertEquals("Estúdio X", ensaio.local());
        assertNotNull(ensaio.id());
        assertFalse(ensaio.id().isBlank());
        assertEquals(Ensaio.DecisaoFinal.PENDENTE, ensaio.decisaoFinal());
    }

    @Test
    @DisplayName("data-model.md: local não informado (null) vira 'A definir'")
    void criar_localNuloViraADefinir() {
        Ensaio ensaio = Ensaio.criar("2026-09-25 19:00", null, CRIADO_EM);

        assertEquals("A definir", ensaio.local());
    }

    @Test
    @DisplayName("T022: registrarVoto guarda o voto do integrante com o horário da resposta")
    void registrarVoto_guardaOVoto() {
        Ensaio ensaio = Ensaio.criar("2026-09-25 19:00", "Estúdio X", CRIADO_EM);
        Instant resposta = CRIADO_EM.plusSeconds(60);

        ensaio.registrarVoto("11999991111", Voto.Escolha.SIM, resposta);

        assertEquals(List.of(new Voto("11999991111", ensaio.id(), Voto.Escolha.SIM, resposta)), ensaio.votos());
    }

    @Test
    @DisplayName("T022: o segundo voto do mesmo integrante substitui o primeiro (resposta e horário)")
    void registrarVoto_repetido_substituiOAnterior() {
        Ensaio ensaio = Ensaio.criar("2026-09-25 19:00", "Estúdio X", CRIADO_EM);
        Instant depois = CRIADO_EM.plusSeconds(120);

        ensaio.registrarVoto("11999991111", Voto.Escolha.SIM, CRIADO_EM.plusSeconds(60));
        ensaio.registrarVoto("11999991111", Voto.Escolha.NAO, depois);

        assertEquals(List.of(new Voto("11999991111", ensaio.id(), Voto.Escolha.NAO, depois)), ensaio.votos());
    }

    @Test
    @DisplayName("T022: votos de integrantes diferentes coexistem")
    void registrarVoto_integrantesDiferentes_coexistem() {
        Ensaio ensaio = Ensaio.criar("2026-09-25 19:00", "Estúdio X", CRIADO_EM);

        ensaio.registrarVoto("11999991111", Voto.Escolha.SIM, CRIADO_EM);
        ensaio.registrarVoto("11999992222", Voto.Escolha.NAO, CRIADO_EM);

        assertEquals(2, ensaio.votos().size());
    }
}
