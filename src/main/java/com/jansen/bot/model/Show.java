package com.jansen.bot.model;

/**
 * Representa um show cadastrado na aba Shows.
 */
public record Show(
        String id,
        String nome,
        String data,
        String local,
        String horario,
        String criadoEm
) {}
