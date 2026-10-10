package com.jansen.bot.rehearsal.adapters.in;

import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.rehearsal.domain.Voto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** T022G / P-031: parser de voto sem IA. Estrito: na dúvida não reconhece e a mensagem segue para a IA. */
class InterpretadorDeVotoTest {

    private final InterpretadorDeVoto interpretador = new InterpretadorDeVoto();

    @ParameterizedTest
    @ValueSource(strings = {"sim", "Sim!", "SIM.", "vou", "confirmo", "to dentro", "Tô dentro!"})
    @DisplayName("FR-004/P-031: palavras de 'sim' viram voto SIM, sem tipo, ignorando acento, maiúscula e pontuação")
    void reconheceSim(String texto) {
        assertEquals(Optional.of(new VotoInterpretado(Voto.Escolha.SIM, null)), interpretador.interpretar(texto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"não", "Nao!", "NÃO.", "não vou", "não posso", "to fora", "Tô fora!"})
    @DisplayName("FR-004/P-031: palavras de 'não' viram voto NAO, sem tipo, ignorando acento, maiúscula e pontuação")
    void reconheceNao(String texto) {
        assertEquals(Optional.of(new VotoInterpretado(Voto.Escolha.NAO, null)), interpretador.interpretar(texto));
    }

    @Test
    @DisplayName("FR-022: 'sim, vocal' traz o tipo junto com a escolha")
    void reconheceSimComTipo() {
        assertEquals(Optional.of(new VotoInterpretado(Voto.Escolha.SIM, TipoEnsaio.VOCAL)),
                interpretador.interpretar("sim, vocal"));
        assertEquals(Optional.of(new VotoInterpretado(Voto.Escolha.SIM, TipoEnsaio.INSTRUMENTAL)),
                interpretador.interpretar("SIM INSTRUMENTAL"));
    }

    @Test
    @DisplayName("FR-022: 'não, geral' traz o tipo junto com a escolha")
    void reconheceNaoComTipo() {
        assertEquals(Optional.of(new VotoInterpretado(Voto.Escolha.NAO, TipoEnsaio.GERAL)),
                interpretador.interpretar("não, geral"));
        assertEquals(Optional.of(new VotoInterpretado(Voto.Escolha.NAO, TipoEnsaio.VOCAL)),
                interpretador.interpretar("não vou, vocal"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "sim, mas chego atrasado", "não sei", "talvez", "sim?", "quando é o ensaio?",
            "sim, vocal e instrumental", "sim, banana", "vocal", "oi, tudo bem?"})
    @DisplayName("FR-004/P-031: o que não é inteiramente um voto não é reconhecido e segue para a IA")
    void naoReconheceOQueNaoEUmVoto(String texto) {
        assertEquals(Optional.empty(), interpretador.interpretar(texto));
    }
}
