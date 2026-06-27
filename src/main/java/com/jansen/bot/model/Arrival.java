package com.jansen.bot.model;

/**
 * Registro de chegada de membro em ensaio (aba Arrivals).
 */
public record Arrival(
        String id,
        String memberPhone,
        String rehearsalId,
        String horarioChegada,
        String data
) {}
