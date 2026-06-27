package com.jansen.bot.scheduler;

import com.jansen.bot.model.Rehearsal;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.service.RehearsalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Scheduler que envia lembretes automáticos 24h antes do ensaio.
 * Roda diariamente às 9h (configurável via application.properties).
 */
@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final GoogleSheetsRepository repository;
    private final RehearsalService rehearsalService;

    public ReminderScheduler(GoogleSheetsRepository repository, RehearsalService rehearsalService) {
        this.repository = repository;
        this.rehearsalService = rehearsalService;
    }

    @Scheduled(cron = "${scheduler.reminder.cron}", zone = "${scheduler.reminder.timezone}")
    public void sendReminders() {
        log.info("Verificando ensaios para lembrete 24h...");

        List<Rehearsal> rehearsals = repository.findAllRehearsals();

        for (Rehearsal rehearsal : rehearsals) {
            if (!"AGENDADO".equals(rehearsal.status()) || rehearsal.lembreteEnviado()) {
                continue;
            }

            if (isWithin24Hours(rehearsal.dataHora())) {
                log.info("Enviando lembrete para ensaio {}", rehearsal.id());
                rehearsalService.sendReminders(rehearsal);
            }
        }
    }

    /**
     * Verifica se o ensaio está entre 23h e 25h no futuro.
     */
    private boolean isWithin24Hours(String dataHora) {
        if (dataHora == null || dataHora.isBlank()) {
            return false;
        }
        try {
            LocalDateTime rehearsalTime = LocalDateTime.parse(dataHora, FORMATTER);
            long hoursUntil = ChronoUnit.HOURS.between(LocalDateTime.now(), rehearsalTime);
            return hoursUntil >= 23 && hoursUntil <= 25;
        } catch (Exception e) {
            log.warn("Data de ensaio inválida: {}", dataHora);
            return false;
        }
    }
}
