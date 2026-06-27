package com.jansen.bot.model;

/**
 * Relatório mensal gerado automaticamente (aba MonthlyReport).
 */
public record MonthlyReport(
        String id,
        int mes,
        int ano,
        String geradoEm,
        String dadosJson
) {}
