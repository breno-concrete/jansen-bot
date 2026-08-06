package com.jansen.bot.service;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.*;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.util.PhoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Interpreta a ação retornada pela Claude e executa a operação correspondente.
 */
@Service
public class ActionDispatcher {

    private static final Logger log = LoggerFactory.getLogger(ActionDispatcher.class);

    private final RehearsalService rehearsalService;
    private final BroadcastService broadcastService;
    private final GoogleSheetsRepository repository;
    private final EvolutionClient evolutionClient;
    private final AppProperties properties;
    private final ShowService showService;
    private final ArrivalService arrivalService;
    private final MemberOfMonthService memberOfMonthService;
    private final MusicSuggestionService musicSuggestionService;
    private final RehearsalCounterService rehearsalCounterService;

    public ActionDispatcher(RehearsalService rehearsalService,
                            BroadcastService broadcastService,
                            GoogleSheetsRepository repository,
                            EvolutionClient evolutionClient,
                            AppProperties properties,
                            ShowService showService,
                            ArrivalService arrivalService,
                            MemberOfMonthService memberOfMonthService,
                            MusicSuggestionService musicSuggestionService,
                            RehearsalCounterService rehearsalCounterService) {
        this.rehearsalService = rehearsalService;
        this.broadcastService = broadcastService;
        this.repository = repository;
        this.evolutionClient = evolutionClient;
        this.properties = properties;
        this.showService = showService;
        this.arrivalService = arrivalService;
        this.memberOfMonthService = memberOfMonthService;
        this.musicSuggestionService = musicSuggestionService;
        this.rehearsalCounterService = rehearsalCounterService;
    }

    /**
     * Executa a ação e retorna a resposta final para enviar ao membro.
     */
    public String dispatch(String memberPhone, ClaudeAction action) {
        String acao = normalizeAction(action.acao());
        ClaudeAction.ActionData dados = action.dados();

        return switch (acao) {
            // Ensaios
            case BotAction.AGENDAR_ENSAIO -> handleScheduleRehearsal(memberPhone, action);
            case BotAction.VOTAR_DATA -> {
                handleVote(memberPhone, dados);
                yield fallback(action.resposta(), "Anotado seu voto! 👍");
            }
            case BotAction.CONFIRMAR_PRESENCA -> {
                handlePresence(memberPhone, dados, true);
                yield fallback(action.resposta(), "Show! Te espero no ensaio 🎸");
            }
            case BotAction.NEGAR_PRESENCA -> {
                handlePresence(memberPhone, dados, false);
                yield fallback(action.resposta(), "Beleza, anotei que você não vai 👍");
            }
            case BotAction.STATUS_PRESENCA -> handlePresenceStatus(dados);
            case BotAction.CONCLUIR_ENSAIO -> handleCompleteRehearsal(memberPhone, dados);
            case BotAction.CONTAR_ENSAIOS -> rehearsalCounterService.formatCountMessage();

            // Broadcast e setlist
            case BotAction.BROADCAST -> handleBroadcast(memberPhone, dados, action);
            case BotAction.VER_SETLIST -> handleViewSetlist(dados, action);
            case BotAction.ATUALIZAR_SETLIST -> handleUpdateSetlist(memberPhone, dados, action);

            // Shows
            case BotAction.CADASTRO_SHOW -> handleShowRegistration(memberPhone, dados);
            case BotAction.CONFIRMAR_SHOW -> handleConfirmShow(memberPhone);
            case BotAction.CANCELAR_SHOW -> handleCancelShow(memberPhone);

            // Chegada
            case BotAction.REGISTRAR_CHEGADA -> arrivalService.registerArrival(memberPhone);

            // Membro do mês
            case BotAction.ABERTURA_VOTACAO_MEMBRO -> handleOpenMemberVote(memberPhone);
            case BotAction.VOTAR_MEMBRO_MES -> handleMemberOfMonthVote(memberPhone, dados, action);
            case BotAction.ENCERRAR_VOTACAO_MEMBRO -> handleCloseMemberVote(memberPhone);

            // Músicas
            case BotAction.SUGERIR_MUSICA -> musicSuggestionService.suggestMusic(memberPhone, dados);
            case BotAction.APROVAR_MUSICA -> handleApproveMusic(memberPhone);
            case BotAction.REJEITAR_MUSICA -> handleRejectMusic(memberPhone);
            case BotAction.CONSULTAR_REPERTORIO -> musicSuggestionService.formatApprovedRepertoire();

            default -> action.resposta();
        };
    }

    // ==================== HANDLERS ====================

