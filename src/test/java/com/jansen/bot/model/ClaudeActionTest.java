package com.jansen.bot.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** T022F: o JSON devolvido pela IA ganha o campo {@code tipo_ensaio} (FR-019, FR-022). */
class ClaudeActionTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("FR-022/T022F: tipo_ensaio presente no JSON da IA preenche dados().tipoEnsaio()")
    void tipoEnsaio_presenteNoJson_ficaPreenchido() throws Exception {
        String json = "{\"acao\":\"CONFIRMAR_PRESENCA\",\"resposta\":\"ok\",\"dados\":{\"tipo_ensaio\":\"vocal\"}}";

        ClaudeAction acao = mapper.readValue(json, ClaudeAction.class);

        assertEquals("vocal", acao.dados().tipoEnsaio());
    }

    @Test
    @DisplayName("FR-022/T022F: sem tipo_ensaio no JSON da IA, dados().tipoEnsaio() fica null")
    void tipoEnsaio_ausenteNoJson_ficaNulo() throws Exception {
        String json = "{\"acao\":\"CONFIRMAR_PRESENCA\",\"resposta\":\"ok\",\"dados\":{}}";

        ClaudeAction acao = mapper.readValue(json, ClaudeAction.class);

        assertNull(acao.dados().tipoEnsaio());
    }
}
