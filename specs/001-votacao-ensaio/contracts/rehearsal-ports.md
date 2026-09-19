# Contracts: Portas do domínio de votação de ensaio

Este projeto não expõe uma API HTTP pública para esta feature (entrada continua sendo
webhook do WhatsApp → `ActionDispatcher`). O "contrato" que importa aqui é a fronteira entre
`rehearsal/application` e o mundo externo, via `rehearsal/ports`. Qualquer adapter (Sheets,
Postgres futuro, Evolution, um teste fake) deve satisfazer estas interfaces.

## In-port (chamado pelo `ActionDispatcher`)

```java
public interface RehearsalVotingUseCase {
    Ensaio criarEnsaio(String telefoneSolicitante, LocalDateTime dataHora, String local);
    void registrarVoto(String ensaioId, String telefoneIntegrante, Voto.Escolha escolha);
    Ensaio remarcar(String telefoneSolicitante, String ensaioId, LocalDateTime novaDataHora);
}
```

- `criarEnsaio`/`remarcar` MUST rejeitar (lançar exceção de domínio, ex.
  `NaoAutorizadoException`) quando `telefoneSolicitante` não é líder (D3) — sem criar/alterar
  o `Ensaio`. Corresponde a FR-001, FR-013, FR-015.
- `registrarVoto` com `escolha` não reconhecida (texto livre não mapeado para SIM/NAO) MUST
  ser um no-op do ponto de vista do domínio — a camada de interpretação de linguagem natural
  (Claude/`ActionDispatcher`) é quem decide não chamar esta porta, não o domínio quem valida
  string livre (FR-004).

## Out-ports (implementados pelos adapters)

```java
public interface RehearsalRepositoryPort {
    void salvar(Ensaio ensaio);
    Optional<Ensaio> buscarPorId(String ensaioId);
    List<Ensaio> buscarComVotacaoAberta();
}

public interface NotificationPort {
    void notificarIntegrante(String telefone, String mensagem);
    void notificarTodos(List<String> telefones, String mensagem); // respeita rate limit de 20s
    void notificarLider(String mensagem);
}

public interface ClockPort {
    Instant agora();
}

public interface LeaderPolicyPort {
    boolean isLider(String telefone);
}
```

- `GoogleSheetsRehearsalAdapter implements RehearsalRepositoryPort` — delega para
  `GoogleSheetsRepository` (ver `data-model.md` § Mapeamento).
- `EvolutionNotificationAdapter implements NotificationPort` — delega para `EvolutionClient`,
  reaproveitando `sendTextMessageSeries` para `notificarTodos`.
- `ClockPort` em produção retorna `Instant.now()`; em teste, um fake com relógio controlável
  — é o *seam* (Feathers) que permite testar o prazo de 12h/lembrete de 1h sem esperar de
  verdade.
- `LeaderPolicyPort` existe para que `RehearsalVotingService` (aplicação) não dependa
  diretamente de `AppProperties` — mesmo motivo de isolar Sheets/Evolution atrás de porta
  (D1). `AdminPhoneLeaderPolicyAdapter implements LeaderPolicyPort` reaproveita a lógica de
  `ActionDispatcher.isAdmin()` (D3) em vez de duplicá-la.

## Regra de compatibilidade durante a migração (Strangler Fig)

Enquanto `ActionDispatcher` ainda não migrou um `BotAction` para `RehearsalVotingUseCase`,
esse `BotAction` continua chamando `RehearsalService` (legado) exatamente como hoje — os dois
caminhos não devem coexistir para o mesmo `BotAction` ao mesmo tempo (evita bugs de duas
fontes de verdade sobre o mesmo voto).
