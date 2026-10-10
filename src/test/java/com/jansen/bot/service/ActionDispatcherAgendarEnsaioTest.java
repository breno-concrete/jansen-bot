package com.jansen.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.exception.NaoAutorizadoException;
import com.jansen.bot.model.ClaudeAction;
import com.jansen.bot.rehearsal.application.RehearsalVotingService;
import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.repository.GoogleSheetsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** T023: AGENDAR_ENSAIO passa a criar o ensaio pelo RehearsalVotingService (FR-001, FR-002, FR-019). */
@ExtendWith(MockitoExtension.class)
class ActionDispatcherAgendarEnsaioTest {

    private static final String LIDER = "5511999990000";
    private static final String DATA_HORA = "25/10/2026 19:00";

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
    @DisplayName("FR-019/T023: AGENDAR_ENSAIO com tipo cria o ensaio daquele tipo pelo serviço novo")
    void agendarEnsaio_comTipo_criaPeloServicoNovo() {
        ClaudeAction acao = acao("vocal", DATA_HORA);

        String resposta = dispatcher.dispatch(LIDER, acao);

        verify(votacao).criarEnsaio(LIDER, TipoEnsaio.VOCAL, DATA_HORA, "Estúdio X");
        assertEquals("Ensaio vocal criado. Mandei o pedido de confirmação para o pessoal.", resposta);
        verifyNoInteractions(rehearsalService);
    }

    @Test
    @DisplayName("FR-019/P-034: AGENDAR_ENSAIO sem tipo assume geral")
    void agendarEnsaio_semTipo_assumeGeral() {
        ClaudeAction acao = acao("", DATA_HORA);

        String resposta = dispatcher.dispatch(LIDER, acao);

        verify(votacao).criarEnsaio(LIDER, TipoEnsaio.GERAL, DATA_HORA, "Estúdio X");
        assertEquals("Ensaio geral criado. Mandei o pedido de confirmação para o pessoal.", resposta);
    }

    @Test
    @DisplayName("FR-002/T023: AGENDAR_ENSAIO sem data e hora não cria o ensaio e pede esclarecimento")
    void agendarEnsaio_semDataHora_naoCriaEPedeEsclarecimento() {
        ClaudeAction acao = acao("vocal", "");

        String resposta = dispatcher.dispatch(LIDER, acao);

        verify(votacao, org.mockito.Mockito.never()).criarEnsaio(anyString(), any(), anyString(), anyString());
        assertEquals("Não consegui entender a data e o horário do ensaio. Pode mandar de novo? Exemplo: 25/10 às 19:00.",
                resposta);
    }

    @Test
    @DisplayName("FR-001/T023: quem não é líder é recusado pelo serviço e recebe a mensagem de só admin")
    void agendarEnsaio_naoLider_respondeSoAdmin() {
        when(votacao.criarEnsaio(anyString(), any(), anyString(), anyString()))
                .thenThrow(new NaoAutorizadoException("Só a líder pode criar um ensaio."));

        String resposta = dispatcher.dispatch("5511999991111", acao("vocal", DATA_HORA));

        assertEquals("Só admin pode agendar ensaio, beleza?", resposta);
    }

    private ClaudeAction acao(String tipoEnsaio, String opcoesDatas) {
        String json = "{\"acao\":\"AGENDAR_ENSAIO\",\"resposta\":\"ok\",\"dados\":{"
                + "\"opcoes_datas\":\"" + opcoesDatas + "\","
                + "\"local\":\"Estúdio X\","
                + "\"tipo_ensaio\":\"" + tipoEnsaio + "\"}}";
        try {
            return new ObjectMapper().readValue(json, ClaudeAction.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
