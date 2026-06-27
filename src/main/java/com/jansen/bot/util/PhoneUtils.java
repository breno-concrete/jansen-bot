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

    /**
     * Normaliza número de telefone removendo caracteres não numéricos.
     */
    public static String normalize(String phone) {
        if (phone == null) {
            return "";
        }
        return phone.replaceAll("[^0-9]", "");
    }

    /**
     * Formata telefone para JID do WhatsApp.
     */
    public static String toWhatsAppJid(String phone) {
        String normalized = normalize(phone);
        return normalized + "@s.whatsapp.net";
    }

    public static String generateId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public static String nowFormatted() {
        return LocalDateTime.now().format(SHEET_DATETIME);
    }
}
