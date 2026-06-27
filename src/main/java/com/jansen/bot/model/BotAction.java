package com.jansen.bot.model;

/**
 * Ações que o bot pode executar após interpretação da Claude API.
 */
public final class BotAction {

    private BotAction() {}

    // Ensaios e presença
    public static final String RESPONDER = "RESPONDER";
    public static final String AGENDAR_ENSAIO = "AGENDAR_ENSAIO";
    public static final String VOTAR_DATA = "VOTAR_DATA";
    public static final String CONFIRMAR_PRESENCA = "CONFIRMAR_PRESENCA";
    public static final String NEGAR_PRESENCA = "NEGAR_PRESENCA";
    public static final String STATUS_PRESENCA = "STATUS_PRESENCA";
    public static final String CONCLUIR_ENSAIO = "CONCLUIR_ENSAIO";
    public static final String CONTAR_ENSAIOS = "CONTAR_ENSAIOS";

    // Broadcast e setlist de ensaio
    public static final String BROADCAST = "BROADCAST";
    public static final String VER_SETLIST = "VER_SETLIST";
    public static final String ATUALIZAR_SETLIST = "ATUALIZAR_SETLIST";
    public static final String LEMBRETE = "LEMBRETE";

    // Shows
    public static final String CADASTRO_SHOW = "CADASTRO_SHOW";
    public static final String CONFIRMAR_SHOW = "CONFIRMAR_SHOW";
    public static final String CANCELAR_SHOW = "CANCELAR_SHOW";

    // Chegada e pontualidade
    public static final String REGISTRAR_CHEGADA = "REGISTRAR_CHEGADA";

    // Membro do mês
    public static final String ABERTURA_VOTACAO_MEMBRO = "ABERTURA_VOTACAO_MEMBRO";
    public static final String VOTAR_MEMBRO_MES = "VOTAR_MEMBRO_MES";
    public static final String ENCERRAR_VOTACAO_MEMBRO = "ENCERRAR_VOTACAO_MEMBRO";

    // Músicas e repertório
    public static final String SUGERIR_MUSICA = "SUGERIR_MUSICA";
    public static final String APROVAR_MUSICA = "APROVAR_MUSICA";
    public static final String REJEITAR_MUSICA = "REJEITAR_MUSICA";
    public static final String CONSULTAR_REPERTORIO = "CONSULTAR_REPERTORIO";
}
