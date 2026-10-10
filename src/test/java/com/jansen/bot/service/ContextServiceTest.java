package com.jansen.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.BandContext;
import com.jansen.bot.model.Rehearsal;
import com.jansen.bot.rehearsal.domain.Ensaio;
import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.rehearsal.ports.RehearsalRepositoryPort;
import com.jansen.bot.repository.GoogleSheetsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/** T023A / P-032: a IA enxerga no contexto os ensaios com votação aberta, que moram no Postgres. */
@ExtendWith(MockitoExtension.class)
class ContextServiceTest {

    private static final Instant AGORA = Instant.parse("2026-10-10T10:00:00Z");

    @Mock private GoogleSheetsRepository repository;
    @Mock private AppProperties properties;
    @Mock private RehearsalRepositoryPort ensaios;

    private BandContext contexto() {
        ContextService service = new ContextService(repository, properties, new ObjectMapper(), ensaios);
        return service.buildContext("5511999991111");
    }

    @Test
    @DisplayName("T023A/FR-022: ensaio com votação aberta aparece no resumo como AGENDADO, com tipo, data e local")
    void ensaioComVotacaoAberta_apareceNoResumo() {
        when(ensaios.buscarComVotacaoAberta())
                .thenReturn(List.of(Ensaio.criar(TipoEnsaio.VOCAL, "25/10/2026 19:00", "Estúdio X", AGORA)));

        String resumo = contexto().proximoEnsaioResumo();

        assertTrue(resumo.contains("vocal"), resumo);
        assertTrue(resumo.contains("25/10/2026 19:00"), resumo);
        assertTrue(resumo.contains("Estúdio X"), resumo);
        assertTrue(resumo.contains("AGENDADO"), "o prompt só aciona CONFIRMAR_PRESENCA com ensaio AGENDADO: " + resumo);
    }

    @Test
    @DisplayName("T023A/FR-021: com mais de um ensaio aberto, todos aparecem no resumo")
    void maisDeUmEnsaioAberto_todosApareceNoResumo() {
        when(ensaios.buscarComVotacaoAberta()).thenReturn(List.of(
                Ensaio.criar(TipoEnsaio.VOCAL, "25/10/2026 19:00", "Estúdio X", AGORA),
                Ensaio.criar(TipoEnsaio.GERAL, "26/10/2026 20:00", "Estúdio Y", AGORA)));

        String resumo = contexto().proximoEnsaioResumo();

        assertTrue(resumo.contains("25/10/2026 19:00"), resumo);
        assertTrue(resumo.contains("26/10/2026 20:00"), resumo);
    }

    @Test
    @DisplayName("T023A: sem nenhum ensaio, o resumo continua dizendo que não há ensaio agendado")
    void semEnsaio_resumoDizQueNaoHa() {
        assertEquals("Nenhum ensaio agendado", contexto().proximoEnsaioResumo());
    }

    @Test
    @DisplayName("T023A/P-032: ensaio novo e ensaio legado do Sheets convivem no mesmo resumo")
    void ensaioNovoELegado_convivemNoResumo() {
        when(ensaios.buscarComVotacaoAberta())
                .thenReturn(List.of(Ensaio.criar(TipoEnsaio.GERAL, "26/10/2026 20:00", "Estúdio Y", AGORA)));
        when(repository.findNextScheduledRehearsal())
                .thenReturn(Optional.of(new Rehearsal("r1", "2026-10-20 19:00", "Sala Legado", "AGENDADO", "", "", false)));

        String resumo = contexto().proximoEnsaioResumo();

        assertTrue(resumo.contains("26/10/2026 20:00"), resumo);
        assertTrue(resumo.contains("Sala Legado"), resumo);
    }
}
