package com.jansen.bot.rehearsal.adapters.in;

import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.rehearsal.domain.Voto;

/** Voto reconhecido numa mensagem; {@code tipo} é null quando o integrante não o disse (FR-022). */
public record VotoInterpretado(Voto.Escolha escolha, TipoEnsaio tipo) {}
