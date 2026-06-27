package com.jansen.bot.model;

import java.util.List;

/**
 * Contexto completo da banda enviado à Claude API no system prompt.
 */
public record BandContext(
        String bandaNome,
        List<Member> membros,
        List<Rehearsal> ensaios,
        List<SetlistSong> setlist,
        List<ResponseRecord> respostasRecentes,
        ConversationState estadoConversa,
        String proximoEnsaioResumo,
        List<Show> shows,
        List<Arrival> chegadasRecentes,
        int totalEnsaiosRealizados,
        boolean votacaoMembroAberta,
        List<SetlistSong> repertorioAprovado,
        List<SetlistSong> sugestoesPendentes
) {}
