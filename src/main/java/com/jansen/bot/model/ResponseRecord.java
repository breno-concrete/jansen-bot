package com.jansen.bot.model;

/**
 * Registro de resposta de um membro (confirmação ou voto).
 * Aba Responses.
 */
public record ResponseRecord(
        String id,
        String rehearsalId,
        String memberPhone,
        String tipo,
        String valor,
        String timestamp
) {}
