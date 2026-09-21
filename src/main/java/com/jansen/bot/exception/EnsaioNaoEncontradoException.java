package com.jansen.bot.exception;

/** Lançada quando se referencia um ensaio que não existe (ex.: voto para um ensaioId desconhecido). */
public class EnsaioNaoEncontradoException extends RuntimeException {

    public EnsaioNaoEncontradoException(String ensaioId) {
        super("Ensaio não encontrado: " + ensaioId);
    }
}
