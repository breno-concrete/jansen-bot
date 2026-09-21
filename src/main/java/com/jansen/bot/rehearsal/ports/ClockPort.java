package com.jansen.bot.rehearsal.ports;

import java.time.Instant;

/**
 * Out-port de tempo (contracts/rehearsal-ports.md): permite testar o prazo de 12h e o
 * lembrete de 1h (FR-007, FR-008) sem esperar de verdade.
 */
public interface ClockPort {

    Instant agora();
}
