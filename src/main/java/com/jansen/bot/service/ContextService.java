package com.jansen.bot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.*;
import com.jansen.bot.rehearsal.domain.Ensaio;
import com.jansen.bot.rehearsal.ports.RehearsalRepositoryPort;
import com.jansen.bot.repository.GoogleSheetsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Monta o contexto da banda para enviar à Claude API.
 */
@Service
public class ContextService {

    private final GoogleSheetsRepository repository;
    private final AppProperties properties;
    private final ObjectMapper objectMapper;
    private final RehearsalRepositoryPort ensaios;

    public ContextService(GoogleSheetsRepository repository, AppProperties properties, ObjectMapper objectMapper,
                          RehearsalRepositoryPort ensaios) {
        this.repository = repository;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.ensaios = ensaios;
    }

    public BandContext buildContext(String memberPhone) {
        List<Member> membros = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());

        List<Rehearsal> ensaios = repository.findAllRehearsals().stream()
                .filter(r -> !"REALIZADO".equalsIgnoreCase(r.status()))
                .collect(Collectors.toList());
        Optional<Rehearsal> proximo = repository.findNextScheduledRehearsal();

        List<SetlistSong> setlist = proximo
                .map(r -> repository.findSetlistByRehearsal(r.id()))
                .orElse(List.of());

        List<ResponseRecord> respostas = proximo
                .map(r -> repository.findResponsesByRehearsal(r.id()))
                .orElse(List.of());

        ConversationState estado = repository.findConversationState(memberPhone)
                .orElse(new ConversationState(memberPhone, ConversationStates.LIVRE, "{}", ""));

        String resumo = formatResumo(proximo);

        List<Show> shows = List.of();
        List<Arrival> chegadas = List.of();
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
                List.of(),
                List.of()
        );
    }

    public String toJson(BandContext context) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(context);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    /**
     * T023A / P-032: ensaios novos (Postgres, votação aberta) primeiro, depois o legado do Sheets.
     * Os novos entram como "AGENDADO" porque é o status que o system-prompt usa para acionar CONFIRMAR_PRESENCA.
     */
    private String formatResumo(Optional<Rehearsal> proximoLegado) {
        List<String> linhas = Stream.concat(
                ensaios.buscarComVotacaoAberta().stream().map(this::formatEnsaioSummary),
                proximoLegado.stream().map(this::formatRehearsalSummary)
        ).collect(Collectors.toList());
        return linhas.isEmpty() ? "Nenhum ensaio agendado" : String.join("\n", linhas);
    }

    private String formatEnsaioSummary(Ensaio e) {
        return String.format("Ensaio %s | Data=%s | Local=%s | Status=AGENDADO",
                e.tipo().name().toLowerCase(), e.dataHora(), e.local());
    }

    private String formatRehearsalSummary(Rehearsal r) {
        return String.format("ID=%s | Data=%s | Local=%s | Status=%s | Opções=%s",
                r.id(), r.dataHora(), r.local(), r.status(), r.opcoesVoto());
    }
}
