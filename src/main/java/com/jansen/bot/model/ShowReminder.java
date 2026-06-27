package com.jansen.bot.model;

/**
 * Registro de lembrete enviado para um show (aba ShowReminders).
 * tipo: 3dias | 1dia
 */
public record ShowReminder(
        String id,
        String showId,
        String tipo,
        boolean enviado,
        String enviadoEm
) {}
