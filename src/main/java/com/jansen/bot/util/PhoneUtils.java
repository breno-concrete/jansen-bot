package com.jansen.bot.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Utilitários para normalização de telefone e formatação de datas.
 */
public final class PhoneUtils {

    private static final DateTimeFormatter SHEET_DATETIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private PhoneUtils() {}

    public static String normalize(String phone) {
        if (phone == null) {
            return "";
        }
        String digits = phone.replaceAll("[^0-9]", "");
        if (digits.startsWith("55") && digits.length() >= 12) {
            digits = digits.substring(2);
        }
        if (digits.length() == 11 && digits.charAt(2) == '9') {
            digits = digits.substring(0, 2) + digits.substring(3);
        }
        return digits;
    }

    /**
     * Formata telefone para JID do WhatsApp.
     */
    public static String toWhatsAppJid(String phone) {
        String normalized = normalize(phone);
        return normalized + "@s.whatsapp.net";
    }

    public static String generateId() {
        return "j" + UUID.randomUUID().toString().substring(0, 7);
    }

    public static String nowFormatted() {
        return LocalDateTime.now().format(SHEET_DATETIME);
    }
}
