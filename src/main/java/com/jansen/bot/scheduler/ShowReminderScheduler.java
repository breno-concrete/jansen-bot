package com.jansen.bot.scheduler;

import com.jansen.bot.service.ShowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Envia lembretes de show: 3 dias antes e 1 dia antes.
 * Roda diariamente às 09h.
 */
@Component
public class ShowReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ShowReminderScheduler.class);

    private final ShowService showService;

    public ShowReminderScheduler(ShowService showService) {
        this.showService = showService;
    }

    @Scheduled(cron = "${scheduler.show-reminder.cron}", zone = "${scheduler.show-reminder.timezone}")
    public void sendShowReminders() {
        log.info("Verificando shows para lembretes...");
        showService.sendShowReminders("3dias");
        showService.sendShowReminders("1dia");
    }
}
