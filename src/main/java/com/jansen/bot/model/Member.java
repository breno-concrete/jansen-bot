package com.jansen.bot.model;

/**
 * Representa um membro da banda na aba Members.
 */
public record Member(
        String id,
        String nome,
        String telefone,
        String instrumento,
        boolean ativo,
        boolean admin
) {}
