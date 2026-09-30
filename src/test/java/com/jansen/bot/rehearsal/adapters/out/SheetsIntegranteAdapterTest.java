package com.jansen.bot.rehearsal.adapters.out;

import com.jansen.bot.model.Member;
import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.repository.GoogleSheetsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SheetsIntegranteAdapterTest {

    @Mock
    private GoogleSheetsRepository repository;

    private Member membro(String telefone, String instrumento, boolean ativo) {
        return new Member("id-" + telefone, "Nome", telefone, instrumento, ativo, false);
    }

    @Test
    @DisplayName("FR-003/FR-018: devolve os telefones dos integrantes ativos, sem projeção")
    void buscarTelefonesElegiveis_soAtivosSemProjecao() {
        when(repository.findAllMembers()).thenReturn(List.of(
                membro("5511111111111", "Guitarra", true),
                membro("5522222222222", "Vocal", true),
                membro("5533333333333", "Projeção", true),
                membro("5544444444444", "Projecao", true),
                membro("5555555555555", "Baixo", false)));

        List<String> telefones = new SheetsIntegranteAdapter(repository).buscarTelefonesElegiveis(TipoEnsaio.GERAL);

        assertEquals(List.of("5511111111111", "5522222222222"), telefones);
    }

    private void cadastroDeTodosOsInstrumentos() {
        when(repository.findAllMembers()).thenReturn(List.of(
                membro("5511111111111", "Guitarra", true),
                membro("5522222222222", "Vocal", true),
                membro("5533333333333", "Voz", true),
                membro("5544444444444", "Projeção", true),
                membro("5555555555555", "Baixo", false),
                membro("5566666666666", "Vocal", false)));
    }

    @Test
    @DisplayName("FR-019: ensaio VOCAL convoca só ativos com instrumento vocal/voz")
    void buscarTelefonesElegiveis_vocal_soVocalistas() {
        cadastroDeTodosOsInstrumentos();

        List<String> telefones = new SheetsIntegranteAdapter(repository).buscarTelefonesElegiveis(TipoEnsaio.VOCAL);

        assertEquals(List.of("5522222222222", "5533333333333"), telefones);
    }

    @Test
    @DisplayName("FR-019: ensaio INSTRUMENTAL convoca os ativos que não são vocal nem projeção")
    void buscarTelefonesElegiveis_instrumental_soInstrumentistas() {
        cadastroDeTodosOsInstrumentos();

        List<String> telefones = new SheetsIntegranteAdapter(repository)
                .buscarTelefonesElegiveis(TipoEnsaio.INSTRUMENTAL);

        assertEquals(List.of("5511111111111"), telefones);
    }

    @Test
    @DisplayName("FR-019: ensaio GERAL convoca todos os ativos, exceto projeção")
    void buscarTelefonesElegiveis_geral_todosMenosProjecao() {
        cadastroDeTodosOsInstrumentos();

        List<String> telefones = new SheetsIntegranteAdapter(repository).buscarTelefonesElegiveis(TipoEnsaio.GERAL);

        assertEquals(List.of("5511111111111", "5522222222222", "5533333333333"), telefones);
    }
}
