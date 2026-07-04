package com.jansen.bot.service;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.model.Rehearsal;
import com.jansen.bot.model.Member;
import com.jansen.bot.repository.GoogleSheetsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Contador de ensaios realizados pela banda.
 */
@Service
public class RehearsalCounterService {

    private static final Logger log = LoggerFactory.getLogger(RehearsalCounterService.class);

    private final GoogleSheetsRepository repository;
    private final EvolutionClient evolutionClient;
    

    public RehearsalCounterService(GoogleSheetsRepository repository,
                                   EvolutionClient evolutionClient) {
        this.repository = repository;
        this.evolutionClient = evolutionClient;
    }

    public long countCompletedRehearsals() {
        return repository.countRehearsalsByStatus("REALIZADO");
    }

    public String formatCountMessage() {
        long count = countCompletedRehearsals();
        return String.format("A banda já fez %d ensaios juntos 🎸", count);
    }

    /**
     * Marca ensaio atual como realizado e anuncia contador no grupo.
     */
    public String completeRehearsal(String rehearsalId) {
        Optional<Rehearsal> rehearsal = repository.findRehearsalById(rehearsalId);

        if (rehearsal.isEmpty()) {
            rehearsal = repository.findNextScheduledRehearsal()
                    .filter(r -> "AGENDADO".equalsIgnoreCase(r.status()));
        }

        if (rehearsal.isEmpty()) {
            return "Não achei ensaio agendado pra concluir 🤷";
        }

        Rehearsal r = rehearsal.get();
        Rehearsal updated = new Rehearsal(
                r.id(), r.dataHora(), r.local(), "REALIZADO",
                r.opcoesVoto(), r.vencedor(), r.lembreteEnviado()
        );
        repository.updateRehearsal(updated);

        long count = countCompletedRehearsals();
        String message = String.format(
                "🎸 Mais um ensaio no bolso! Esse foi o de número *%d* da banda. Vamos pro próximo! 💪",
                count
        );

        List<Member> activeMembers = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());
        evolutionClient.sendTextMessageSeries(activeMembers, message);

        log.info("Ensaio {} marcado como REALIZADO. Total: {}", r.id(), count);
        return "Ensaio concluído! Anunciei pro grupo — total: " + count + " ensaios 🎸";
    }
}
