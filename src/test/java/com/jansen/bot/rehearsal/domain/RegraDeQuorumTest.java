package com.jansen.bot.rehearsal.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Fronteira de 50% do quórum (spec.md Edge Cases; research.md D6).
 * Abaixo de 50% dispara a pergunta extra à líder (FR-011); 50% ou mais não (FR-012).
 */
class RegraDeQuorumTest {

    private final RegraDeQuorum regra = new RegraDeQuorum();

    @ParameterizedTest(name = "{0} sim de {1} -> abaixoDoQuorum={2}")
    @DisplayName("FR-011/FR-012: só está abaixo do quórum quando 'sim' fica estritamente abaixo de 50%")
    @CsvSource({
            "4, 8, false", // exatamente 50%: quórum atingido (quickstart.md, D6)
            "3, 8, true",  // abaixo de 50% (quickstart.md, D6)
            "5, 8, false", // acima de 50%
            "0, 8, true",  // ninguém confirmou
            "8, 8, false", // todos confirmaram
            "1, 2, false", // 50% com total menor
            "1, 3, true",  // total ímpar, abaixo de 50%
            "2, 3, false"  // total ímpar, acima de 50%
    })
    void abaixoDoQuorum_respeitaFronteiraDe50PorCento(int votosSim, int totalVotantes, boolean esperado) {
        assertEquals(esperado, regra.abaixoDoQuorum(votosSim, totalVotantes));
    }
}