    private String handleScheduleRehearsal(String memberPhone, ClaudeAction action) {
        if (!isAdmin(memberPhone)) {
            return "Só admin pode agendar ensaio, beleza? 😅";
        }
        ClaudeAction.ActionData dados = action.dados();
        String dataHora = dados != null ? dados.opcoesDatas() : "";
        String local = dados != null ? dados.local() : "A definir";
        Rehearsal rehearsal = rehearsalService.createScheduledRehearsal(dataHora, local);

        String presenceMessage = "🎸 *Ensaio marcado!*\n\n" +
                action.resposta() +
                "\n\n👉 *Você vai estar presente?*\n" +
                "Responde *SIM* ou *NÃO*!";

        // Detecta tipo de ensaio pela resposta da IA
        String respLower = action.resposta().toLowerCase();
        List<Member> recipients = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .filter(m -> !isProjecao(m)) // projeção nunca recebe msg de ensaio
                .filter(m -> filterByRehearsalType(m, respLower))
                .collect(Collectors.toList());

        evolutionClient.sendTextMessageSeries(recipients, presenceMessage);

        String tipoLog = respLower.contains("voz") ? "vozes" : respLower.contains("instrumental") ? "instrumental" : "geral";
        return "Pronto! Mandei pro pessoal do ensaio de " + tipoLog + " confirmar presença ✅ (" + recipients.size() + " membros)";
    }

    /**
     * Filtra membros por tipo de ensaio:
     * - "vozes" → só quem tem VOCAL no instrumento
     * - "instrumental" → só quem tem instrumento (não vocal, não projeção)
     * - "geral" (default) → todos (exceto projeção, já filtrado antes)
     */
    private boolean filterByRehearsalType(Member member, String respLower) {
        String instr = member.instrumento().toLowerCase();
        if (respLower.contains("voz") || respLower.contains("vocal")) {
            return instr.contains("vocal") || instr.contains("voz");
        }
        if (respLower.contains("instrumental")) {
            return !instr.contains("vocal") && !instr.contains("voz");
        }
        // Geral: todos (projeção já foi filtrado antes)
        return true;
    }

    private boolean isProjecao(Member member) {
        String instr = member.instrumento().toLowerCase();
        return instr.contains("proje") || instr.contains("projeção") || instr.contains("projecao");
    }

    private String handleShowRegistration(String memberPhone, ClaudeAction.ActionData dados) {
        if (!isAdmin(memberPhone)) {
            return "Só admin pode cadastrar show 🎸";
        }
        if (dados == null) {
            return "Preciso de data, horário e local do show 🤷";
        }
        return showService.requestShowRegistration(memberPhone, dados);
    }

    private String handleConfirmShow(String memberPhone) {
        if (!isAdmin(memberPhone)) {
            return "Só admin pode confirmar show 😅";
        }
        Show show = showService.confirmShowRegistration(memberPhone);
        if (show == null) {
            return "Não tem show pendente de confirmação 🤷";
        }
        return String.format("Show cadastrado! 🎉\n*%s* — %s às %s no %s",
                show.nome(), show.data(), show.horario(), show.local());
    }

    private String handleCancelShow(String memberPhone) {
        showService.cancelShowRegistration(memberPhone);
        return "Beleza, cadastro de show cancelado 👍";
    }

    private String handleOpenMemberVote(String memberPhone) {
        if (!isAdmin(memberPhone)) {
            return "Só admin pode abrir votação 🌟";
        }
        return memberOfMonthService.openVoting(memberPhone);
    }

    private String handleCloseMemberVote(String memberPhone) {
        if (!isAdmin(memberPhone)) {
            return "Só admin pode encerrar votação 🌟";
        }
        return memberOfMonthService.closeVoting(memberPhone);
    }

    private String handleMemberOfMonthVote(String memberPhone, ClaudeAction.ActionData dados, ClaudeAction action) {
        int voteNumber = dados != null && dados.voteNumber() != null
                ? dados.voteNumber()
                : parseVoteNumber(action.resposta());
        if (voteNumber <= 0) {
            return fallback(action.resposta(), "Manda o número do membro que você quer votar 🔢");
        }
        return memberOfMonthService.registerVote(memberPhone, voteNumber);
    }

    private String handleCompleteRehearsal(String memberPhone, ClaudeAction.ActionData dados) {
        if (!isAdmin(memberPhone)) {
            return "Só admin pode marcar ensaio como concluído 😅";
        }
        String rehearsalId = dados != null ? dados.rehearsalId() : null;
        return rehearsalCounterService.completeRehearsal(rehearsalId);
    }

