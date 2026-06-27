package com.jansen.bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.model.*;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.util.DateUtils;
import com.jansen.bot.util.PhoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Relatórios mensais: presença, pódio e pontualidade.
 */
@Service
public class MonthlyReportService {

    private static final Logger log = LoggerFactory.getLogger(MonthlyReportService.class);

    private final GoogleSheetsRepository repository;
    private final EvolutionClient evolutionClient;
    private final ArrivalService arrivalService;
    private final ObjectMapper objectMapper;

    public MonthlyReportService(GoogleSheetsRepository repository,
                                EvolutionClient evolutionClient,
                                ArrivalService arrivalService,
                                ObjectMapper objectMapper) {
        this.repository = repository;
        this.evolutionClient = evolutionClient;
        this.arrivalService = arrivalService;
        this.objectMapper = objectMapper;
    }

    /**
     * Gera e envia relatórios do mês anterior (dia 1º às 08h).
     */
    public void generateMonthlyReports() {
        YearMonth previous = YearMonth.now().minusMonths(1);
        int mes = previous.getMonthValue();
        int ano = previous.getYear();

        if (repository.hasMonthlyReport(mes, ano)) {
            log.info("Relatório de {}/{} já gerado", mes, ano);
            return;
        }

        String punctuality = arrivalService.buildPunctualityRanking(mes, ano);
        String presence = buildPresenceReport(mes, ano);
        String podium = buildPresencePodium(mes, ano);

        broadcastReport(punctuality);
        broadcastReport(presence);
        broadcastReport(podium);

        saveReport(mes, ano, Map.of(
                "pontualidade", punctuality,
                "presenca", presence,
                "podio", podium
        ));

        log.info("Relatórios mensais de {}/{} enviados", mes, ano);
    }

    private void broadcastReport(String message) {
        repository.findAllMembers().stream()
                .filter(Member::ativo)
                .forEach(m -> evolutionClient.sendTextMessage(m.telefone(), message));
    }

    /**
     * Relatório de presença com emojis por faixa de percentual.
     */
    public String buildPresenceReport(int mes, int ano) {
        List<Rehearsal> rehearsals = repository.findCompletedRehearsalsInMonth(mes, ano);
        int totalRehearsals = rehearsals.size();
        String monthName = DateUtils.capitalize(DateUtils.formatMonthName(mes, ano));

        StringBuilder sb = new StringBuilder(String.format(
                "📋 *Presença — %s (%d ensaios)*\n\n", monthName, totalRehearsals
        ));

        if (totalRehearsals == 0) {
            sb.append("Nenhum ensaio realizado neste mês.");
            return sb.toString();
        }

        List<Member> members = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());

        List<PresenceScore> scores = new ArrayList<>();
        for (Member member : members) {
            long present = rehearsals.stream()
                    .filter(r -> repository.hasArrivalForRehearsal(member.telefone(), r.id()))
                    .count();
            scores.add(new PresenceScore(member.nome(), (int) present, totalRehearsals));
        }

        scores.sort(Comparator.comparingInt(PresenceScore::present).reversed());

        for (PresenceScore score : scores) {
            double pct = (double) score.present / score.total * 100;
            String emoji = pct >= 70 ? "✅" : (pct >= 50 ? "⚠️" : "❌");
            sb.append(emoji).append(" ").append(score.name())
                    .append(" — ").append(score.present).append("/").append(score.total)
                    .append("\n");
        }

        return sb.toString();
    }

    /**
     * Pódio de presença do mês (top 3).
     */
    public String buildPresencePodium(int mes, int ano) {
        List<Rehearsal> rehearsals = repository.findCompletedRehearsalsInMonth(mes, ano);
        String monthName = DateUtils.capitalize(DateUtils.formatMonthName(mes, ano));

        if (rehearsals.isEmpty()) {
            return "🏆 Sem ensaios realizados em " + monthName;
        }

        List<Member> members = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());

        List<PresenceScore> scores = new ArrayList<>();
        for (Member member : members) {
            long present = rehearsals.stream()
                    .filter(r -> repository.hasArrivalForRehearsal(member.telefone(), r.id()))
                    .count();
            scores.add(new PresenceScore(member.nome(), (int) present, rehearsals.size()));
        }

        scores.sort(Comparator.comparingInt(PresenceScore::present).reversed());

        StringBuilder sb = new StringBuilder("🏆 *Presença do mês — " + monthName + "*\n\n");
        String[] medals = {"🥇", "🥈", "🥉"};
        for (int i = 0; i < Math.min(3, scores.size()); i++) {
            PresenceScore s = scores.get(i);
            sb.append(medals[i]).append(" ").append(s.name())
                    .append(" — ").append(s.present).append(" ensaios\n");
        }
        sb.append("\nParabéns aos mais presentes! 👏");

        return sb.toString();
    }

    private void saveReport(int mes, int ano, Map<String, String> data) {
        try {
            String json = objectMapper.writeValueAsString(data);
            repository.saveMonthlyReport(new MonthlyReport(
                    PhoneUtils.generateId(), mes, ano, PhoneUtils.nowFormatted(), json
            ));
        } catch (JsonProcessingException e) {
            log.error("Erro ao salvar relatório mensal: {}", e.getMessage());
        }
    }

    private record PresenceScore(String name, int present, int total) {}
}
