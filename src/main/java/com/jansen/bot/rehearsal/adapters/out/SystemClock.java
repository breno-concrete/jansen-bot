package com.jansen.bot.rehearsal.adapters.out;

import com.jansen.bot.rehearsal.ports.ClockPort;
import org.springframework.stereotype.Component;

import java.time.Instant;

/** Adapter de produção de {@link ClockPort}: relógio real. */
@Component
public class SystemClock implements ClockPort {

    @Override
    public Instant agora() {
        return Instant.now();
    }
}
