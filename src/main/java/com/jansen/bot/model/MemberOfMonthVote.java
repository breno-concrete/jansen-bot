package com.jansen.bot.model;

/**
 * Voto para membro do mês (aba MemberOfMonthVotes).
 */
public record MemberOfMonthVote(
        String id,
        String voterPhone,
        String votedPhone,
        int mes,
        int ano
) {}
