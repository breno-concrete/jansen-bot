package com.jansen.bot.model;

/**
 * Representa uma música na aba Setlist.
 * status: setlist | sugerida | aprovada | rejeitada
 */
public record SetlistSong(
        String id,
        String rehearsalId,
        String musica,
        int ordem,
        String artista,
        String status,
        String link,
        String suggestedBy,
        String descricao
) {
    /** Status para músicas do repertório aprovado (sem ensaio específico). */
    public static final String STATUS_APROVADA = "aprovada";
    public static final String STATUS_SUGERIDA = "sugerida";
    public static final String STATUS_REJEITADA = "rejeitada";
    public static final String STATUS_SETLIST = "setlist";
}
