package com.jansen.bot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Propriedades centralizadas da aplicação.
 */
@Configuration
public class AppProperties {

    @Value("${evolution.api.instance}")
    private String evolutionInstance;

    @Value("${google.sheets.spreadsheet-id}")
    private String spreadsheetId;

    @Value("${google.sheets.tab.members}")
    private String tabMembers;

    @Value("${google.sheets.tab.rehearsals}")
    private String tabRehearsals;

    @Value("${google.sheets.tab.setlist}")
    private String tabSetlist;

    @Value("${google.sheets.tab.responses}")
    private String tabResponses;

    @Value("${google.sheets.tab.conversation}")
    private String tabConversation;

    @Value("${google.sheets.tab.shows}")
    private String tabShows;

    @Value("${google.sheets.tab.show-reminders}")
    private String tabShowReminders;

    @Value("${google.sheets.tab.arrivals}")
    private String tabArrivals;

    @Value("${google.sheets.tab.member-of-month-votes}")
    private String tabMemberOfMonthVotes;

    @Value("${google.sheets.tab.monthly-report}")
    private String tabMonthlyReport;

    @Value("${banda.nome}")
    private String bandaNome;

    @Value("${banda.admin-phones}")
    private String adminPhones;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.model}")
    private String geminiModel;

    @Value("${gemini.api.max-tokens}")
    private int geminiMaxTokens;

    public String getEvolutionInstance() { return evolutionInstance; }
    public String getSpreadsheetId() { return spreadsheetId; }
    public String getTabMembers() { return tabMembers; }
    public String getTabRehearsals() { return tabRehearsals; }
    public String getTabSetlist() { return tabSetlist; }
    public String getTabResponses() { return tabResponses; }
    public String getTabConversation() { return tabConversation; }
    public String getTabShows() { return tabShows; }
    public String getTabShowReminders() { return tabShowReminders; }
    public String getTabArrivals() { return tabArrivals; }
    public String getTabMemberOfMonthVotes() { return tabMemberOfMonthVotes; }
    public String getTabMonthlyReport() { return tabMonthlyReport; }
    public String getBandaNome() { return bandaNome; }
    public String getAdminPhones() { return adminPhones; }
    public String getGeminiApiKey() { return geminiApiKey; }
    public String getGeminiModel() { return geminiModel; }
    public int getGeminiMaxTokens() { return geminiMaxTokens; }

    /** Retorna o primeiro telefone admin (para alertas privados). */
    public String getPrimaryAdminPhone() {
        return adminPhones.split(",")[0].trim();
    }
}
