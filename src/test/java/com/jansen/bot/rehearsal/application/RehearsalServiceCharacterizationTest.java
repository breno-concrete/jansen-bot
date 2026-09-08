package com.jansen.bot.rehearsal.application;


import com.jansen.bot.client.EvolutionClient;
import com.jansen.bot.config.AppProperties;
import com.jansen.bot.model.Rehearsal;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.service.RehearsalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

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

        assertEquals("20/09/2026 19h", rehearsal.dataHora());
        assertEquals("Estúdio Central", rehearsal.local());
        assertEquals("AGENDADO", rehearsal.status());
        assertEquals("", rehearsal.opcoesVoto());
        assertEquals("", rehearsal.vencedor());
        assertFalse(rehearsal.lembreteEnviado());
        assertNotNull(rehearsal.id());
        assertFalse(rehearsal.id().isBlank(), "PhoneUtils.generateId() não deveria devolver vazio");


        verify(repository).saveRehearsal(rehearsal);
    }
}
