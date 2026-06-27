package com.jansen.bot.repository;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.*;
import com.jansen.bot.util.DateUtils;
import com.jansen.bot.util.PhoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositório que persiste e lê dados do Google Sheets.
 */
@Repository
public class GoogleSheetsRepository {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetsRepository.class);

    private final Sheets sheets;
    private final AppProperties properties;

    public GoogleSheetsRepository(Sheets sheets, AppProperties properties) {
        this.sheets = sheets;
        this.properties = properties;
    }

    // ==================== MEMBERS ====================

    public List<Member> findAllMembers() {
        List<List<Object>> rows = readRange(properties.getTabMembers() + "!A2:F");
        List<Member> members = new ArrayList<>();
        for (List<Object> row : rows) {
            if (row.size() >= 4) {
                members.add(new Member(
                        str(row, 0), str(row, 1), str(row, 2), str(row, 3),
                        bool(row, 4, true), bool(row, 5, false)
                ));
            }
        }
        return members;
    }

    public Optional<Member> findMemberByPhone(String phone) {
        String normalized = PhoneUtils.normalize(phone);
        return findAllMembers().stream()
                .filter(m -> PhoneUtils.normalize(m.telefone()).equals(normalized))
                .findFirst();
    }

    // ==================== REHEARSALS ====================

    public List<Rehearsal> findAllRehearsals() {
        List<List<Object>> rows = readRange(properties.getTabRehearsals() + "!A2:G");
        List<Rehearsal> rehearsals = new ArrayList<>();
        for (List<Object> row : rows) {
            if (row.size() >= 4) {
                rehearsals.add(mapRehearsal(row));
            }
        }
        return rehearsals;
    }

    public Optional<Rehearsal> findRehearsalById(String id) {
        return findAllRehearsals().stream().filter(r -> r.id().equals(id)).findFirst();
    }

    public Optional<Rehearsal> findNextScheduledRehearsal() {
        return findAllRehearsals().stream()
                .filter(r -> "AGENDADO".equalsIgnoreCase(r.status()) || "VOTACAO".equalsIgnoreCase(r.status()))
                .findFirst();
    }

    /** Ensaio agendado que acontece hoje. */
    public Optional<Rehearsal> findTodayRehearsal() {
        return findAllRehearsals().stream()
                .filter(r -> "AGENDADO".equalsIgnoreCase(r.status()))
                .filter(r -> DateUtils.isRehearsalToday(r.dataHora()))
                .findFirst();
    }

    public List<Rehearsal> findRehearsalsByStatus(String status) {
        return findAllRehearsals().stream()
                .filter(r -> status.equalsIgnoreCase(r.status()))
                .collect(Collectors.toList());
    }

    public long countRehearsalsByStatus(String status) {
        return findRehearsalsByStatus(status).size();
    }

    public List<Rehearsal> findLastCompletedRehearsals(int limit) {
        List<Rehearsal> completed = findRehearsalsByStatus("REALIZADO").stream()
                .sorted(Comparator.comparing(Rehearsal::dataHora).reversed())
                .limit(limit)
                .collect(Collectors.toList());
        Collections.reverse(completed);
        return completed;
    }

    public List<Rehearsal> findCompletedRehearsalsInMonth(int mes, int ano) {
        YearMonth ym = YearMonth.of(ano, mes);
        return findRehearsalsByStatus("REALIZADO").stream()
                .filter(r -> isInMonth(r.dataHora(), ym))
                .collect(Collectors.toList());
    }

    public void saveRehearsal(Rehearsal rehearsal) {
        appendRow(properties.getTabRehearsals(), rehearsalRow(rehearsal));
    }

    public void updateRehearsal(Rehearsal rehearsal) {
        List<Rehearsal> all = findAllRehearsals();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).id().equals(rehearsal.id())) {
                writeRange(properties.getTabRehearsals() + "!A" + (i + 2) + ":G" + (i + 2),
                        List.of(rehearsalRow(rehearsal)));
                return;
            }
        }
    }

    // ==================== SETLIST ====================

    public List<SetlistSong> findSetlistByRehearsal(String rehearsalId) {
        return findAllSetlist().stream()
                .filter(s -> rehearsalId.equals(s.rehearsalId()))
                .filter(s -> SetlistSong.STATUS_SETLIST.equalsIgnoreCase(s.status())
                        || s.status() == null || s.status().isBlank())
                .sorted(Comparator.comparingInt(SetlistSong::ordem))
                .collect(Collectors.toList());
    }

    public List<SetlistSong> findApprovedRepertoire() {
        return findAllSetlist().stream()
                .filter(s -> SetlistSong.STATUS_APROVADA.equalsIgnoreCase(s.status()))
                .sorted(Comparator.comparingInt(SetlistSong::ordem))
                .collect(Collectors.toList());
    }

    public List<SetlistSong> findPendingSuggestions() {
        return findAllSetlist().stream()
                .filter(s -> SetlistSong.STATUS_SUGERIDA.equalsIgnoreCase(s.status()))
                .collect(Collectors.toList());
    }

    public Optional<SetlistSong> findSetlistSongById(String id) {
        return findAllSetlist().stream().filter(s -> s.id().equals(id)).findFirst();
    }

    public List<SetlistSong> findAllSetlist() {
        List<List<Object>> rows = readRange(properties.getTabSetlist() + "!A2:I");
        List<SetlistSong> songs = new ArrayList<>();
        for (List<Object> row : rows) {
            if (row.size() >= 3) {
                songs.add(mapSetlistSong(row));
            }
        }
        return songs;
    }

    public void saveSetlistSong(SetlistSong song) {
        appendRow(properties.getTabSetlist(), setlistRow(song));
    }

    public void updateSetlistSong(SetlistSong song) {
        List<SetlistSong> all = findAllSetlist();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).id().equals(song.id())) {
                writeRange(properties.getTabSetlist() + "!A" + (i + 2) + ":I" + (i + 2),
                        List.of(setlistRow(song)));
                return;
            }
        }
    }

    public void clearSetlistForRehearsal(String rehearsalId) {
        List<SetlistSong> kept = findAllSetlist().stream()
                .filter(s -> !rehearsalId.equals(s.rehearsalId())
                        || !SetlistSong.STATUS_SETLIST.equalsIgnoreCase(s.status()))
                .collect(Collectors.toList());
        rewriteSetlistTab(kept);
    }

    private void rewriteSetlistTab(List<SetlistSong> songs) {
        clearTabFromRow2(properties.getTabSetlist());
        for (SetlistSong song : songs) {
            appendRow(properties.getTabSetlist(), setlistRow(song));
        }
    }

    // ==================== RESPONSES ====================

    public List<ResponseRecord> findResponsesByRehearsal(String rehearsalId) {
        return findAllResponses().stream()
                .filter(r -> r.rehearsalId().equals(rehearsalId))
                .collect(Collectors.toList());
    }

    public List<ResponseRecord> findAllResponses() {
        List<List<Object>> rows = readRange(properties.getTabResponses() + "!A2:F");
        List<ResponseRecord> records = new ArrayList<>();
        for (List<Object> row : rows) {
            if (row.size() >= 5) {
                records.add(new ResponseRecord(
                        str(row, 0), str(row, 1), str(row, 2),
                        str(row, 3), str(row, 4), str(row, 5)
                ));
            }
        }
        return records;
    }

    public void saveResponse(ResponseRecord record) {
        appendRow(properties.getTabResponses(), List.of(
                record.id(), record.rehearsalId(), record.memberPhone(),
                record.tipo(), record.valor(), record.timestamp()
        ));
    }

    // ==================== SHOWS ====================

    public List<Show> findAllShows() {
        List<List<Object>> rows = readRange(properties.getTabShows() + "!A2:F");
        List<Show> shows = new ArrayList<>();
        for (List<Object> row : rows) {
            if (row.size() >= 4) {
                shows.add(new Show(
                        str(row, 0), str(row, 1), str(row, 2),
                        str(row, 3), str(row, 4), str(row, 5)
                ));
            }
        }
        return shows;
    }

    public Optional<Show> findShowById(String id) {
        return findAllShows().stream().filter(s -> s.id().equals(id)).findFirst();
    }

    public void saveShow(Show show) {
        appendRow(properties.getTabShows(), List.of(
                show.id(), show.nome(), show.data(),
                show.local(), show.horario(), show.criadoEm()
        ));
    }

    // ==================== SHOW REMINDERS ====================

    public List<ShowReminder> findAllShowReminders() {
        List<List<Object>> rows = readRange(properties.getTabShowReminders() + "!A2:E");
        List<ShowReminder> reminders = new ArrayList<>();
        for (List<Object> row : rows) {
            if (row.size() >= 3) {
                reminders.add(new ShowReminder(
                        str(row, 0), str(row, 1), str(row, 2),
                        bool(row, 3, false), str(row, 4)
                ));
            }
        }
        return reminders;
    }

    public boolean isShowReminderSent(String showId, String tipo) {
        return findAllShowReminders().stream()
                .anyMatch(r -> showId.equals(r.showId()) && tipo.equals(r.tipo()) && r.enviado());
    }

    public void saveShowReminder(ShowReminder reminder) {
        appendRow(properties.getTabShowReminders(), List.of(
                reminder.id(), reminder.showId(), reminder.tipo(),
                reminder.enviado(), reminder.enviadoEm()
        ));
    }

    // ==================== ARRIVALS ====================

    public List<Arrival> findAllArrivals() {
        List<List<Object>> rows = readRange(properties.getTabArrivals() + "!A2:E");
        List<Arrival> arrivals = new ArrayList<>();
        for (List<Object> row : rows) {
            if (row.size() >= 4) {
                arrivals.add(new Arrival(
                        str(row, 0), str(row, 1), str(row, 2),
                        str(row, 3), str(row, 4)
                ));
            }
        }
        return arrivals;
    }

    public List<Arrival> findArrivalsByRehearsal(String rehearsalId) {
        return findAllArrivals().stream()
                .filter(a -> rehearsalId.equals(a.rehearsalId()))
                .collect(Collectors.toList());
    }

    public List<Arrival> findArrivalsByMemberInMonth(String phone, int mes, int ano) {
        String normalized = PhoneUtils.normalize(phone);
        YearMonth ym = YearMonth.of(ano, mes);
        return findAllArrivals().stream()
                .filter(a -> PhoneUtils.normalize(a.memberPhone()).equals(normalized))
                .filter(a -> isInMonth(a.data(), ym))
                .collect(Collectors.toList());
    }

    public boolean hasArrivalForRehearsal(String phone, String rehearsalId) {
        String normalized = PhoneUtils.normalize(phone);
        return findArrivalsByRehearsal(rehearsalId).stream()
                .anyMatch(a -> PhoneUtils.normalize(a.memberPhone()).equals(normalized));
    }

    public void saveArrival(Arrival arrival) {
        appendRow(properties.getTabArrivals(), List.of(
                arrival.id(), arrival.memberPhone(), arrival.rehearsalId(),
                arrival.horarioChegada(), arrival.data()
        ));
    }

    // ==================== MEMBER OF MONTH VOTES ====================

    public List<MemberOfMonthVote> findVotesByMonth(int mes, int ano) {
        return findAllMemberOfMonthVotes().stream()
                .filter(v -> v.mes() == mes && v.ano() == ano)
                .collect(Collectors.toList());
    }

    public List<MemberOfMonthVote> findAllMemberOfMonthVotes() {
        List<List<Object>> rows = readRange(properties.getTabMemberOfMonthVotes() + "!A2:E");
        List<MemberOfMonthVote> votes = new ArrayList<>();
        for (List<Object> row : rows) {
            if (row.size() >= 5) {
                votes.add(new MemberOfMonthVote(
                        str(row, 0), str(row, 1), str(row, 2),
                        intVal(row, 3, 0), intVal(row, 4, 0)
                ));
            }
        }
        return votes;
    }

    public boolean hasVotedInMonth(String voterPhone, int mes, int ano) {
        String normalized = PhoneUtils.normalize(voterPhone);
        return findVotesByMonth(mes, ano).stream()
                .anyMatch(v -> PhoneUtils.normalize(v.voterPhone()).equals(normalized));
    }

    public void saveMemberOfMonthVote(MemberOfMonthVote vote) {
        appendRow(properties.getTabMemberOfMonthVotes(), List.of(
                vote.id(), vote.voterPhone(), vote.votedPhone(),
                vote.mes(), vote.ano()
        ));
    }

    // ==================== MONTHLY REPORT ====================

    public void saveMonthlyReport(MonthlyReport report) {
        appendRow(properties.getTabMonthlyReport(), List.of(
                report.id(), report.mes(), report.ano(),
                report.geradoEm(), report.dadosJson()
        ));
    }

    public boolean hasMonthlyReport(int mes, int ano) {
        List<List<Object>> rows = readRange(properties.getTabMonthlyReport() + "!A2:E");
        for (List<Object> row : rows) {
            if (intVal(row, 1, 0) == mes && intVal(row, 2, 0) == ano) {
                return true;
            }
        }
        return false;
    }

    // ==================== CONVERSATION STATE ====================

    public Optional<ConversationState> findConversationState(String phone) {
        String normalized = PhoneUtils.normalize(phone);
        List<List<Object>> rows = readRange(properties.getTabConversation() + "!A2:D");
        for (List<Object> row : rows) {
            if (row.size() >= 2 && PhoneUtils.normalize(str(row, 0)).equals(normalized)) {
                return Optional.of(new ConversationState(
                        str(row, 0), str(row, 1), str(row, 2), str(row, 3)
                ));
            }
        }
        return Optional.empty();
    }

    public void saveConversationState(ConversationState state) {
        String normalized = PhoneUtils.normalize(state.memberPhone());
        List<List<Object>> rows = readRange(properties.getTabConversation() + "!A2:D");
        for (int i = 0; i < rows.size(); i++) {
            if (PhoneUtils.normalize(str(rows.get(i), 0)).equals(normalized)) {
                writeRange(properties.getTabConversation() + "!A" + (i + 2) + ":D" + (i + 2),
                        List.of(List.of(state.memberPhone(), state.estado(),
                                state.contextoJson(), state.updatedAt())));
                return;
            }
        }
        appendRow(properties.getTabConversation(), List.of(
                state.memberPhone(), state.estado(), state.contextoJson(), state.updatedAt()
        ));
    }

    public void clearConversationState(String phone) {
        saveConversationState(new ConversationState(
                phone, ConversationStates.LIVRE, "{}", PhoneUtils.nowFormatted()
        ));
    }

    /** Verifica se há votação de membro do mês aberta (estado no admin). */
    public boolean isMemberOfMonthVotingOpen() {
        return findAllMembers().stream()
                .filter(Member::admin)
                .anyMatch(m -> findConversationState(m.telefone())
                        .map(s -> ConversationStates.VOTACAO_MEMBRO_ABERTA.equals(s.estado()))
                        .orElse(false));
    }

    // ==================== HELPERS ====================

    private List<Object> rehearsalRow(Rehearsal r) {
        return List.of(r.id(), r.dataHora(), r.local(), r.status(),
                r.opcoesVoto(), r.vencedor(), r.lembreteEnviado());
    }

    private Rehearsal mapRehearsal(List<Object> row) {
        return new Rehearsal(
                str(row, 0), str(row, 1), str(row, 2), str(row, 3),
                str(row, 4), str(row, 5), bool(row, 6, false)
        );
    }

    private List<Object> setlistRow(SetlistSong s) {
        return List.of(
                s.id(), s.rehearsalId(), s.musica(), s.ordem(), s.artista(),
                s.status() != null ? s.status() : SetlistSong.STATUS_SETLIST,
                s.link() != null ? s.link() : "",
                s.suggestedBy() != null ? s.suggestedBy() : "",
                s.descricao() != null ? s.descricao() : ""
        );
    }

    private SetlistSong mapSetlistSong(List<Object> row) {
        String status = str(row, 5);
        if (status.isBlank()) {
            status = SetlistSong.STATUS_SETLIST;
        }
        return new SetlistSong(
                str(row, 0), str(row, 1), str(row, 2), intVal(row, 3, 0), str(row, 4),
                status, str(row, 6), str(row, 7), str(row, 8)
        );
    }

    private boolean isInMonth(String dateStr, YearMonth ym) {
        LocalDate date = DateUtils.parseDateFlexible(DateUtils.extractDatePart(dateStr));
        if (date == null) {
            date = DateUtils.parseDateFlexible(dateStr);
        }
        return date != null && YearMonth.from(date).equals(ym);
    }

    private List<List<Object>> readRange(String range) {
        try {
            ValueRange response = sheets.spreadsheets().values()
                    .get(properties.getSpreadsheetId(), range)
                    .execute();
            return response.getValues() != null ? response.getValues() : Collections.emptyList();
        } catch (IOException e) {
            log.error("Erro ao ler planilha {}: {}", range, e.getMessage());
            return Collections.emptyList();
        }
    }

    private void appendRow(String tab, List<Object> values) {
        try {
            ValueRange body = new ValueRange().setValues(List.of(values));
            sheets.spreadsheets().values()
                    .append(properties.getSpreadsheetId(), tab + "!A1", body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
        } catch (IOException e) {
            log.error("Erro ao inserir linha em {}: {}", tab, e.getMessage());
        }
    }

    private void writeRange(String range, List<List<Object>> values) {
        try {
            ValueRange body = new ValueRange().setValues(values);
            sheets.spreadsheets().values()
                    .update(properties.getSpreadsheetId(), range, body)
                    .setValueInputOption("USER_ENTERED")
                    .execute();
        } catch (IOException e) {
            log.error("Erro ao atualizar {}: {}", range, e.getMessage());
        }
    }

    private void clearTabFromRow2(String tab) {
        writeRange(tab + "!A2:Z1000", Collections.emptyList());
    }

    private String str(List<Object> row, int index) {
        return row.size() > index && row.get(index) != null ? row.get(index).toString() : "";
    }

    private boolean bool(List<Object> row, int index, boolean defaultVal) {
        if (row.size() <= index || row.get(index) == null) return defaultVal;
        String val = row.get(index).toString().toLowerCase();
        return "true".equals(val) || "sim".equals(val) || "1".equals(val);
    }

    private int intVal(List<Object> row, int index, int defaultVal) {
        if (row.size() <= index || row.get(index) == null) return defaultVal;
        try {
            return Integer.parseInt(row.get(index).toString());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
