package com.jansen.bot.rehearsal.domain;

/**
 * Resumo gerado ao encerrar uma votação (data-model.md § RelatorioVotacao; FR-010/FR-011).
 * Calculado, não persistido. A soma das contagens deve fechar com o total de elegíveis (FR-018).
 * O total vem do cadastro vigente, calculado por quem cria o relatório; aqui ele é só validado.
 * A decisão de quórum vem de {@link RegraDeQuorum} (research.md D6).
 * <p>
 * {@code percentualSim} (campo derivado, só para exibição) ainda não está aqui: o tipo não está
 * definido nos documentos. Ver {@code pendencias.md}, P-002.
 */
public record RelatorioVotacao(
        String ensaioId,
        int totalIntegrantesElegiveis,
        int confirmados,
        int recusados,
        int naoRespondeu,
        boolean abaixoDoQuorum) {

    public RelatorioVotacao {
        // FR-018
        if (confirmados + recusados + naoRespondeu != totalIntegrantesElegiveis) {
            throw new IllegalArgumentException(
                    "confirmados + recusados + naoRespondeu deve ser igual a totalIntegrantesElegiveis");
        }
    }
}
