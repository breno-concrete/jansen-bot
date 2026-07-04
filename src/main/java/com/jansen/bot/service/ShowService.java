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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Cadastro de shows com confirmação e lembretes automáticos.
 */
@Service
public class ShowService {

    private static final Logger log = LoggerFactory.getLogger(ShowService.class);

    private final GoogleSheetsRepository repository;
    private final EvolutionClient evolutionClient;

    public ShowService(GoogleSheetsRepository repository, EvolutionClient evolutionClient) {
        this.repository = repository;
        this.evolutionClient = evolutionClient;
    }

    /**
     * Inicia cadastro pedindo confirmação ao admin (não salva ainda).
     */
    public String requestShowRegistration(String adminPhone, ClaudeAction.ActionData dados) {
        String nome = dados.showNome() != null && !dados.showNome().isBlank()
                ? dados.showNome() : "Show";
        String data = dados.showData() != null ? dados.showData() : "";
        String horario = dados.showHorario() != null ? dados.showHorario() : "";
        String local = dados.showLocal() != null ? dados.showLocal() : dados.local();

        Map<String, String> pending = new HashMap<>();
        pending.put("nome", nome);
        pending.put("data", data);
        pending.put("horario", horario);
        pending.put("local", local != null ? local : "A definir");

        String contextJson = toJson(pending);
        repository.saveConversationState(new ConversationState(
                adminPhone, ConversationStates.AGUARDANDO_CONFIRMACAO_SHOW,
                contextJson, PhoneUtils.nowFormatted()
        ));

        return String.format(
                "Vou cadastrar: *%s* no *%s*, %s às %s.\n\nConfirma? (sim/não)",
                nome, pending.get("local"), data, horario
        );
    }

    /**
     * Confirma e persiste show na aba Shows.
     */
    public Show confirmShowRegistration(String adminPhone) {
        ConversationState state = repository.findConversationState(adminPhone).orElse(null);
        if (state == null || !ConversationStates.AGUARDANDO_CONFIRMACAO_SHOW.equals(state.estado())) {
            return null;
        }

        Map<String, String> pending = fromJson(state.contextoJson());
        Show show = new Show(
                PhoneUtils.generateId(),
                pending.getOrDefault("nome", "Show"),
                pending.getOrDefault("data", ""),
                pending.getOrDefault("local", ""),
                pending.getOrDefault("horario", ""),
                PhoneUtils.nowFormatted()
        );
        repository.saveShow(show);
        repository.clearConversationState(adminPhone);
        log.info("Show cadastrado: {}", show.id());
        return show;
    }

    public void cancelShowRegistration(String adminPhone) {
        repository.clearConversationState(adminPhone);
    }

    /**
     * Envia lembretes de show (3 dias ou 1 dia antes).
     */
    public void sendShowReminders(String tipo) {
        for (Show show : repository.findAllShows()) {
            long days = DateUtils.daysUntil(show.data());
            boolean shouldSend = ("3dias".equals(tipo) && days == 3)
                    || ("1dia".equals(tipo) && days == 1);

            if (!shouldSend || repository.isShowReminderSent(show.id(), tipo)) {
                continue;
            }

            String message = buildReminderMessage(show, tipo);
            List<Member> activeMembers = repository.findAllMembers().stream()
                    .filter(Member::ativo)
                    .collect(Collectors.toList());
            evolutionClient.sendTextMessageSeries(activeMembers, message);

            repository.saveShowReminder(new ShowReminder(
                    PhoneUtils.generateId(), show.id(), tipo, true, PhoneUtils.nowFormatted()
            ));
            log.info("Lembrete {} enviado para show {}", tipo, show.id());
        }
    }

    private String buildReminderMessage(Show show, String tipo) {
        if ("1dia".equals(tipo)) {
            return String.format(
                    "🎸 *Show amanhã!*\n\n*%s*\n📍 %s\n📅 %s às %s\n\n" +
                    "Checklist: não esquece instrumento, cabo e roupa! 👕🎸",
                    show.nome(), show.local(), show.data(), show.horario()
            );
        }
        return String.format(
                "🎸 *Show em 3 dias!*\n\n*%s* — %s, %s às %s.\n\n" +
                "Confirme presença com *SIM* ou *NÃO*!",
                show.nome(), show.local(), show.data(), show.horario()
        );
    }

    private String toJson(Map<String, String> map) {
        try {
            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> fromJson(String json) {
        try {
            return new ObjectMapper().readValue(json, Map.class);
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }
}
