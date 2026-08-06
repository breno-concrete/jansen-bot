package com.jansen.bot.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Utilitários para parsing e formatação de datas usados nas features novas.
 */
public final class DateUtils {

    private static final DateTimeFormatter DATE_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_BR_SHORT = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter DATE_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_HM = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter MONTH_NAME = DateTimeFormatter.ofPattern("MMMM", new Locale("pt", "BR"));

    /** Fuso horário oficial do Brasil (Brasília). */
    public static final ZoneId ZONE_BR = ZoneId.of("America/Sao_Paulo");

    private DateUtils() {}

    /** Retorna a data de HOJE no horário de Brasília (não UTC). */
    public static LocalDate todayBrazil() {
        return LocalDate.now(ZONE_BR);
    }

    /** Retorna a hora AGORA no horário de Brasília (não UTC). */
    public static LocalTime nowBrazil() {
        return LocalTime.now(ZONE_BR);
    }

    /** Retorna data e hora AGORA no horário de Brasília (não UTC). */
    public static LocalDateTime nowDateTimeBrazil() {
        return LocalDateTime.now(ZONE_BR);
    }

    public static LocalDate parseDateFlexible(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        String trimmed = dateStr.trim();
        for (DateTimeFormatter fmt : new DateTimeFormatter[]{DATE_ISO, DATE_BR, DATE_BR_SHORT}) {
            try {
                if (fmt == DATE_BR_SHORT) {
                    return LocalDate.parse(trimmed + "/" + todayBrazil().getYear(), DATE_BR);
                }
                return LocalDate.parse(trimmed, fmt);
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    public static LocalTime parseTime(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) {
            return null;
        }
        try {
            String normalized = timeStr.trim().replace('h', ':');
            if (normalized.matches("\\d{1,2}:\\d{2}")) {
                return LocalTime.parse(normalized, TIME_HM);
            }
            if (normalized.matches("\\d{1,2}")) {
                return LocalTime.of(Integer.parseInt(normalized), 0);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String formatDateBr(LocalDate date) {
        return date != null ? date.format(DATE_BR) : "";
    }

    public static String formatTime(LocalTime time) {
        return time != null ? time.format(TIME_HM) : "";
    }

    public static String formatMonthName(int mes, int ano) {
        return LocalDate.of(ano, mes, 1).format(MONTH_NAME);
    }

    public static String capitalize(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public static boolean isToday(String dateStr) {
        LocalDate date = parseDateFlexible(dateStr);
        return date != null && date.equals(todayBrazil());
    }

    public static boolean isRehearsalToday(String dataHora) {
        if (dataHora == null || dataHora.isBlank()) {
            return false;
        }
        try {
            LocalDateTime dt = LocalDateTime.parse(dataHora, DATETIME);
            return dt.toLocalDate().equals(todayBrazil());
        } catch (DateTimeParseException e) {
            return isToday(dataHora);
        }
    }

    public static long daysUntil(String dateStr) {
        LocalDate date = parseDateFlexible(dateStr);
        if (date == null) {
            return -1;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(todayBrazil(), date);
    }

    public static String extractDatePart(String dataHora) {
        if (dataHora == null) {
            return "";
        }
        if (dataHora.length() >= 10) {
            return dataHora.substring(0, 10);
        }
        return dataHora;
    }
}

