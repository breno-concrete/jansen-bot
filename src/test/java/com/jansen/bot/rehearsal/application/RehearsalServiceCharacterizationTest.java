package com.jansen.bot.rehearsal.application;


import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.Member;
import com.jansen.bot.model.Rehearsal;
import com.jansen.bot.model.ResponseRecord;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.service.RehearsalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    @DisplayName("Nível 0 — dependências são injetadas e o serviço é instanciado sem erro")
    void deveInstanciarComDependenciasMockadas() {
        assertNotNull(rehearsalService,
                "RehearsalService deveria ter sido instanciado pelo Mockito via @InjectMocks");
    }

    @Test
    @DisplayName("Nível 1a — cria ensaio com dataHora e local informados")
    void deveCriarEnsaioComDataHoraELocalInformados(){


        Rehearsal rehearsal = rehearsalService.createScheduledRehearsal(
                "2024-07-01 19:00", "Sala de Ensaios A");

        assertEquals("2024-07-01 19:00", rehearsal.dataHora());
        assertEquals("Sala de Ensaios A", rehearsal.local());
        assertEquals("AGENDADO", rehearsal.status());
        assertEquals("", rehearsal.opcoesVoto());
        assertEquals("", rehearsal.vencedor());
        assertFalse(rehearsal.lembreteEnviado());
        assertNotNull(rehearsal.id());
        assertFalse(rehearsal.id().isBlank(), "PhoneUtils.generateId() não deveria devolver vazio");


        verify(repository).saveRehearsal(rehearsal);
    }

    @Test
    @DisplayName("Nível 1b — retorna valores padrão para entradas vazias")
    void deveRetornarValoresPadraoQuandoDataHoraELocalSaoNulo(){
        Rehearsal rehearsal = rehearsalService.createScheduledRehearsal(null, null);

        assertEquals("", rehearsal.dataHora());
        assertEquals("A definir", rehearsal.local());


        verify(repository).saveRehearsal(rehearsal);
    }


    @Test
    @DisplayName("Nível 2 — registra voto de membro com id, phone e dataOpcao")
    void deveRegistrarVotosComIdMemberPhoneEDataOpcao(){

        ArgumentCaptor<ResponseRecord> captor = ArgumentCaptor.forClass(ResponseRecord.class);

        rehearsalService.registerVote(
                "123456789",
                "987654321",
                "2024-07-01 19:00");

        verify(repository).saveResponse(captor.capture());
        ResponseRecord captured = captor.getValue();

        assertEquals("123456789", captured.rehearsalId());
        assertEquals("987654321", captured.memberPhone());
        assertEquals("VOTO", captured.tipo());
        assertEquals("2024-07-01 19:00", captured.valor());
        assertNotNull(captured.id());
        assertFalse(captured.id().isBlank(), "PhoneUtils.generateId() não deveria devolver vazio");
        assertNotNull(captured.timestamp());



    }

    @Test
    @DisplayName("Nível 3 — registra presença de membro com id, phone e confirmado")
    void deveRegistarPresencaComIdMemberPhoneEConfirmado(){

        ArgumentCaptor<ResponseRecord> captor = ArgumentCaptor.forClass(ResponseRecord.class);

        rehearsalService.registerPresence(
                "123456789",
                "987654321",
                true);


        verify(repository).saveResponse(captor.capture());

        assertEquals("123456789", captor.getValue().rehearsalId());
        assertEquals("987654321", captor.getValue().memberPhone());
        assertEquals("CONFIRMACAO", captor.getValue().tipo());
        assertEquals("SIM", captor.getValue().valor());
        assertNotNull(captor.getValue().id());
        assertFalse(captor.getValue().id().isBlank(), "PhoneUtils.generateId() não deveria devolver vazio");
        assertNotNull(captor.getValue().timestamp());

        verifyNoInteractions(evolutionClient);
    }

    @Test
    @DisplayName("Nível 4 — chama EvolutionClient quando todos responderem")
    void deveChamarEvolutionClientQuandoTodosResponderem(){

        Member member1 = new Member("111", "Alice", "111111111","Teclado", true, false);

        when(repository.findAllMembers()).thenReturn(List.of(member1));

        ResponseRecord record = new ResponseRecord(
                "id1",
                "rehearsal1",
                "111111111",
                "CONFIRMACAO",
                "SIM",
                "2024-06-30 10:00"
        );

        when(repository.findResponsesByRehearsal(record.rehearsalId())).thenReturn(List.of(record));
        when(properties.getPrimaryAdminPhone()).thenReturn("999999999");

        rehearsalService.registerPresence(
                record.rehearsalId(),
                record.memberPhone(),
                true);



        verify(evolutionClient).sendTextMessage(eq("999999999"), anyString());

    }

    @Test
    @DisplayName("Nível 5a — finaliza votação atualizando data vencedora quando ensaio existe")
    void deveFinalizarVotacaoQuandoEnsaioExiste() {
        Rehearsal existente = new Rehearsal(
                "r1", "opcao1;opcao2", "Estúdio X", "VOTACAO", "opcao1;opcao2", "", false);
        when(repository.findRehearsalById("r1")).thenReturn(Optional.of(existente));

        rehearsalService.finalizeVoting("r1", "2024-08-01 20:00");

        ArgumentCaptor<Rehearsal> captor = ArgumentCaptor.forClass(Rehearsal.class);
        verify(repository).updateRehearsal(captor.capture());
        Rehearsal updated = captor.getValue();

        assertEquals("r1", updated.id());
        assertEquals("2024-08-01 20:00", updated.dataHora());
        assertEquals("Estúdio X", updated.local());
        assertEquals("AGENDADO", updated.status());
        assertEquals("opcao1;opcao2", updated.opcoesVoto());
        assertEquals("2024-08-01 20:00", updated.vencedor());
        assertFalse(updated.lembreteEnviado());
    }

    @Test
    @DisplayName("Nível 5b — não atualiza nada quando ensaio não é encontrado")
    void naoDeveAtualizarQuandoEnsaioNaoEncontrado() {
        when(repository.findRehearsalById("inexistente")).thenReturn(Optional.empty());

        rehearsalService.finalizeVoting("inexistente", "2024-08-01 20:00");

        verify(repository, never()).updateRehearsal(any());
    }

    @Test
    @DisplayName("Nível 6 — monta resumo de presença com confirmados, negados e sem resposta")
    void deveMontarResumoDePresenca() {
        Member alice = new Member("m1", "Alice", "111", "Guitarra", true, false);
        Member bob = new Member("m2", "Bob", "222", "Baixo", true, false);
        Member carol = new Member("m3", "Carol", "333", "Bateria", true, false);
        when(repository.findAllMembers()).thenReturn(List.of(alice, bob, carol));

        ResponseRecord respostaAlice = new ResponseRecord(
                "resp1", "r1", "111", "CONFIRMACAO", "SIM", "2024-06-30 10:00");
        ResponseRecord respostaBob = new ResponseRecord(
                "resp2", "r1", "222", "CONFIRMACAO", "NAO", "2024-06-30 10:05");
        when(repository.findResponsesByRehearsal("r1")).thenReturn(List.of(respostaAlice, respostaBob));

        String resumo = rehearsalService.buildPresenceSummary("r1");

        String esperado = "📋 *Presença no ensaio:*\n\n" +
                "✅ *Confirmados:*\n" +
                "  • Alice\n" +
                "\n❌ *Não vão:*\n" +
                "  • Bob\n" +
                "\n⏳ *Sem resposta:*\n" +
                "  • Carol\n";

        assertEquals(esperado, resumo);
    }

    @Test
    @DisplayName("Nível 7 — envia lembretes só para membros ativos e marca lembreteEnviado")
    void deveEnviarLembretesParaMembrosAtivos() {
        Member ativo = new Member("m1", "Alice", "111", "Guitarra", true, false);
        Member inativo = new Member("m2", "Bob", "222", "Baixo", false, false);
        when(repository.findAllMembers()).thenReturn(List.of(ativo, inativo));

        Rehearsal rehearsal = new Rehearsal(
                "r1", "2024-08-01 20:00", "Estúdio X", "AGENDADO", "", "", false);

        rehearsalService.sendReminders(rehearsal);

        ArgumentCaptor<List<Member>> membersCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(evolutionClient).sendTextMessageSeries(membersCaptor.capture(), messageCaptor.capture());

        assertEquals(List.of(ativo), membersCaptor.getValue());
        assertEquals(
                "🎸 *Lembrete de ensaio!*\n\n" +
                        "Amanhã tem ensaio!\n📅 2024-08-01 20:00\n📍 Estúdio X\n\n" +
                        "Confirma presença respondendo *sim* ou *não*!",
                messageCaptor.getValue());

        ArgumentCaptor<Rehearsal> rehearsalCaptor = ArgumentCaptor.forClass(Rehearsal.class);
        verify(repository).updateRehearsal(rehearsalCaptor.capture());
        Rehearsal updated = rehearsalCaptor.getValue();

        assertEquals("r1", updated.id());
        assertEquals("2024-08-01 20:00", updated.dataHora());
        assertEquals("Estúdio X", updated.local());
        assertTrue(updated.lembreteEnviado());
    }
}
