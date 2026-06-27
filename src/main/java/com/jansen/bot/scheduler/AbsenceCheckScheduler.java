package com.jansen.bot.scheduler;

import com.jansen.bot.service.AbsenceAlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Verifica faltas consecutivas nos últimos 2 ensaios e alerta o admin.
 * Roda diariamente às 22h.
 */
@Component
public class AbsenceCheckScheduler {

    private static final Logger log = LoggerFactory.getLogger(AbsenceCheckScheduler.class);

    private final AbsenceAlertService absenceAlertService;

    public AbsenceCheckScheduler(AbsenceAlertService absenceAlertService) {
        this.absenceAlertService = absenceAlertService;
    }

    @Scheduled(cron = "${scheduler.absence-check.cron}", zone = "${scheduler.absence-check.timezone}")
    public void checkAbsences() {
        log.info("Verificando faltas consecutivas...");
        absenceAlertService.checkConsecutiveAbsences();
    }
}
