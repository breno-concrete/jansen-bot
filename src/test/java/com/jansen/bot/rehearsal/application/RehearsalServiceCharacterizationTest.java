package com.jansen.bot.rehearsal.application;

import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.Member;
import com.jansen.bot.model.ResponseRecord;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.service.RehearsalService;
import com.jansen.bot.util.PhoneUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RehearsalServiceCharacterizationTest {

    @Mock
    private GoogleSheetsRepository repository;

    @Mock
    private EvolutionClient evolutionClient;

    @Mock
    private AppProperties properties;

    @InjectMocks
    private RehearsalService rehearsalService;

    @Test
    void registerPresence_confirmado_salvaResponseComTipoConfirmacaoEValorSim() {
        when(repository.findAllMembers()).thenReturn(List.of());
        when(repository.findResponsesByRehearsal("ens-1")).thenReturn(List.of());

        rehearsalService.registerPresence("ens-1", "5511999998888", true);

        ArgumentCaptor<ResponseRecord> captor = ArgumentCaptor.forClass(ResponseRecord.class);
        verify(repository).saveResponse(captor.capture());
        ResponseRecord saved = captor.getValue();

        assertThat(saved.rehearsalId()).isEqualTo("ens-1");
        assertThat(saved.memberPhone()).isEqualTo("5511999998888");
        assertThat(saved.tipo()).isEqualTo("CONFIRMACAO");
        assertThat(saved.valor()).isEqualTo("SIM");
        assertThat(saved.id()).isNotBlank();
        assertThat(saved.timestamp()).isNotBlank();
    }

    @Test
    void registerPresence_negado_salvaResponseComValorNao() {
        when(repository.findAllMembers()).thenReturn(List.of());
        when(repository.findResponsesByRehearsal("ens-1")).thenReturn(List.of());

        rehearsalService.registerPresence("ens-1", "5511999998888", false);

        ArgumentCaptor<ResponseRecord> captor = ArgumentCaptor.forClass(ResponseRecord.class);
        verify(repository).saveResponse(captor.capture());
        assertThat(captor.getValue().valor()).isEqualTo("NAO");
    }

    @Test
    void registerPresence_todosElegiveisResponderam_enviaResumoParaAdmin() {
        Member m1 = member("5511900000001", "Guitarra", true);
        Member m2 = member("5511900000002", "Baixo", true);
        when(repository.findAllMembers()).thenReturn(List.of(m1, m2));
        when(repository.findResponsesByRehearsal("ens-1")).thenReturn(List.of(
                confirmacao("ens-1", m1.telefone(), "SIM"),
                confirmacao("ens-1", m2.telefone(), "NAO")
        ));
        when(properties.getPrimaryAdminPhone()).thenReturn("5511911112222");

        rehearsalService.registerPresence("ens-1", m2.telefone(), false);

        verify(evolutionClient).sendTextMessage(
                org.mockito.ArgumentMatchers.eq("5511911112222"),
                org.mockito.ArgumentMatchers.contains("Todos responderam"));
    }

    @Test
    void registerPresence_faltaAlguemResponder_naoEnviaMensagem() {
        Member m1 = member("5511900000001", "Guitarra", true);
        Member m2 = member("5511900000002", "Baixo", true);
        when(repository.findAllMembers()).thenReturn(List.of(m1, m2));
        when(repository.findResponsesByRehearsal("ens-1")).thenReturn(List.of(
                confirmacao("ens-1", m1.telefone(), "SIM")
        ));

        rehearsalService.registerPresence("ens-1", m1.telefone(), true);

        verifyNoInteractions(evolutionClient);
    }

    @Test
    void registerPresence_semMembrosElegiveis_naoEnviaMensagemMesmoComZeroRespostas() {
        Member inativo = member("5511900000003", "Bateria", false);
        when(repository.findAllMembers()).thenReturn(List.of(inativo));
        when(repository.findResponsesByRehearsal("ens-1")).thenReturn(List.of());

        rehearsalService.registerPresence("ens-1", "5511900000003", true);

        verify(evolutionClient, never()).sendTextMessage(anyString(), anyString());
    }

    @Test
    void registerPresence_membroDeProjecaoNaoContaComoElegivel() {
        Member guitarrista = member("5511900000001", "Guitarra", true);
        Member projecao = member("5511900000004", "Projeção", true);
        when(repository.findAllMembers()).thenReturn(List.of(guitarrista, projecao));
        when(repository.findResponsesByRehearsal("ens-1")).thenReturn(List.of(
                confirmacao("ens-1", guitarrista.telefone(), "SIM")
        ));
        when(properties.getPrimaryAdminPhone()).thenReturn("5511911112222");

        rehearsalService.registerPresence("ens-1", guitarrista.telefone(), true);

        verify(evolutionClient).sendTextMessage(
                org.mockito.ArgumentMatchers.eq("5511911112222"),
                org.mockito.ArgumentMatchers.contains("Todos responderam"));
    }

    @Test
    void registerPresence_reconhecePresencaMesmoComTelefoneFormatadoDiferente() {
        Member m1 = member("11987654321", "Guitarra", true);
        when(repository.findAllMembers()).thenReturn(List.of(m1));
        when(repository.findResponsesByRehearsal("ens-1")).thenReturn(List.of(
                confirmacao("ens-1", "5511987654321", "SIM")
        ));
        when(properties.getPrimaryAdminPhone()).thenReturn("5511911112222");

        rehearsalService.registerPresence("ens-1", "5511987654321", true);

        verify(evolutionClient).sendTextMessage(
                org.mockito.ArgumentMatchers.eq("5511911112222"),
                org.mockito.ArgumentMatchers.contains("Todos responderam"));
    }

    private Member member(String phone, String instrumento, boolean ativo) {
        return new Member(PhoneUtils.generateId(), "Membro " + phone, phone, instrumento, ativo, false);
    }

    private ResponseRecord confirmacao(String rehearsalId, String phone, String valor) {
        return new ResponseRecord(PhoneUtils.generateId(), rehearsalId, phone, "CONFIRMACAO", valor,
                PhoneUtils.nowFormatted());
    }
}
