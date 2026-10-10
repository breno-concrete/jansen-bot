package com.jansen.bot.rehearsal.adapters.in;

import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.rehearsal.domain.Voto;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Reconhece, sem IA, mensagens que são inteiramente um voto: uma palavra de sim/não e, opcionalmente,
 * o tipo do ensaio (FR-004, FR-022; P-031). Estrito: na dúvida devolve vazio e a mensagem segue para a
 * IA. O erro admitido é o falso "não reconheci", nunca o falso voto.
 */
@Component
public class InterpretadorDeVoto {

    /** Frases já normalizadas (sem acento, minúsculas). As mais longas vêm primeiro: "nao vou" antes de "nao". */
    private static final Map<String, Voto.Escolha> FRASES = new LinkedHashMap<>();

    static {
        FRASES.put("nao vou", Voto.Escolha.NAO);
        FRASES.put("nao posso", Voto.Escolha.NAO);
        FRASES.put("to fora", Voto.Escolha.NAO);
        FRASES.put("to dentro", Voto.Escolha.SIM);
        FRASES.put("confirmo", Voto.Escolha.SIM);
        FRASES.put("nao", Voto.Escolha.NAO);
        FRASES.put("vou", Voto.Escolha.SIM);
        FRASES.put("sim", Voto.Escolha.SIM);
    }

    public Optional<VotoInterpretado> interpretar(String texto) {
        // pergunta não é voto ("sim?", "quando é o ensaio?")
        if (texto == null || texto.contains("?")) {
            return Optional.empty();
        }
        String limpo = normalizar(texto);
        for (Map.Entry<String, Voto.Escolha> frase : FRASES.entrySet()) {
            if (limpo.equals(frase.getKey())) {
                return Optional.of(new VotoInterpretado(frase.getValue(), null));
            }
            if (limpo.startsWith(frase.getKey() + " ")) {
                TipoEnsaio tipo = tipoDe(limpo.substring(frase.getKey().length() + 1));
                if (tipo != null) {
                    return Optional.of(new VotoInterpretado(frase.getValue(), tipo));
                }
            }
        }
        return Optional.empty();
    }

    private String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim();
    }

    private TipoEnsaio tipoDe(String palavra) {
        for (TipoEnsaio tipo : TipoEnsaio.values()) {
            if (tipo.name().toLowerCase(Locale.ROOT).equals(palavra)) {
                return tipo;
            }
        }
        return null;
    }
}
