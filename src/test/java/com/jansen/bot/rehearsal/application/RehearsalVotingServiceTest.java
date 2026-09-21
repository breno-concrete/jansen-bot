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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Fakes simples das portas, sem Mockito (quickstart.md § 3). */
class RehearsalVotingServiceTest {

    private static final String LIDER = "5511999990000";
    private static final String MEMBRO = "5511999991111";
    private static final String MEMBRO_2 = "5511999992222";
    // data-model.md: Voto.integranteId é o telefone normalizado
    private static final String MEMBRO_ID = PhoneUtils.normalize(MEMBRO);
    private static final Instant AGORA = Instant.parse("2026-09-20T10:00:00Z");

    private FakeRehearsalRepository repositorio;
    private FakeNotification notificacao;
    private FakeIntegrantes integrantes;
    private RehearsalVotingService service;

    @BeforeEach
    void setUp() {
        repositorio = new FakeRehearsalRepository();
        notificacao = new FakeNotification();
        integrantes = new FakeIntegrantes(LIDER, MEMBRO, MEMBRO_2);
        ClockPort relogio = () -> AGORA;
        LeaderPolicyPort politica = telefone -> Set.of(LIDER).contains(telefone);
        service = new RehearsalVotingService(repositorio, notificacao, relogio, politica, integrantes);
    }

    @Test
    @DisplayName("FR-001: criarEnsaio por quem não é líder lança NaoAutorizadoException e não persiste nem notifica")
    void criarEnsaio_solicitanteNaoLider_rejeitaSemPersistirNemNotificar() {
        assertThrows(NaoAutorizadoException.class,
                () -> service.criarEnsaio(MEMBRO, "2026-09-25 19:00", "Estúdio X"));

        assertTrue(repositorio.salvos.isEmpty());
        assertTrue(notificacao.paraIntegrante.isEmpty());
        assertTrue(notificacao.paraTodos.isEmpty());
        assertTrue(notificacao.paraLider.isEmpty());
    }

    @Test
    @DisplayName("FR-001/FR-003: criarEnsaio pela líder cria o Ensaio com votação aberta e o persiste")
    void criarEnsaio_lider_criaEnsaioComVotacaoAbertaEPersiste() {
        Ensaio criado = service.criarEnsaio(LIDER, "2026-09-25 19:00", "Estúdio X");

        assertEquals(Ensaio.Status.VOTACAO_ABERTA, criado.status());
        assertEquals("2026-09-25 19:00", criado.dataHora());
        assertEquals("Estúdio X", criado.local());
        assertEquals(AGORA, criado.criadoEm());
        assertEquals(1, repositorio.salvos.size());
        assertEquals(criado.id(), repositorio.salvos.get(0).id());
    }

    @Test
    @DisplayName("FR-003/FR-018: criarEnsaio notifica todos os integrantes elegíveis, exceto a líder que pediu")
    void criarEnsaio_lider_notificaTodosOsElegiveisMenosALider() {
        service.criarEnsaio(LIDER, "2026-09-25 19:00", "Estúdio X");

        assertEquals(1, notificacao.paraTodos.size());
        assertEquals(List.of(MEMBRO, MEMBRO_2), notificacao.paraTodos.get(0).telefones());
    }

    @Test
    @DisplayName("FR-003: a mensagem pede confirmação (sim/não) e cita a data e hora do ensaio")
    void criarEnsaio_mensagemPedeConfirmacaoDaDataHora() {
        service.criarEnsaio(LIDER, "2026-09-25 19:00", "Estúdio X");

        String mensagem = notificacao.paraTodos.get(0).mensagem().toLowerCase();
        assertTrue(mensagem.contains("2026-09-25 19:00"));
        assertTrue(mensagem.contains("sim"));
        assertTrue(mensagem.contains("não"));
    }

    @Test
    @DisplayName("FR-018: a líder é excluída mesmo quando o cadastro a traz com outra formatação de telefone")
    void criarEnsaio_excluiALiderComTelefoneFormatadoDiferente() {
        integrantes = new FakeIntegrantes("(11) 99999-0000", MEMBRO, MEMBRO_2);
        service = new RehearsalVotingService(repositorio, notificacao, () -> AGORA,
                telefone -> Set.of(LIDER).contains(telefone), integrantes);

        service.criarEnsaio(LIDER, "2026-09-25 19:00", "Estúdio X");

        assertEquals(List.of(MEMBRO, MEMBRO_2), notificacao.paraTodos.get(0).telefones());
    }

