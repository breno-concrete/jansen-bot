package com.jansen.bot.service;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.Member;
import com.jansen.bot.model.Rehearsal;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.util.DateUtils;
import com.jansen.bot.util.PhoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Detecta faltas consecutivas e alerta o admin em privado.
 */
@Service
public class AbsenceAlertService {

    private static final Logger log = LoggerFactory.getLogger(AbsenceAlertService.class);

    private final GoogleSheetsRepository repository;
    private final EvolutionClient evolutionClient;
    private final AppProperties properties;

    public AbsenceAlertService(GoogleSheetsRepository repository,
                               EvolutionClient evolutionClient,
                               AppProperties properties) {
        this.repository = repository;
        this.evolutionClient = evolutionClient;
        this.properties = properties;
    }

    /**
     * Verifica os últimos 2 ensaios realizados e alerta admin sobre faltas consecutivas.
     */
    public void checkConsecutiveAbsences() {
        List<Rehearsal> lastTwo = repository.findLastCompletedRehearsals(2);
        if (lastTwo.size() < 2) {
            return;
        }

        Rehearsal penultimo = lastTwo.get(0);
        Rehearsal ultimo = lastTwo.get(1);

        List<Member> members = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());

        for (Member member : members) {
            boolean missedPenultimo = !repository.hasArrivalForRehearsal(member.telefone(), penultimo.id());
            boolean missedUltimo = !repository.hasArrivalForRehearsal(member.telefone(), ultimo.id());

            if (missedPenultimo && missedUltimo) {
                String message = String.format(
                        "⚠️ *%s* faltou os últimos 2 ensaios consecutivos (%s e %s).",
                        member.nome(),
                        formatDateShort(penultimo.dataHora()),
                        formatDateShort(ultimo.dataHora())
                );
                evolutionClient.sendTextMessage(properties.getPrimaryAdminPhone(), message);
                log.info("Alerta de falta enviado ao admin sobre {}", member.nome());
            }
        }
    }

    private String formatDateShort(String dataHora) {
        return DateUtils.formatDateBr(DateUtils.parseDateFlexible(DateUtils.extractDatePart(dataHora)));
    }
}
