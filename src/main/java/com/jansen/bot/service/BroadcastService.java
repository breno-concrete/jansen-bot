package com.jansen.bot.service;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.model.Member;
import com.jansen.bot.model.SetlistSong;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.util.PhoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de broadcast e gestão de setlist.
 */
@Service
public class BroadcastService {

    private static final Logger log = LoggerFactory.getLogger(BroadcastService.class);

    private final GoogleSheetsRepository repository;
    private final EvolutionClient evolutionClient;

    public BroadcastService(GoogleSheetsRepository repository, EvolutionClient evolutionClient) {
        this.repository = repository;
        this.evolutionClient = evolutionClient;
    }

    /**
     * Envia aviso para todos os membros ativos.
     */
    public void broadcastToAll(String message) {
        List<Member> members = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());

        String formatted = "📢 *Aviso da banda:*\n\n" + message;

        evolutionClient.sendTextMessageSeries(members, formatted);
        log.info("Broadcast enviado para {} membros", members.size());
    }

    /**
     * Monta texto formatado da setlist do ensaio.
     */
    public String formatSetlist(String rehearsalId) {
        List<SetlistSong> songs = repository.findSetlistByRehearsal(rehearsalId);

        if (songs.isEmpty()) {
            return "A setlist ainda não foi definida 🎵";
        }

        StringBuilder sb = new StringBuilder("🎵 *Setlist do ensaio:*\n\n");
        for (SetlistSong song : songs) {
            sb.append(song.ordem()).append(". ")
                    .append(song.musica());
            if (song.artista() != null && !song.artista().isBlank()) {
                sb.append(" — ").append(song.artista());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Atualiza setlist a partir de string (uma música por linha).
     */
    public void updateSetlist(String rehearsalId, String musicasText) {
        repository.clearSetlistForRehearsal(rehearsalId);

        String[] lines = musicasText.split("\n");
        int ordem = 1;
        for (String line : lines) {
            line = line.trim();
            if (line.isBlank()) continue;

            String musica = line;
            String artista = "";
            if (line.contains(" - ")) {
                String[] parts = line.split(" - ", 2);
                musica = parts[0].trim();
                artista = parts[1].trim();
            } else if (line.contains(" — ")) {
                String[] parts = line.split(" — ", 2);
                musica = parts[0].trim();
                artista = parts[1].trim();
            }

            SetlistSong song = new SetlistSong(
                    PhoneUtils.generateId(), rehearsalId, musica, ordem++, artista,
                    SetlistSong.STATUS_SETLIST, "", "", ""
            );
            repository.saveSetlistSong(song);
        }
        log.info("Setlist atualizada para ensaio {}: {} músicas", rehearsalId, ordem - 1);
    }
}