    @Test
    @DisplayName("FR-005/US1-2: registrarVoto com SIM grava o voto no Ensaio, persiste e confirma o 'sim' a quem votou")
    void registrarVoto_sim_gravaVotoEConfirmaAoIntegrante() {
        Ensaio ensaio = service.criarEnsaio(LIDER, "2026-09-25 19:00", "Estúdio X");

        service.registrarVoto(ensaio.id(), MEMBRO, Voto.Escolha.SIM);

        assertEquals(List.of(new Voto(MEMBRO_ID, ensaio.id(), Voto.Escolha.SIM, AGORA)), ultimoSalvo().votos());
        assertEquals(1, notificacao.paraIntegrante.size());
        assertTrue(notificacao.paraIntegrante.get(0).startsWith(MEMBRO));
        assertTrue(notificacao.paraIntegrante.get(0).toLowerCase().contains("sim"));
    }

    @Test
    @DisplayName("FR-005/US1-3: registrarVoto com NAO grava o voto no Ensaio, persiste e confirma o 'não' a quem votou")
    void registrarVoto_nao_gravaVotoEConfirmaAoIntegrante() {
        Ensaio ensaio = service.criarEnsaio(LIDER, "2026-09-25 19:00", "Estúdio X");

        service.registrarVoto(ensaio.id(), MEMBRO, Voto.Escolha.NAO);

        assertEquals(List.of(new Voto(MEMBRO_ID, ensaio.id(), Voto.Escolha.NAO, AGORA)), ultimoSalvo().votos());
        assertEquals(1, notificacao.paraIntegrante.size());
        assertTrue(notificacao.paraIntegrante.get(0).startsWith(MEMBRO));
        assertTrue(notificacao.paraIntegrante.get(0).toLowerCase().contains("não"));
    }

    @Test
    @DisplayName("T022: registrarVoto de um ensaio inexistente lança EnsaioNaoEncontradoException, sem salvar nem notificar")
    void registrarVoto_ensaioInexistente_lancaExcecaoSemSalvarNemNotificar() {
        assertThrows(EnsaioNaoEncontradoException.class,
                () -> service.registrarVoto("nao-existe", MEMBRO, Voto.Escolha.SIM));

        assertTrue(repositorio.salvos.isEmpty());
        assertTrue(notificacao.paraIntegrante.isEmpty());
    }

    @Test
    @DisplayName("T022: o mesmo integrante votando de novo substitui o voto anterior e a nova resposta também é confirmada")
    void registrarVoto_repetido_substituiOVotoAnterior() {
        Ensaio ensaio = service.criarEnsaio(LIDER, "2026-09-25 19:00", "Estúdio X");

        service.registrarVoto(ensaio.id(), MEMBRO, Voto.Escolha.SIM);
        service.registrarVoto(ensaio.id(), MEMBRO, Voto.Escolha.NAO);

        assertEquals(List.of(new Voto(MEMBRO_ID, ensaio.id(), Voto.Escolha.NAO, AGORA)), ultimoSalvo().votos());
        assertEquals(2, notificacao.paraIntegrante.size());
    }

    @Test
    @DisplayName("data-model.md: o voto é gravado com o telefone normalizado, mesmo que chegue formatado")
    void registrarVoto_normalizaOTelefoneDoIntegrante() {
        Ensaio ensaio = service.criarEnsaio(LIDER, "2026-09-25 19:00", "Estúdio X");

        service.registrarVoto(ensaio.id(), "(11) 99999-1111", Voto.Escolha.SIM);

        assertEquals(MEMBRO_ID, ultimoSalvo().votos().get(0).integranteId());
    }

    private Ensaio ultimoSalvo() {
        return repositorio.salvos.get(repositorio.salvos.size() - 1);
    }

    // ---- fakes ----

    static class FakeIntegrantes implements IntegranteRepositoryPort {
        private final List<String> telefones;

        FakeIntegrantes(String... telefones) {
            this.telefones = List.of(telefones);
        }

        @Override
        public List<String> buscarTelefonesElegiveis() {
            return telefones;
        }
    }

    static class FakeRehearsalRepository implements RehearsalRepositoryPort {
        final List<Ensaio> salvos = new ArrayList<>();

        @Override
        public void salvar(Ensaio ensaio) {
            salvos.add(ensaio);
        }

        @Override
        public Optional<Ensaio> buscarPorId(String ensaioId) {
            return salvos.stream().filter(e -> e.id().equals(ensaioId)).findFirst();
        }

        @Override
        public List<Ensaio> buscarComVotacaoAberta() {
            return salvos.stream().filter(e -> e.status() == Ensaio.Status.VOTACAO_ABERTA).toList();
        }
    }

    record Notificacao(List<String> telefones, String mensagem) {}

    static class FakeNotification implements NotificationPort {
        final List<String> paraIntegrante = new ArrayList<>();
        final List<Notificacao> paraTodos = new ArrayList<>();
        final List<String> paraLider = new ArrayList<>();

        @Override
        public void notificarIntegrante(String telefone, String mensagem) {
            paraIntegrante.add(telefone + ": " + mensagem);
        }

        @Override
        public void notificarTodos(List<String> telefones, String mensagem) {
            paraTodos.add(new Notificacao(telefones, mensagem));
        }

        @Override
        public void notificarLider(String mensagem) {
            paraLider.add(mensagem);
        }
    }
}
