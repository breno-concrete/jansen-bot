package com.jansen.bot.rehearsal.adapters.out;

import com.jansen.bot.model.Member;
import com.jansen.bot.rehearsal.domain.TipoEnsaio;
import com.jansen.bot.repository.GoogleSheetsRepository;
import com.jansen.bot.rehearsal.ports.IntegranteRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adapter de {@link IntegranteRepositoryPort} sobre o cadastro de membros do Google Sheets
 * (FR-018: cadastro vigente) filtrado pelo tipo do ensaio (FR-019). Mesma regra de exclusão de "projeção" de
 * {@code ActionDispatcher.isProjecao}.
 */
@Component
public class SheetsIntegranteAdapter implements IntegranteRepositoryPort {

    private final GoogleSheetsRepository repository;

    public SheetsIntegranteAdapter(GoogleSheetsRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<String> buscarTelefonesElegiveis(TipoEnsaio tipo) {
        return repository.findAllMembers().stream()
                .filter(Member::ativo)
                .filter(m -> !isProjecao(m))
                .filter(m -> convocadoPara(m, tipo))
                .map(Member::telefone)
                .toList();
    }

    /** FR-019: vocal = instrumento vocal/voz; instrumental = os demais; geral = todos. */
    private boolean convocadoPara(Member member, TipoEnsaio tipo) {
        return switch (tipo) {
            case VOCAL -> isVocal(member);
            case INSTRUMENTAL -> !isVocal(member);
            case GERAL -> true;
        };
    }

    private boolean isVocal(Member member) {
        String instr = member.instrumento().toLowerCase();
        return instr.contains("vocal") || instr.contains("voz");
    }

    private boolean isProjecao(Member member) {
        String instr = member.instrumento().toLowerCase();
        return instr.contains("proje") || instr.contains("projeção") || instr.contains("projecao");
    }
}
