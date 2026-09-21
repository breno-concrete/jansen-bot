package com.jansen.bot.rehearsal.application;

import com.jansen.bot.exception.EnsaioNaoEncontradoException;
import com.jansen.bot.exception.NaoAutorizadoException;
import com.jansen.bot.rehearsal.domain.Ensaio;
import com.jansen.bot.rehearsal.domain.Voto;
import com.jansen.bot.rehearsal.ports.ClockPort;
import com.jansen.bot.rehearsal.ports.IntegranteRepositoryPort;
import com.jansen.bot.rehearsal.ports.LeaderPolicyPort;
import com.jansen.bot.rehearsal.ports.NotificationPort;
import com.jansen.bot.rehearsal.ports.RehearsalRepositoryPort;
import com.jansen.bot.util.PhoneUtils;

import java.util.List;

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

    /** FR-001, FR-003, FR-018. */
    public Ensaio criarEnsaio(String telefoneSolicitante, String dataHora, String local) {
        if (!politicaDeLider.isLider(telefoneSolicitante)) {
            throw new NaoAutorizadoException("Só a líder pode criar um ensaio.");
        }

        Ensaio ensaio = Ensaio.criar(dataHora, local, relogio.agora());
        repositorio.salvar(ensaio);

        String solicitante = PhoneUtils.normalize(telefoneSolicitante);
        List<String> destinatarios = integrantes.buscarTelefonesElegiveis().stream()
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

    private String mensagemDeConfirmacaoDoVoto(Voto.Escolha escolha) {
        String resposta = escolha == Voto.Escolha.SIM ? "SIM" : "NÃO";
        return "Sua resposta *" + resposta + "* foi registrada.";
    }

    private String mensagemDePedidoDeConfirmacao(Ensaio ensaio) {
        return "*Ensaio marcado*\n\n"
                + "Data e hora: " + ensaio.dataHora() + "\n"
                + "Local: " + ensaio.local() + "\n\n"
                + "Você vai estar presente?\n"
                + "Responda *SIM* ou *NÃO*.";
    }
}
