package com.jansen.bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.*;
import com.jansen.bot.repository.GoogleSheetsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Monta o contexto da banda para enviar à Claude API.
 */
@Service
public class ContextService {

    private final GoogleSheetsRepository repository;
    private final AppProperties properties;
    private final ObjectMapper objectMapper;

    public ContextService(GoogleSheetsRepository repository, AppProperties properties, ObjectMapper objectMapper) {
        this.repository = repository;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public BandContext buildContext(String memberPhone) {
        List<Member> membros = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());

        List<Rehearsal> ensaios = repository.findAllRehearsals();
        Optional<Rehearsal> proximo = repository.findNextScheduledRehearsal();

        List<SetlistSong> setlist = proximo
                .map(r -> repository.findSetlistByRehearsal(r.id()))
                .orElse(List.of());

        List<ResponseRecord> respostas = proximo
                .map(r -> repository.findResponsesByRehearsal(r.id()))
                .orElse(List.of());

        ConversationState estado = repository.findConversationState(memberPhone)
                .orElse(new ConversationState(memberPhone, ConversationStates.LIVRE, "{}", ""));

        String resumo = proximo.map(this::formatRehearsalSummary).orElse("Nenhum ensaio agendado");

        List<Show> shows = repository.findAllShows();
        List<Arrival> chegadas = repository.findAllArrivals().stream()
                .limit(20)
                .collect(Collectors.toList());
        int totalRealizados = (int) repository.countRehearsalsByStatus("REALIZADO");

        return new BandContext(
                properties.getBandaNome(),
                membros,
                ensaios,
                setlist,
                respostas,
                estado,
                resumo,
                shows,
                chegadas,
                totalRealizados,
                repository.isMemberOfMonthVotingOpen(),
                repository.findApprovedRepertoire(),
                repository.findPendingSuggestions()
        );
    }

    public String toJson(BandContext context) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(context);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private String formatRehearsalSummary(Rehearsal r) {
        return String.format("ID=%s | Data=%s | Local=%s | Status=%s | Opções=%s",
                r.id(), r.dataHora(), r.local(), r.status(), r.opcoesVoto());
    }
}
