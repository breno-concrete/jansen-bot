package com.jansen.bot.service;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.model.Member;
import com.jansen.bot.model.Rehearsal;
import com.jansen.bot.model.ResponseRecord;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.util.PhoneUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Serviço de ensaios: agendamento, votação e confirmação de presença.
 */
@Service
public class RehearsalService {

    private static final Logger log = LoggerFactory.getLogger(RehearsalService.class);

    private final GoogleSheetsRepository repository;
    private final EvolutionClient evolutionClient;

    public RehearsalService(GoogleSheetsRepository repository, EvolutionClient evolutionClient) {
        this.repository = repository;
        this.evolutionClient = evolutionClient;
    }

    /**
     * Cria ensaio em votação com opções de datas propostas.
     */
    public Rehearsal createVotingRehearsal(String opcoesDatas, String local) {
        Rehearsal rehearsal = new Rehearsal(
                PhoneUtils.generateId(),
                "",
                local != null ? local : "A definir",
                "VOTACAO",
                opcoesDatas,
                "",
                false
        );
        repository.saveRehearsal(rehearsal);
        log.info("Ensaio em votação criado: {}", rehearsal.id());
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

        for (Member member : members) {
            evolutionClient.sendTextMessage(member.telefone(), message);
        }

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
