package com.jansen.bot.scheduler;

import com.jansen.bot.model.Rehearsal;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.service.RehearsalCounterService;
import com.jansen.bot.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class RehearsalCloserScheduler {

    private static final Logger log = LoggerFactory.getLogger(RehearsalCloserScheduler.class);

    private final GoogleSheetsRepository repository;
    private final RehearsalCounterService counterService;

    public RehearsalCloserScheduler(GoogleSheetsRepository repository, RehearsalCounterService counterService) {
        this.repository = repository;
        this.counterService = counterService;
    }

    /**
     * Roda a cada hora no minuto 30 (ex: 10:30, 11:30) para verificar se há ensaios que já passaram.
     */
    @Scheduled(cron = "0 30 * * * *")
    public void autoCloseExpiredRehearsals() {
        log.info("Verificando se há ensaios esquecidos para encerrar automaticamente...");
        List<Rehearsal> agendados = repository.findRehearsalsByStatus("AGENDADO");
        LocalDateTime now = DateUtils.nowDateTimeBrazil();

        for (Rehearsal r : agendados) {
            if (r.dataHora() == null || r.dataHora().isBlank()) continue;

            LocalDateTime rehearsalDateTime = null;
            try {
                // Tenta parsear a data e hora do ensaio (ex: 23/07/2026 20:00)
                if (r.dataHora().contains(":")) {
                    LocalDate date = DateUtils.parseDateFlexible(r.dataHora().substring(0, 10));
                    String timeStr = r.dataHora().replaceAll(".*(\\d{2}:\\d{2}).*", "$1");
                    LocalTime time = LocalTime.parse(timeStr);
                    if (date != null) {
                        rehearsalDateTime = LocalDateTime.of(date, time);
                    }
                }
            } catch (Exception e) {
                log.warn("Erro ao ler data/hora do ensaio {}: {}", r.id(), r.dataHora());
            }

            // Se conseguimos ler a data e hora, verificamos se já passou de 3 horas
            if (rehearsalDateTime != null) {
                if (now.isAfter(rehearsalDateTime.plusHours(3))) {
                    log.info("Ensaio {} está pendente há mais de 3h do horário marcado. Encerrando automaticamente.", r.id());
                    counterService.completeRehearsal(r.id());
                }
            } else {
                // Se só tem data (sem horário), encerra à meia-noite do dia seguinte
                LocalDate date = DateUtils.parseDateFlexible(r.dataHora());
                if (date != null && DateUtils.todayBrazil().isAfter(date)) {
                    log.info("Ensaio {} ficou para trás no calendário. Encerrando automaticamente.", r.id());
                    counterService.completeRehearsal(r.id());
                }
            }
        }
    }
}
