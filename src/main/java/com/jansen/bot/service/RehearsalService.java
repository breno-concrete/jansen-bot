package com.jansen.bot.service;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.Member;
import com.jansen.bot.model.Rehearsal;
import com.jansen.bot.model.ResponseRecord;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.util.PhoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço de ensaios: agendamento, votação e confirmação de presença.
 */
@Service
public class RehearsalService {

    private static final Logger log = LoggerFactory.getLogger(RehearsalService.class);

    private final GoogleSheetsRepository repository;
    private final EvolutionClient evolutionClient;
    private final AppProperties properties;

    public RehearsalService(GoogleSheetsRepository repository, EvolutionClient evolutionClient,
                            AppProperties properties) {
        this.repository = repository;
        this.evolutionClient = evolutionClient;
        this.properties = properties;
    }

    /**
     * Cria ensaio agendado pelo líder. Membros confirmam presença com SIM/NÃO.
     */
    public Rehearsal createScheduledRehearsal(String dataHora, String local) {
        Rehearsal rehearsal = new Rehearsal(
                PhoneUtils.generateId(),
                dataHora != null ? dataHora : "",
                local != null ? local : "A definir",
                "AGENDADO",
                "",
                "",
                false
        );
        repository.saveRehearsal(rehearsal);
        log.info("Ensaio agendado criado: {} - {} em {}", rehearsal.id(), dataHora, local);
        return rehearsal;
    }

    /**
     * Registra voto de um membro em uma opção de data.
     */
    public void registerVote(String rehearsalId, String memberPhone, String dataOpcao) {
        ResponseRecord record = new ResponseRecord(
                PhoneUtils.generateId(),
                rehearsalId,
                memberPhone,
                "VOTO",
                dataOpcao,
                PhoneUtils.nowFormatted()
        );
        repository.saveResponse(record);
    }

    /**
     * Confirma ou nega presença no ensaio agendado.
     * Após registrar, verifica se todos já responderam e notifica o admin.
     */
    public void registerPresence(String rehearsalId, String memberPhone, boolean confirmado) {
        ResponseRecord record = new ResponseRecord(
                PhoneUtils.generateId(),
                rehearsalId,
                memberPhone,
                "CONFIRMACAO",
                confirmado ? "SIM" : "NAO",
                PhoneUtils.nowFormatted()
        );
        repository.saveResponse(record);

        // Verifica se todos os membros ativos já responderam
        checkAllResponded(rehearsalId);
    }

    /**
     * Verifica se todos os membros ativos já confirmaram/negaram presença.
     * Se sim, envia resumo automático para o admin.
     */
    private void checkAllResponded(String rehearsalId) {
        // Exclui projeção — eles nunca recebem mensagem de ensaio
        List<Member> eligibleMembers = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .filter(m -> {
                    String instr = m.instrumento().toLowerCase();
                    return !instr.contains("proje") && !instr.contains("projeção") && !instr.contains("projecao");
                })
                .collect(Collectors.toList());

        Set<String> respondedPhones = repository.findResponsesByRehearsal(rehearsalId).stream()
                .filter(r -> "CONFIRMACAO".equals(r.tipo()))
                .map(r -> PhoneUtils.normalize(r.memberPhone()))
                .collect(Collectors.toSet());

        long totalEligible = eligibleMembers.size();
        long totalResponded = eligibleMembers.stream()
                .filter(m -> respondedPhones.contains(PhoneUtils.normalize(m.telefone())))
                .count();

        if (totalResponded >= totalEligible && totalEligible > 0) {
            log.info("Todos os {} membros responderam sobre o ensaio {}", totalEligible, rehearsalId);
            String summary = buildPresenceSummary(rehearsalId);
            String adminMessage = "✅ *Todos responderam!*\n\n" + summary;
            evolutionClient.sendTextMessage(properties.getPrimaryAdminPhone(), adminMessage);
        }
    }

    /**
     * Finaliza votação e agenda ensaio com data vencedora.
     */
    public void finalizeVoting(String rehearsalId, String dataVencedora) {
        repository.findRehearsalById(rehearsalId).ifPresent(r -> {
            Rehearsal updated = new Rehearsal(
                    r.id(), dataVencedora, r.local(), "AGENDADO",
                    r.opcoesVoto(), dataVencedora, false
            );
            repository.updateRehearsal(updated);
        });
    }

    /**
     * Monta resumo de quem confirmou/negou presença.
     */
    public String buildPresenceSummary(String rehearsalId) {
        List<ResponseRecord> responses = repository.findResponsesByRehearsal(rehearsalId).stream()
                .filter(r -> "CONFIRMACAO".equals(r.tipo()))
                .collect(Collectors.toList());

        List<Member> members = repository.findAllMembers();

        StringBuilder sb = new StringBuilder("📋 *Presença no ensaio:*\n\n");

        sb.append("✅ *Confirmados:*\n");
        appendMemberList(sb, responses, members, "SIM");

        sb.append("\n❌ *Não vão:*\n");
        appendMemberList(sb, responses, members, "NAO");

        sb.append("\n⏳ *Sem resposta:*\n");
        List<String> respondedPhones = responses.stream()
                .map(r -> PhoneUtils.normalize(r.memberPhone()))
                .collect(Collectors.toList());

        members.stream()
                .filter(Member::ativo)
                .filter(m -> !respondedPhones.contains(PhoneUtils.normalize(m.telefone())))
                .forEach(m -> sb.append("  • ").append(m.nome()).append("\n"));

        return sb.toString();
    }

    /**
     * Envia lembrete 24h antes para quem confirmou e quem não respondeu.
     */
    public void sendReminders(Rehearsal rehearsal) {
        List<Member> members = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());

        String message = String.format(
                "🎸 *Lembrete de ensaio!*\n\n" +
                "Amanhã tem ensaio!\n📅 %s\n📍 %s\n\n" +
                "Confirma presença respondendo *sim* ou *não*!",
                rehearsal.dataHora(), rehearsal.local()
        );

        evolutionClient.sendTextMessageSeries(members, message);

        Rehearsal updated = new Rehearsal(
                rehearsal.id(), rehearsal.dataHora(), rehearsal.local(),
                rehearsal.status(), rehearsal.opcoesVoto(), rehearsal.vencedor(), true
        );
        repository.updateRehearsal(updated);
        log.info("Lembretes enviados para ensaio {}", rehearsal.id());
    }

    private void appendMemberList(StringBuilder sb, List<ResponseRecord> responses,
                                   List<Member> members, String valor) {
        responses.stream()
                .filter(r -> valor.equals(r.valor()))
                .forEach(r -> {
                    String nome = members.stream()
                            .filter(m -> PhoneUtils.normalize(m.telefone())
                                    .equals(PhoneUtils.normalize(r.memberPhone())))
                            .map(Member::nome)
                            .findFirst()
                            .orElse(r.memberPhone());
                    sb.append("  • ").append(nome).append("\n");
                });
    }
}
