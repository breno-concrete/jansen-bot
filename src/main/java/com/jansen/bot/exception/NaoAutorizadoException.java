package com.jansen.bot.exception;

/**
 * Lançada quando quem solicita uma ação restrita à líder não é líder
 * (FR-001, FR-013, FR-015; contracts/rehearsal-ports.md).
 */
public class NaoAutorizadoException extends RuntimeException {

    public NaoAutorizadoException(String message) {
        super(message);
    }
}
