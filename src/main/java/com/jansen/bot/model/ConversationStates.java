package com.jansen.bot.model;

/**
 * Estados possíveis na aba ConversationState.
 */
public final class ConversationStates {

    private ConversationStates() {}

    public static final String LIVRE = "LIVRE";
    public static final String VOTACAO = "VOTACAO";
    public static final String AGUARDANDO_CONFIRMACAO_SHOW = "AGUARDANDO_CONFIRMACAO_SHOW";
    public static final String VOTACAO_MEMBRO_ABERTA = "VOTACAO_MEMBRO_ABERTA";
    public static final String VOTACAO_MEMBRO_MES = "VOTACAO_MEMBRO_MES";
    public static final String AGUARDANDO_APROVACAO_MUSICA = "AGUARDANDO_APROVACAO_MUSICA";
}
