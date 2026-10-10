package com.jansen.bot.rehearsal.application;

import com.jansen.bot.exception.EnsaioNaoEncontradoException;
import com.jansen.bot.exception.NaoAutorizadoException;
import com.jansen.bot.rehearsal.domain.Ensaio;
import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.rehearsal.domain.Voto;
import com.jansen.bot.rehearsal.ports.ClockPort;
import com.jansen.bot.rehearsal.ports.IntegranteRepositoryPort;
import com.jansen.bot.rehearsal.ports.LeaderPolicyPort;
import com.jansen.bot.rehearsal.ports.NotificationPort;
import com.jansen.bot.rehearsal.ports.RehearsalRepositoryPort;
import com.jansen.bot.util.PhoneUtils;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Casos de uso da votação de ensaio (contracts/rehearsal-ports.md). Sem anotações Spring: o
 * bean é registrado quando o {@code ActionDispatcher} passar a usá-lo (T023).
 */
public class RehearsalVotingService {

    private final RehearsalRepositoryPort repositorio;
    private final NotificationPort notificacao;
    private final ClockPort relogio;
    private final LeaderPolicyPort politicaDeLider;
    private final IntegranteRepositoryPort integrantes;

    public RehearsalVotingService(RehearsalRepositoryPort repositorio, NotificationPort notificacao,
                                  ClockPort relogio, LeaderPolicyPort politicaDeLider,
                                  IntegranteRepositoryPort integrantes) {
        this.repositorio = repositorio;
        this.notificacao = notificacao;
        this.relogio = relogio;
        this.politicaDeLider = politicaDeLider;
        this.integrantes = integrantes;
    }

    /** FR-001, FR-003, FR-018, FR-019. */
    public Ensaio criarEnsaio(String telefoneSolicitante, TipoEnsaio tipo, String dataHora, String local) {
        if (!politicaDeLider.isLider(telefoneSolicitante)) {
            throw new NaoAutorizadoException("Só a líder pode criar um ensaio.");
        }

        Ensaio ensaio = Ensaio.criar(tipo, dataHora, local, relogio.agora());
        repositorio.salvar(ensaio);

        String solicitante = PhoneUtils.normalize(telefoneSolicitante);
        List<String> destinatarios = integrantes.buscarTelefonesElegiveis(tipo).stream()
                .filter(telefone -> !PhoneUtils.normalize(telefone).equals(solicitante))
                .toList();
        notificacao.notificarTodos(destinatarios, mensagemDePedidoDeConfirmacao(ensaio));

        return ensaio;
    }

    /** FR-005: grava o voto no Ensaio, persiste e confirma a resposta a quem votou. */
    public void registrarVoto(String ensaioId, String telefoneIntegrante, Voto.Escolha escolha) {
        Ensaio ensaio = repositorio.buscarPorId(ensaioId)
                .orElseThrow(() -> new EnsaioNaoEncontradoException(ensaioId));

        ensaio.registrarVoto(PhoneUtils.normalize(telefoneIntegrante), escolha, relogio.agora());
        repositorio.salvar(ensaio);

        notificacao.notificarIntegrante(telefoneIntegrante, mensagemDeConfirmacaoDoVoto(escolha));
    }

    public void registrarVoto(String telefoneIntegrante, Voto.Escolha escolha, TipoEnsaio tipoInformado){

        // FR-020: a líder nunca vota (a porta recebe o telefone como veio)
        if (politicaDeLider.isLider(telefoneIntegrante)) {
            return;
        }

        String telefone = PhoneUtils.normalize(telefoneIntegrante);

        // o cadastro vem do Sheets (quota): consulta uma vez por tipo, não uma vez por ensaio aberto
        Map<TipoEnsaio, Boolean> convocadoPorTipo = new EnumMap<>(TipoEnsaio.class);
        List<Ensaio> pendentes = repositorio.buscarComVotacaoAberta().stream()
                .filter(ensaio -> convocadoPorTipo.computeIfAbsent(ensaio.tipo(), tipo -> isConvocado(telefone, tipo)))
                .toList();

        // 0 pendentes: FR-020, ignora em silêncio.
        if (pendentes.isEmpty()) {
            return;
        }
        // 2+ pendentes: FR-021/FR-022 (desempate pelo tipoInformado)
        if (pendentes.size() >= 2) {
            List<Ensaio> filtrados = pendentes.stream()
                    .filter(ensaio -> ensaio.tipo() == tipoInformado)
                    .toList();
            if (filtrados.isEmpty()) {
                notificacao.notificarIntegrante(telefoneIntegrante, mensagemPedindoOTipo(pendentes));
                return;
            } else {
                pendentes = filtrados;
            }
        }

        Ensaio ensaio = pendentes.get(0);
        ensaio.registrarVoto(telefone, escolha, relogio.agora());
        repositorio.salvar(ensaio);

        notificacao.notificarIntegrante(telefoneIntegrante, mensagemDeConfirmacaoDoVoto(escolha));
    }

    /** FR-020: convocado para o tipo do ensaio. */
    private boolean isConvocado(String telefoneNormalizado, TipoEnsaio tipo) {
        return integrantes.buscarTelefonesElegiveis(tipo).stream()
                .map(PhoneUtils::normalize)
                .anyMatch(telefoneNormalizado::equals);

    }

    private String mensagemDeConfirmacaoDoVoto(Voto.Escolha escolha) {
        String resposta = escolha == Voto.Escolha.SIM ? "SIM" : "NÃO";
        return "Sua resposta *" + resposta + "* foi registrada.";
    }

    /** P-030: pede o tipo quando há mais de um ensaio pendente e o integrante não disse (ou errou) qual. */
    private String mensagemPedindoOTipo(List<Ensaio> pendentes) {
        String tipos = pendentes.stream()
                .map(ensaio -> ensaio.tipo().name().toLowerCase())
                .collect(Collectors.joining(", "));
        return "Você tem mais de um ensaio aberto. Responda *sim* ou *não* dizendo o tipo. "
                + "Exemplo: *sim, vocal*.\n"
                + "Ensaios abertos: " + tipos + ".";
    }

    private String mensagemDePedidoDeConfirmacao(Ensaio ensaio) {
        String tipo = ensaio.tipo().name().toLowerCase();
        return "*Ensaio " + tipo + " marcado*\n\n"
                + "Data e hora: " + ensaio.dataHora() + "\n"
                + "Local: " + ensaio.local() + "\n\n"
                + "Você vai estar presente?\n"
                + "Responda *SIM* ou *NÃO* e o tipo do ensaio. Exemplo: *sim, " + tipo + "*.";
    }
}
