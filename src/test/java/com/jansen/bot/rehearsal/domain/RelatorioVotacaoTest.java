package com.jansen.bot.rehearsal.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * FR-018: confirmados + recusados + não respondidos deve ser igual ao total de elegíveis.
 */
class RelatorioVotacaoTest {

    @Test
    @DisplayName("FR-018: aceita relatório em que a soma das contagens é igual ao total de elegíveis")
    void aceitaContagensConsistentes() {
        assertDoesNotThrow(() -> new RelatorioVotacao("e1", 7, 4, 2, 1, false));
    }

    @Test
    @DisplayName("FR-018: rejeita relatório em que a soma das contagens é menor que o total")
    void rejeitaSomaMenorQueOTotal() {
        assertThrows(IllegalArgumentException.class,
                () -> new RelatorioVotacao("e1", 7, 4, 2, 0, false));
    }

    @Test
    @DisplayName("FR-018: rejeita relatório em que a soma das contagens é maior que o total")
    void rejeitaSomaMaiorQueOTotal() {
        assertThrows(IllegalArgumentException.class,
                () -> new RelatorioVotacao("e1", 7, 5, 2, 1, false));
    }

    @Test
    @DisplayName("FR-018: o total varia com o cadastro (ex.: 3 integrantes elegíveis)")
    void totalNaoEhFixo() {
        assertDoesNotThrow(() -> new RelatorioVotacao("e1", 3, 1, 1, 1, true));
    }
}
