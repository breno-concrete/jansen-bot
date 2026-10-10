package com.jansen.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.ClaudeAction;
import com.jansen.bot.rehearsal.application.RehearsalVotingService;
import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.rehearsal.domain.Voto;
import com.jansen.bot.repository.GoogleSheetsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/** T024: CONFIRMAR_PRESENCA / NEGAR_PRESENCA passam a registrar o voto pelo RehearsalVotingService (FR-022). */
@ExtendWith(MockitoExtension.class)
class ActionDispatcherPresencaTest {

    private static final String MEMBRO = "5511999991111";

    @Mock private RehearsalVotingService votacao;
    @Mock private RehearsalService rehearsalService;
    @Mock private BroadcastService broadcastService;
    @Mock private GoogleSheetsRepository repository;
    @Mock private EvolutionClient evolutionClient;
    @Mock private AppProperties properties;
    @Mock private ShowService showService;
    @Mock private ArrivalService arrivalService;
    @Mock private MemberOfMonthService memberOfMonthService;
    @Mock private MusicSuggestionService musicSuggestionService;
    @Mock private RehearsalCounterService rehearsalCounterService;

    @InjectMocks private ActionDispatcher dispatcher;

    @Test
    @DisplayName("FR-022/T024: CONFIRMAR_PRESENCA com tipo registra SIM naquele tipo e não responde (o serviço já responde)")
    void confirmarPresenca_comTipo_registraSimNoTipo() {
        String resposta = dispatcher.dispatch(MEMBRO, acao("CONFIRMAR_PRESENCA", "vocal"));

        verify(votacao).registrarVoto(MEMBRO, Voto.Escolha.SIM, TipoEnsaio.VOCAL);
        assertEquals("", resposta);
        verifyNoInteractions(rehearsalService);
    }

    @Test
    @DisplayName("FR-022/T024: NEGAR_PRESENCA sem tipo registra NAO sem tipo (o serviço decide)")
    void negarPresenca_semTipo_registraNaoSemTipo() {
        String resposta = dispatcher.dispatch(MEMBRO, acao("NEGAR_PRESENCA", ""));

        verify(votacao).registrarVoto(MEMBRO, Voto.Escolha.NAO, null);
        assertEquals("", resposta);
    }

    @Test
    @DisplayName("FR-022/T024: tipo desconhecido vindo da IA vira 'sem tipo', nunca 'geral'")
    void presenca_tipoDesconhecido_viraSemTipo() {
        dispatcher.dispatch(MEMBRO, acao("CONFIRMAR_PRESENCA", "banana"));

        verify(votacao).registrarVoto(MEMBRO, Voto.Escolha.SIM, null);
    }

    private ClaudeAction acao(String nome, String tipoEnsaio) {
        String json = "{\"acao\":\"" + nome + "\",\"resposta\":\"ok\",\"dados\":{\"tipo_ensaio\":\"" + tipoEnsaio + "\"}}";
        try {
            return new ObjectMapper().readValue(json, ClaudeAction.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
