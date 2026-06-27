package com.jansen.bot.model;

/**
 * Estado de conversa persistido no Google Sheets.
 * Permite continuidade sem Redis.
 */
public record ConversationState(
        String memberPhone,
        String estado,
        String contextoJson,
        String updatedAt
) {}
