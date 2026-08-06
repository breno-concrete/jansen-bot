package com.jansen.bot.service;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.*;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.util.PhoneUtils;
import com.jansen.bot.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Votação para membro do mês: abertura, registro e encerramento.
 */
@Service
public class MemberOfMonthService {

    private static final Logger log = LoggerFactory.getLogger(MemberOfMonthService.class);

    private final GoogleSheetsRepository repository;
    private final EvolutionClient evolutionClient;
    private final AppProperties properties;

    public MemberOfMonthService(GoogleSheetsRepository repository,
                                EvolutionClient evolutionClient,
                                AppProperties properties) {
        this.repository = repository;
        this.evolutionClient = evolutionClient;
        this.properties = properties;
    }

    /**
     * Admin abre votação — envia lista numerada para cada membro em privado.
     */
    public String openVoting(String adminPhone) {
        if (repository.isMemberOfMonthVotingOpen()) {
            return "Já tem uma votação aberta! Encerra a atual primeiro 😅";
        }

        int mes = DateUtils.todayBrazil().getMonthValue();
        int ano = DateUtils.todayBrazil().getYear();

        repository.saveConversationState(new ConversationState(
                adminPhone, ConversationStates.VOTACAO_MEMBRO_ABERTA,
                String.format("{\"mes\":%d,\"ano\":%d}", mes, ano),
                PhoneUtils.nowFormatted()
        ));

        List<Member> members = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());

        StringBuilder list = new StringBuilder();
        for (int i = 0; i < members.size(); i++) {
            list.append(i + 1).append(". ").append(members.get(i).nome()).append("\n");
        }

        String voteMessage = "🌟 *Votação — Membro do mês*\n\n" +
                "Vote respondendo com o *número* do membro:\n\n" + list;

        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            evolutionClient.sendTextMessage(member.telefone(), voteMessage);
            repository.saveConversationState(new ConversationState(
                    member.telefone(), ConversationStates.VOTACAO_MEMBRO_MES,
                    String.format("{\"mes\":%d,\"ano\":%d}", mes, ano),
                    PhoneUtils.nowFormatted()
            ));
            if (i < members.size() - 1) {
                evolutionClient.sleepDelay(15000);
            }
        }

        log.info("Votação membro do mês aberta para {}/{}", mes, ano);
        return "Votação aberta! Mandei a lista pro pessoal votar em privado 🗳️";
    }

    /**
     * Registra voto de um membro pelo número escolhido.
     */
    public String registerVote(String voterPhone, int voteNumber) {
        if (!repository.isMemberOfMonthVotingOpen()) {
            return "Não tem votação aberta agora 🤷";
        }

        int mes = DateUtils.todayBrazil().getMonthValue();
        int ano = DateUtils.todayBrazil().getYear();

        if (repository.hasVotedInMonth(voterPhone, mes, ano)) {
            return "Você já votou nesta votação! 🙂";
        }

        List<Member> members = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());

        if (voteNumber < 1 || voteNumber > members.size()) {
            return "Número inválido! Escolhe entre 1 e " + members.size();
        }

        Member voted = members.get(voteNumber - 1);
        repository.saveMemberOfMonthVote(new MemberOfMonthVote(
                PhoneUtils.generateId(), voterPhone, voted.telefone(), mes, ano
        ));

        return "Voto registrado para " + voted.nome() + "! 👍";
    }

    /**
     * Admin encerra votação e anuncia vencedor no grupo.
     */
    public String closeVoting(String adminPhone) {
        if (!repository.isMemberOfMonthVotingOpen()) {
            return "Não tem votação aberta 🤷";
        }

        int mes = DateUtils.todayBrazil().getMonthValue();
        int ano = DateUtils.todayBrazil().getYear();
        List<MemberOfMonthVote> votes = repository.findVotesByMonth(mes, ano);

        if (votes.isEmpty()) {
            repository.clearConversationState(adminPhone);
            return "Ninguém votou ainda 😅";
        }

        Map<String, Long> tally = votes.stream()
                .collect(Collectors.groupingBy(MemberOfMonthVote::votedPhone, Collectors.counting()));

        long maxVotes = tally.values().stream().max(Long::compare).orElse(0L);
        List<String> winners = tally.entrySet().stream()
                .filter(e -> e.getValue() == maxVotes)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (winners.size() > 1) {
            String names = winners.stream()
                    .map(p -> repository.findMemberByPhone(p).map(Member::nome).orElse(p))
                    .collect(Collectors.joining(", "));
            evolutionClient.sendTextMessage(properties.getPrimaryAdminPhone(),
                    "⚠️ Empate na votação do membro do mês: " + names +
                            " (" + maxVotes + " votos cada). Desempate manualmente!");
            return "Deu empate! Te avisei em privado pra desempatar 🤝";
        }

        String winnerPhone = winners.get(0);
        String winnerName = repository.findMemberByPhone(winnerPhone)
                .map(Member::nome).orElse("Membro");

        String monthName = com.jansen.bot.util.DateUtils.capitalize(
                com.jansen.bot.util.DateUtils.formatMonthName(mes, ano));

        String announcement = String.format(
                "🌟 *Membro do mês — %s*\n\nCom %d votos... *%s*! 🎉\n\nObrigado a todos que votaram!",
                monthName, maxVotes, winnerName
        );

        List<Member> activeMembers = repository.findAllMembers().stream()
                .filter(Member::ativo)
                .collect(Collectors.toList());
        evolutionClient.sendTextMessageSeries(activeMembers, announcement);

        // Limpa estados de votação
        repository.clearConversationState(adminPhone);
        repository.findAllMembers().forEach(m -> repository.clearConversationState(m.telefone()));

        log.info("Votação encerrada. Vencedor: {}", winnerName);
        return "Votação encerrada! " + winnerName + " venceu com " + maxVotes + " votos 🎉";
    }
}
