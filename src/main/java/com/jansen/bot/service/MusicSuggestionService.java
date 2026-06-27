package com.jansen.bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.*;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.util.PhoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Sugestão, aprovação e consulta de repertório musical.
 */
@Service
public class MusicSuggestionService {

    private static final Logger log = LoggerFactory.getLogger(MusicSuggestionService.class);

    private final GoogleSheetsRepository repository;
    private final EvolutionClient evolutionClient;
    private final AppProperties properties;

    public MusicSuggestionService(GoogleSheetsRepository repository,
                                  EvolutionClient evolutionClient,
                                  AppProperties properties) {
        this.repository = repository;
        this.evolutionClient = evolutionClient;
        this.properties = properties;
    }

    /**
     * Membro sugere música com link e descrição opcional.
     */
    public String suggestMusic(String memberPhone, ClaudeAction.ActionData dados) {
        String link = dados.musicaLink() != null ? dados.musicaLink() : "";
        String descricao = dados.musicaDescricao() != null ? dados.musicaDescricao() : "";
        String nomeMusica = dados.musicaNome() != null && !dados.musicaNome().isBlank()
                ? dados.musicaNome() : "Sugestão musical";

        String memberName = repository.findMemberByPhone(memberPhone)
                .map(Member::nome).orElse("Membro");
        String adminName = repository.findMemberByPhone(properties.getPrimaryAdminPhone())
                .map(Member::nome).orElse("admin");

        String suggestionId = PhoneUtils.generateId();
        SetlistSong song = new SetlistSong(
                suggestionId, "", nomeMusica, 0, "",
                SetlistSong.STATUS_SUGERIDA, link, memberPhone, descricao
        );
        repository.saveSetlistSong(song);

        // Notifica admin em privado
        String adminMessage = String.format(
                "*%s* sugeriu uma música:\n\nDescrição: '%s'\nLink: %s\n\n" +
                "Responda *APROVAR* ou *REJEITAR* para %s",
                memberName, descricao.isBlank() ? "(sem descrição)" : descricao, link, memberName
        );
        evolutionClient.sendTextMessage(properties.getPrimaryAdminPhone(), adminMessage);

        // Guarda contexto para o admin aprovar/rejeitar
        Map<String, String> ctx = new HashMap<>();
        ctx.put("suggestionId", suggestionId);
        ctx.put("suggesterPhone", memberPhone);
        try {
            String ctxJson = new ObjectMapper().writeValueAsString(ctx);
            repository.saveConversationState(new ConversationState(
                    properties.getPrimaryAdminPhone(),
                    ConversationStates.AGUARDANDO_APROVACAO_MUSICA,
                    ctxJson, PhoneUtils.nowFormatted()
            ));
        } catch (JsonProcessingException e) {
            log.error("Erro ao salvar contexto de sugestão: {}", e.getMessage());
        }

        log.info("Sugestão musical {} registrada por {}", suggestionId, memberPhone);
        return String.format("Sugestão recebida, %s! Vou encaminhar pro %s avaliar 🎵",
                memberName, adminName);
    }

    /**
     * Admin aprova sugestão pendente.
     */
    public String approveMusic(String adminPhone) {
        return processAdminDecision(adminPhone, SetlistSong.STATUS_APROVADA,
                "Boa notícia! Sua sugestão foi aprovada 🎉");
    }

    /**
     * Admin rejeita sugestão pendente.
     */
    public String rejectMusic(String adminPhone) {
        return processAdminDecision(adminPhone, SetlistSong.STATUS_REJEITADA,
                "Obrigado pela sugestão! Dessa vez não vai rolar, mas pode sugerir mais 😊");
    }

    private String processAdminDecision(String adminPhone, String newStatus, String memberMessage) {
        ConversationState state = repository.findConversationState(adminPhone).orElse(null);
        if (state == null || !ConversationStates.AGUARDANDO_APROVACAO_MUSICA.equals(state.estado())) {
            return "Não tem sugestão pendente pra avaliar 🤷";
        }

        Map<String, String> ctx = parseContext(state.contextoJson());
        String suggestionId = ctx.get("suggestionId");
        String suggesterPhone = ctx.get("suggesterPhone");

        repository.findSetlistSongById(suggestionId).ifPresent(song -> {
            SetlistSong updated = new SetlistSong(
                    song.id(), song.rehearsalId(), song.musica(), song.ordem(), song.artista(),
                    newStatus, song.link(), song.suggestedBy(), song.descricao()
            );
            repository.updateSetlistSong(updated);
        });

        evolutionClient.sendTextMessage(suggesterPhone, memberMessage);
        repository.clearConversationState(adminPhone);

        return SetlistSong.STATUS_APROVADA.equals(newStatus)
                ? "Sugestão aprovada! Membro avisado ✅"
                : "Sugestão rejeitada. Membro avisado 👍";
    }

    /**
     * Lista repertório aprovado com links.
     */
    public String formatApprovedRepertoire() {
        List<SetlistSong> songs = repository.findApprovedRepertoire();

        if (songs.isEmpty()) {
            return "O repertório ainda tá vazio 🎵 Manda sugestões!";
        }

        StringBuilder sb = new StringBuilder("🎵 *Repertório da banda:*\n\n");
        int i = 1;
        for (SetlistSong song : songs) {
            sb.append(i++).append(". ").append(song.musica());
            if (song.link() != null && !song.link().isBlank()) {
                sb.append("\n   🔗 ").append(song.link());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> parseContext(String json) {
        try {
            return new ObjectMapper().readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }
}
