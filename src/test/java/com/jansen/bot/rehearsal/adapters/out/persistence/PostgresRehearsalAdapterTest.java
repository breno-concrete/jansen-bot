package com.jansen.bot.rehearsal.adapters.out.persistence;

import com.jansen.bot.rehearsal.domain.Ensaio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Teste de integração do adapter de persistência (T013; contracts/rehearsal-ports.md), contra
 * um Postgres real em container, rodando a migration Flyway. Exige Docker em execução.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@Import(PostgresRehearsalAdapter.class)
class PostgresRehearsalAdapterTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    private static final Instant CRIADO_EM = Instant.parse("2026-09-20T10:00:00Z");

    @Autowired
    private PostgresRehearsalAdapter adapter;

    @Test
    @DisplayName("salvar + buscarPorId devolvem o Ensaio com todos os campos, incluindo o histórico de remarcações")
    void salvarEBuscarPorId_preservaTodosOsCampos() {
        Ensaio salvo = Ensaio.reconstituir(
                "e1", "2026-09-25 19:00", "Estúdio X", CRIADO_EM, CRIADO_EM.plusSeconds(43_200),
                Ensaio.Status.ENCERRADA, Ensaio.DecisaoFinal.CONFIRMADO,
                List.of(new Ensaio.Remarcacao("2026-09-26 20:00", Instant.parse("2026-09-20T11:00:00Z")),
                        new Ensaio.Remarcacao("2026-09-27 20:00", Instant.parse("2026-09-20T12:00:00Z"))));

        adapter.salvar(salvo);
        Optional<Ensaio> lido = adapter.buscarPorId("e1");

        assertTrue(lido.isPresent());
        Ensaio e = lido.get();
        assertEquals("e1", e.id());
        assertEquals("2026-09-25 19:00", e.dataHora());
        assertEquals("Estúdio X", e.local());
        assertEquals(CRIADO_EM, e.criadoEm());
        assertEquals(CRIADO_EM.plusSeconds(43_200), e.prazoVotacaoEm());
        assertEquals(Ensaio.Status.ENCERRADA, e.status());
        assertEquals(Ensaio.DecisaoFinal.CONFIRMADO, e.decisaoFinal());
        assertEquals(salvo.historicoRemarcacoes(), e.historicoRemarcacoes());
    }

    @Test
    @DisplayName("buscarPorId de um id inexistente devolve Optional vazio")
    void buscarPorId_inexistente_devolveVazio() {
        assertTrue(adapter.buscarPorId("nao-existe").isEmpty());
    }

    @Test
    @DisplayName("buscarComVotacaoAberta devolve só os ensaios com status VOTACAO_ABERTA")
    void buscarComVotacaoAberta_devolveSomenteAbertos() {
        adapter.salvar(Ensaio.criar("2026-09-25 19:00", "A", CRIADO_EM));
        Ensaio encerrado = Ensaio.reconstituir(
                "encerrado", "2026-09-26 19:00", "B", CRIADO_EM, CRIADO_EM.plusSeconds(43_200),
                Ensaio.Status.ENCERRADA, Ensaio.DecisaoFinal.PENDENTE, List.of());
        adapter.salvar(encerrado);

        List<Ensaio> abertos = adapter.buscarComVotacaoAberta();

        assertEquals(1, abertos.size());
        assertEquals("A", abertos.get(0).local());
        assertEquals(Ensaio.Status.VOTACAO_ABERTA, abertos.get(0).status());
    }

    @Test
    @DisplayName("salvar um Ensaio com id já existente atualiza o registro, sem duplicar")
    void salvar_idExistente_atualizaSemDuplicar() {
        adapter.salvar(Ensaio.reconstituir(
                "e2", "2026-09-25 19:00", "A", CRIADO_EM, CRIADO_EM.plusSeconds(43_200),
                Ensaio.Status.VOTACAO_ABERTA, Ensaio.DecisaoFinal.PENDENTE, List.of()));

        adapter.salvar(Ensaio.reconstituir(
                "e2", "2026-09-25 19:00", "A", CRIADO_EM, CRIADO_EM.plusSeconds(43_200),
                Ensaio.Status.ENCERRADA, Ensaio.DecisaoFinal.PENDENTE, List.of()));

        assertEquals(Ensaio.Status.ENCERRADA, adapter.buscarPorId("e2").orElseThrow().status());
        assertTrue(adapter.buscarComVotacaoAberta().isEmpty());
    }
}
