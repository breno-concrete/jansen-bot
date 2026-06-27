package com.jansen.bot.scheduler;

import com.jansen.bot.service.MonthlyReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Gera relatórios mensais no dia 1º às 08h:
 * ranking de pontualidade, presença e pódio.
 */
@Component
public class MonthlyReportScheduler {

    private static final Logger log = LoggerFactory.getLogger(MonthlyReportScheduler.class);

    private final MonthlyReportService monthlyReportService;

    public MonthlyReportScheduler(MonthlyReportService monthlyReportService) {
        this.monthlyReportService = monthlyReportService;
    }

    @Scheduled(cron = "${scheduler.monthly-report.cron}", zone = "${scheduler.monthly-report.timezone}")
    public void generateMonthlyReports() {
        log.info("Gerando relatórios mensais...");
        monthlyReportService.generateMonthlyReports();
    }
}