    private String handleApproveMusic(String memberPhone) {
        if (!isAdmin(memberPhone)) {
            return "Só admin pode aprovar sugestões 🎵";
        }
        return musicSuggestionService.approveMusic(memberPhone);
    }

    private String handleRejectMusic(String memberPhone) {
        if (!isAdmin(memberPhone)) {
            return "Só admin pode rejeitar sugestões 🎵";
        }
        return musicSuggestionService.rejectMusic(memberPhone);
    }

    private void handleVote(String memberPhone, ClaudeAction.ActionData dados) {
        repository.findNextScheduledRehearsal().ifPresent(r -> {
            String dataOpcao = dados != null ? dados.dataOpcao() : "";
            rehearsalService.registerVote(r.id(), memberPhone, dataOpcao);
        });
    }

    private void handlePresence(String memberPhone, ClaudeAction.ActionData dados, boolean confirmado) {
        var opt = repository.findAllRehearsals().stream()
                .filter(r -> "AGENDADO".equalsIgnoreCase(r.status()))
                .findFirst();
        if (opt.isPresent()) {
            log.info("Registrando presença: {} -> {} no ensaio {}", memberPhone, confirmado ? "SIM" : "NAO", opt.get().id());
            rehearsalService.registerPresence(opt.get().id(), memberPhone, confirmado);
        } else {
            log.warn("Nenhum ensaio AGENDADO encontrado para registrar presença de {}", memberPhone);
        }
    }

    private String handlePresenceStatus(ClaudeAction.ActionData dados) {
        String rehearsalId = resolveRehearsalId(dados);
        if (rehearsalId == null) {
            return "Não achei ensaio agendado 🤷";
        }
        return rehearsalService.buildPresenceSummary(rehearsalId);
    }

    private String handleBroadcast(String memberPhone, ClaudeAction.ActionData dados, ClaudeAction action) {
        if (!isAdmin(memberPhone)) {
            return "Só admin pode mandar aviso geral 📢";
        }
        String mensagem = dados != null ? dados.mensagemBroadcast() : action.resposta();
        broadcastService.broadcastToAll(mensagem);
        return "Aviso enviado pra galera! 📢";
    }

    private String handleViewSetlist(ClaudeAction.ActionData dados, ClaudeAction action) {
        String rehearsalId = resolveRehearsalId(dados);
        if (rehearsalId == null) {
            return action.resposta();
        }
        return broadcastService.formatSetlist(rehearsalId);
    }

    private String handleUpdateSetlist(String memberPhone, ClaudeAction.ActionData dados, ClaudeAction action) {
        if (!isAdmin(memberPhone)) {
            return "Só admin pode editar a setlist 🎵";
        }
        String rehearsalId = resolveRehearsalId(dados);
        if (rehearsalId == null || dados == null || dados.musicas() == null) {
            return "Preciso saber qual ensaio e quais músicas 🤷";
        }
        broadcastService.updateSetlist(rehearsalId, dados.musicas());
        return "Setlist atualizada! 🎵\n\n" + broadcastService.formatSetlist(rehearsalId);
    }

    // ==================== HELPERS ====================

    private String normalizeAction(String acao) {
        if (acao == null) {
            return BotAction.RESPONDER;
        }
        return acao.toUpperCase().replace(" ", "_");
    }

    private String fallback(String primary, String secondary) {
        return primary != null && !primary.isBlank() ? primary : secondary;
    }

    private int parseVoteNumber(String text) {
        if (text == null) return -1;
        try {
            return Integer.parseInt(text.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String resolveRehearsalId(ClaudeAction.ActionData dados) {
        if (dados != null && dados.rehearsalId() != null && !dados.rehearsalId().isBlank()) {
            return dados.rehearsalId();
        }
        return repository.findNextScheduledRehearsal().map(Rehearsal::id).orElse(null);
    }

    private boolean isAdmin(String phone) {
        String normalized = PhoneUtils.normalize(phone);
        return Arrays.stream(properties.getAdminPhones().split(","))
                .map(String::trim)
                .map(PhoneUtils::normalize)
                .anyMatch(normalized::equals)
                || repository.findMemberByPhone(phone).map(Member::admin).orElse(false);
    }

    private void updateConversationState(String phone, String estado, String context) {
        ConversationState state = new ConversationState(
                phone, estado, "{\"rehearsalId\":\"" + context + "\"}", PhoneUtils.nowFormatted()
        );
        repository.saveConversationState(state);
    }
}
