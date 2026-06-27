package com.jansen.bot.service;

import com.jansen.bot.model.Arrival;
import com.jansen.bot.model.Member;
import com.jansen.bot.model.Rehearsal;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.util.DateUtils;
import com.jansen.bot.util.PhoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Registro de chegada em ensaios e cálculo de ranking de pontualidade.
 */
@Service
public class ArrivalService {

    private static final Logger log = LoggerFactory.getLogger(ArrivalService.class);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final GoogleSheetsRepository repository;

    public ArrivalService(GoogleSheetsRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra chegada do membro no ensaio de hoje.
     */
    public String registerArrival(String memberPhone) {
        Optional<Rehearsal> todayRehearsal = repository.findTodayRehearsal();
        String memberName = repository.findMemberByPhone(memberPhone)
                .map(Member::nome).orElse("membro");

        if (todayRehearsal.isEmpty()) {
            return String.format("Não tem ensaio hoje, %s 😄", memberName);
        }

        Rehearsal rehearsal = todayRehearsal.get();
        String nowTime = LocalTime.now().format(TIME_FMT);
        String today = LocalDate.now().toString();

        Arrival arrival = new Arrival(
                PhoneUtils.generateId(),
                memberPhone,
                rehearsal.id(),
                nowTime,
                today
        );
        repository.saveArrival(arrival);
        log.info("Chegada registrada: {} no ensaio {}", memberPhone, rehearsal.id());

        return String.format("✅ Chegada registrada às %s, %s!", nowTime, memberName);
    }

    /**
     * Monta ranking de pontualidade do mês anterior (média de horário de chegada).
     */
    public String buildPunctualityRanking(int mes, int ano) {
        List<Member> members = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());

        List<PunctualityScore> scores = new ArrayList<>();
        for (Member member : members) {
            List<Arrival> arrivals = repository.findArrivalsByMemberInMonth(member.telefone(), mes, ano);
            if (arrivals.isEmpty()) {
                continue;
            }
            double avgMinutes = arrivals.stream()
                    .mapToInt(a -> parseTimeToMinutes(a.horarioChegada()))
                    .average()
                    .orElse(0);
            scores.add(new PunctualityScore(member.nome(), avgMinutes));
        }

        scores.sort(Comparator.comparingDouble(PunctualityScore::avgMinutes));

        String monthName = DateUtils.capitalize(DateUtils.formatMonthName(mes, ano));
        StringBuilder sb = new StringBuilder("⏱ *Ranking de pontualidade — " + monthName + "*\n\n");

        String[] medals = {"🥇", "🥈", "🥉"};
        for (int i = 0; i < scores.size(); i++) {
            PunctualityScore score = scores.get(i);
            String medal = i < 3 ? medals[i] + " " : "   ";
            sb.append(medal).append(score.name()).append(" — média ")
                    .append(minutesToTime((int) score.avgMinutes())).append("\n");
        }

        if (scores.isEmpty()) {
            sb.append("Sem registros de chegada neste mês.");
        }

        return sb.toString();
    }

    private int parseTimeToMinutes(String time) {
        try {
            LocalTime lt = LocalTime.parse(time, TIME_FMT);
            return lt.getHour() * 60 + lt.getMinute();
        } catch (Exception e) {
            return 0;
        }
    }

    private String minutesToTime(int minutes) {
        return String.format("%dh%02d", minutes / 60, minutes % 60);
    }

    private record PunctualityScore(String name, double avgMinutes) {}
}
