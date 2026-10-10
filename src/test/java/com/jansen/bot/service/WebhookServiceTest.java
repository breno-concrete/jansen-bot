package com.jansen.bot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.model.ClaudeAction;
import com.jansen.bot.model.EvolutionWebhookPayload;
import com.jansen.bot.rehearsal.adapters.in.InterpretadorDeVoto;
import com.jansen.bot.rehearsal.application.RehearsalVotingService;
import com.jansen.bot.rehearsal.domain.Ensaio;
import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.rehearsal.domain.Voto;
import com.jansen.bot.rehearsal.ports.RehearsalRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** T024: o atalho "sim/não" sem IA passa a usar o InterpretadorDeVoto e os ensaios do Postgres (FR-004, FR-022). */
@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    private static final String MEMBRO = "5511999991111";

    @Mock private GeminiService geminiService;
    @Mock private ActionDispatcher actionDispatcher;
    @Mock private EvolutionClient evolutionClient;
    @Mock private RehearsalVotingService votacao;
    @Mock private RehearsalRepositoryPort ensaios;

    private WebhookService webhook;

    @BeforeEach
    void setUp() {
        webhook = new WebhookService(geminiService, actionDispatcher, evolutionClient,
                new InterpretadorDeVoto(), votacao, ensaios);
    }

    @Test
    @DisplayName("FR-022/T024: com ensaio aberto, 'sim, vocal' é voto: vai direto ao serviço, sem IA e sem resposta duplicada")
    void votoComEnsaioAberto_vaiDiretoAoServico() throws Exception {
        ensaioAberto();

        webhook.processWebhook(mensagem("sim, vocal"));

        verify(votacao).registrarVoto(MEMBRO, Voto.Escolha.SIM, TipoEnsaio.VOCAL);
        verifyNoInteractions(geminiService, actionDispatcher);
        verify(evolutionClient, never()).sendTextMessage(anyString(), anyString());
    }

    @Test
    @DisplayName("P-031/T024: sem ensaio aberto, um 'sim' solto vai para a IA")
    void semEnsaioAberto_simVaiParaAIA() throws Exception {
        ClaudeAction acao = new ClaudeAction("RESPONDER", "oi", null);
        when(geminiService.interpret(MEMBRO, "sim")).thenReturn(acao);
        when(actionDispatcher.dispatch(MEMBRO, acao)).thenReturn("oi");

        webhook.processWebhook(mensagem("sim"));

        verifyNoInteractions(votacao);
        verify(evolutionClient).sendTextMessage(MEMBRO, "oi");
    }

    @Test
    @DisplayName("P-031/T024: com ensaio aberto, texto que não é inteiramente um voto vai para a IA")
    void ensaioAberto_textoLivreVaiParaAIA() throws Exception {
        ensaioAberto();
        ClaudeAction acao = new ClaudeAction("RESPONDER", "dia 25", null);
        when(geminiService.interpret(MEMBRO, "quando é o ensaio?")).thenReturn(acao);
        when(actionDispatcher.dispatch(MEMBRO, acao)).thenReturn("dia 25");

        webhook.processWebhook(mensagem("quando é o ensaio?"));

        verifyNoInteractions(votacao);
        verify(evolutionClient).sendTextMessage(MEMBRO, "dia 25");
    }

    @Test
    @DisplayName("T024: dispatcher devolvendo texto vazio significa 'não enviar nada' (o serviço já respondeu)")
    void respostaVazia_naoEnviaNada() throws Exception {
        ClaudeAction acao = new ClaudeAction("CONFIRMAR_PRESENCA", "ok da IA", null);
        when(geminiService.interpret(MEMBRO, "vou nesse")).thenReturn(acao);
        when(actionDispatcher.dispatch(MEMBRO, acao)).thenReturn("");

        webhook.processWebhook(mensagem("vou nesse"));

        verify(evolutionClient, never()).sendTextMessage(anyString(), anyString());
    }

    private void ensaioAberto() {
        when(ensaios.buscarComVotacaoAberta()).thenReturn(List.of(
                Ensaio.criar(TipoEnsaio.VOCAL, "25/10/2026 19:00", "Estúdio X", Instant.parse("2026-10-10T10:00:00Z"))));
    }

    private EvolutionWebhookPayload mensagem(String texto) throws Exception {
        String json = "{\"event\":\"messages.upsert\",\"data\":{\"key\":{\"remoteJid\":\"" + MEMBRO
                + "@s.whatsapp.net\",\"fromMe\":false},\"message\":{\"conversation\":\"" + texto + "\"}}}";
        return new ObjectMapper().readValue(json, EvolutionWebhookPayload.class);
    }
}
