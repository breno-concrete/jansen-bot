package com.jansen.bot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resposta estruturada retornada pela Claude API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClaudeAction(
        @JsonProperty("acao") String acao,
        @JsonProperty("resposta") String resposta,
        @JsonProperty("dados") ActionData dados
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ActionData(
            @JsonProperty("rehearsal_id") String rehearsalId,
            @JsonProperty("data_opcao") String dataOpcao,
            @JsonProperty("confirmacao") Boolean confirmacao,
            @JsonProperty("mensagem_broadcast") String mensagemBroadcast,
            @JsonProperty("musicas") String musicas,
            @JsonProperty("opcoes_datas") String opcoesDatas,
            @JsonProperty("local") String local,
            // Shows
            @JsonProperty("show_id") String showId,
            @JsonProperty("show_nome") String showNome,
            @JsonProperty("show_data") String showData,
            @JsonProperty("show_horario") String showHorario,
            @JsonProperty("show_local") String showLocal,
            // Votação membro do mês
            @JsonProperty("vote_number") Integer voteNumber,
            @JsonProperty("voted_phone") String votedPhone,
            // Músicas
            @JsonProperty("musica_link") String musicaLink,
            @JsonProperty("musica_descricao") String musicaDescricao,
            @JsonProperty("suggestion_id") String suggestionId,
            @JsonProperty("musica_nome") String musicaNome
    ) {}
}
