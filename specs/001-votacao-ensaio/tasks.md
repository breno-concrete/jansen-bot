---

description: "Task list for spec-001 (votação de ensaio com quórum e remarcação)"
---

# Tasks: Votação de Ensaio com Quórum e Remarcação

**Input**: Design documents from `/specs/001-votacao-ensaio/` (plan.md, spec.md, research.md,
data-model.md, contracts/, quickstart.md)

**Tests**: Incluídos de propósito. O objetivo desta feature não é só "funcionar", é migrar
um serviço legado com segurança (Feathers) — isso só existe se cada passo tiver um teste que
falha antes e passa depois.

**Organização**: por User Story, na ordem de prioridade do `spec.md` (US1=P1, US2=P1,
US3=P2, US4=P2). Cada fase termina num "Checkpoint" — um ponto em que dá pra parar, rodar
tudo, e ter algo demonstrável.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: pode ser feito em paralelo (arquivo diferente, sem depender de uma task ainda não
  concluída)
- **[Story]**: a qual User Story a task pertence (US1-US4). Setup/Foundational/Polish não
  levam esse rótulo — são compartilhados.

---

## Phase 1: Setup

**Objetivo**: confirmar a baseline antes de tocar em qualquer coisa. Em legado, "baseline
verde" é o ponto de partida non-negociável — se algo já está quebrado antes de você começar,
você não vai saber depois se foi você que quebrou.

- [x] T001 Rodar `./mvnw test` com `JAVA_HOME` apontando para o JDK 21
      (`C:\Users\breno\.jdks\ms-21.0.10-1`) e confirmar que
      `RehearsalServiceCharacterizationTest` está 100% verde. Anote o resultado — é a
      baseline contra a qual toda mudança abaixo será comparada.
      _Auditado 2026-09-20: `mvn test` (o projeto não tem `mvnw`), JDK 21 → 10 testes, 0 falhas,
      0 erros, 0 skipped._
- [x] T002 [P] Confirmar que os pacotes `src/main/java/com/jansen/bot/rehearsal/{domain,ports,application,adapters/in/web,adapters/out/messaging,adapters/out/persistence}`
      existem (hoje só têm `.gitkeep`) e criar os pacotes de teste espelhados em
      `src/test/java/com/jansen/bot/rehearsal/{domain,application}` (ainda vazios).

**Checkpoint**: build limpo, baseline registrada, pastas prontas para receber código.

---

## Phase 2: Foundational (bloqueia todas as User Stories)

**Objetivo**: o vocabulário de domínio (`Ensaio`, `Voto`, `RelatorioVotacao`,
`RegraDeQuorum`) e as portas que todas as 4 User Stories vão usar. Nenhuma story começa
antes disso existir — é o "esqueleto" da arquitetura hexagonal do `plan.md`.

**⚠️ CRÍTICO**: não pule para a Phase 3 sem terminar esta fase — toda US depende dela.

- [x] T003 [P] Escrever teste que falha para `RegraDeQuorum` (fronteira de 50% — ver
      `research.md` D6: 4 de 8 "sim" NÃO é abaixo do quórum; 3 de 8 "sim" É) em
      `src/test/java/com/jansen/bot/rehearsal/domain/RegraDeQuorumTest.java`
- [x] T004 Implementar `RegraDeQuorum` (aritmética inteira `votosSim*2 >= total`, sem
      `double`) em `src/main/java/com/jansen/bot/rehearsal/domain/RegraDeQuorum.java` até
      T003 passar
- [x] T005 [P] Implementar `Voto` (record + enum `Escolha { SIM, NAO, NAO_RESPONDEU }`,
      campos `integranteId`, `ensaioId`, `respondidoEm` nullable — ver `data-model.md`) em
      `src/main/java/com/jansen/bot/rehearsal/domain/Voto.java`
- [x] T006 [P] Implementar `RelatorioVotacao` (record com `ensaioId`, `totalIntegrantesElegiveis`,
      `confirmados`, `recusados`, `naoRespondeu`, `percentualSim`, `abaixoDoQuorum` — ver
      `data-model.md`) em `src/main/java/com/jansen/bot/rehearsal/domain/RelatorioVotacao.java`
- [x] T007 [P] [US-shared] Escrever teste que falha para a criação de `Ensaio` (estado
      inicial: `status=VOTACAO_ABERTA`, `prazoVotacaoEm = criadoEm + 12h`,
      `historicoRemarcacoes` vazio — FR-007) em
      `src/test/java/com/jansen/bot/rehearsal/domain/EnsaioTest.java`
- [x] T008 Implementar `Ensaio` (aggregate root com os campos de `data-model.md`: `id`,
      `dataHora`, `local`, `criadoEm`, `prazoVotacaoEm`, `status`, `decisaoFinal`,
      `historicoRemarcacoes`) — só o suficiente para T007 passar — em
      `src/main/java/com/jansen/bot/rehearsal/domain/Ensaio.java` (depende de T005 para o
      tipo `Voto` usado internamente)
- [x] T009 [P] Definir a porta `RehearsalRepositoryPort` (`salvar`, `buscarPorId`,
      `buscarComVotacaoAberta` — ver `contracts/rehearsal-ports.md`) em
      `src/main/java/com/jansen/bot/rehearsal/ports/RehearsalRepositoryPort.java`
- [x] T010 [P] Definir a porta `NotificationPort` (`notificarIntegrante`, `notificarTodos`,
      `notificarLider`) em
      `src/main/java/com/jansen/bot/rehearsal/ports/NotificationPort.java`
- [x] T011 [P] Definir a porta `ClockPort` (`Instant agora()`) em
      `src/main/java/com/jansen/bot/rehearsal/ports/ClockPort.java`
- [x] T012 [P] Definir a porta `LeaderPolicyPort` (`boolean isLider(String telefone)` — ver
      `contracts/rehearsal-ports.md`, existe para o domínio não depender de `AppProperties`
      diretamente) em
      `src/main/java/com/jansen/bot/rehearsal/ports/LeaderPolicyPort.java`
- [x] T013 [P] Implementar `PostgresRehearsalAdapter implements RehearsalRepositoryPort` com
      Spring Data JPA (ver `research.md` D4 e `data-model.md` § Mapeamento) em
      `src/main/java/com/jansen/bot/rehearsal/adapters/out/persistence/`, incluindo: entidade
      JPA de `Ensaio` (+ histórico de remarcações), repositório Spring Data, migration Flyway
      da tabela, configuração de datasource via `.env`/`application.properties` e o database
      `jansenbot` no serviço `postgres-bot` do `docker-compose.yml` (volume próprio). Só `Ensaio` (votos entram na T022/T030).
      Teste de integração com Testcontainers (salvar, buscar por id, listar com votação
      aberta, rodando a migration) em
      `src/test/java/com/jansen/bot/rehearsal/adapters/out/persistence/`
- [x] T014 [P] Implementar `EvolutionNotificationAdapter implements NotificationPort`
      delegando para `EvolutionClient` (reaproveitar `sendTextMessageSeries` para
      `notificarTodos`, respeitando o intervalo de 20s já implementado) em
      `src/main/java/com/jansen/bot/rehearsal/adapters/out/messaging/EvolutionNotificationAdapter.java`
- [x] T015 [P] Implementar `SystemClock implements ClockPort` (retorna `Instant.now()` em
      produção) em `src/main/java/com/jansen/bot/rehearsal/adapters/out/SystemClock.java`
- [x] T016 [P] Implementar `AdminPhoneLeaderPolicyAdapter implements LeaderPolicyPort`
      reaproveitando a mesma lógica de `ActionDispatcher.isAdmin()` (ler
      `AppProperties.getAdminPhones()`, normalizar com `PhoneUtils`) em
      `src/main/java/com/jansen/bot/rehearsal/adapters/out/AdminPhoneLeaderPolicyAdapter.java`

**Checkpoint**: `./mvnw test` continua 100% verde (baseline T001 intacta) e agora existem,
além disso, `Ensaio`/`Voto`/`RelatorioVotacao`/`RegraDeQuorum` com teste próprio, e as 4
portas com um adapter real cada. Nenhum `BotAction` foi tocado ainda — o legado nem sabe que
isso existe.

---

## Phase 3: User Story 1 - Líder marca ensaio e integrantes votam (Priority: P1) 🎯 MVP

**Goal**: líder pede ensaio → todos os elegíveis recebem pedido de confirmação → cada
sim/não gera confirmação de volta pra quem respondeu. Pedido de quem não é líder é rejeitado.

**Independent Test** (do spec.md): enviar como líder um pedido de ensaio com data/hora,
verificar que cada integrante recebe o pedido, e que cada resposta sim/não gera confirmação.

### Tests for User Story 1 ⚠️ escrever ANTES da implementação, ver falhar, só então implementar

- [x] T017 [P] [US1] Teste: `RehearsalVotingService.criarEnsaio` chamado por um telefone que
      não é líder lança exceção (`NaoAutorizadoException` ou similar) e NÃO chama
      `RehearsalRepositoryPort.salvar` nem `NotificationPort` (FR-001) em
      `src/test/java/com/jansen/bot/rehearsal/application/RehearsalVotingServiceTest.java`
- [x] T018 [P] [US1] Teste: `criarEnsaio` chamado pela líder cria `Ensaio` com
      `status=VOTACAO_ABERTA`, persiste via `RehearsalRepositoryPort.salvar`, e notifica
      (via `NotificationPort.notificarTodos`) todos os integrantes elegíveis — mesma regra
      de exclusão de "projeção" que existe hoje em `RehearsalService.checkAllResponded`
      (FR-002, FR-003) no mesmo arquivo de T017
- [x] T019 [P] [US1] Teste: `registrarVoto` com escolha SIM ou NAO grava o `Voto` e chama
      `NotificationPort.notificarIntegrante` confirmando o recebimento para quem votou
      (FR-005) no mesmo arquivo de T017
- [x] T020 [US1] Rodar `RehearsalServiceCharacterizationTest` de novo (deve continuar 100%
      verde — `ActionDispatcher` ainda não foi tocado nesta fase, então o legado não pode
      ter mudado de comportamento)

### Implementation for User Story 1

- [x] T021 [US1] Criar `RehearsalVotingService` implementando `criarEnsaio` (usa
      `LeaderPolicyPort` para autorizar, `RehearsalRepositoryPort` para persistir,
      `NotificationPort` para avisar todos elegíveis) até T017/T018 passarem em
      `src/main/java/com/jansen/bot/rehearsal/application/RehearsalVotingService.java`
      (depende de T009, T010, T012)
- [x] T022 [US1] Implementar `registrarVoto` no mesmo `RehearsalVotingService.java` até T019
      passar (depende de T021)
- [ ] T023 [US1] Migrar `ActionDispatcher.handleScheduleRehearsal` (`BotAction.AGENDAR_ENSAIO`)
      para chamar `RehearsalVotingService.criarEnsaio` em vez de
      `RehearsalService.createScheduledRehearsal` — **este é o primeiro corte do Strangler
      Fig** (research.md D2) — em `src/main/java/com/jansen/bot/service/ActionDispatcher.java`
      (depende de T021; rodar T001+T020 de novo logo depois)
- [ ] T024 [US1] Migrar os cases `BotAction.CONFIRMAR_PRESENCA` e `BotAction.NEGAR_PRESENCA`
      em `ActionDispatcher.dispatch` para chamar `RehearsalVotingService.registrarVoto` em
      vez de `RehearsalService.registerPresence`, no mesmo arquivo de T023 (depende de T022)
- [ ] T025 [US1] Rodar `./mvnw test` completo (baseline + T017-T019) e validar manualmente os
      passos 1-3 do `quickstart.md` § 4

**Checkpoint**: US1 funciona de ponta a ponta de forma independente. `AGENDAR_ENSAIO`,
`CONFIRMAR_PRESENCA` e `NEGAR_PRESENCA` já passam pelo novo `RehearsalVotingService`;
`RehearsalService` legado continua existindo (ainda é chamado por `CONCLUIR_ENSAIO` e
`CONTAR_ENSAIOS`, que só migram nas próximas fases).

---

## Phase 4: User Story 2 - Encerramento da votação e relatório para a líder (Priority: P1)

**Goal**: quando todos responderem, a votação encerra na hora e a líder recebe o relatório;
se o "sim" ficar abaixo de 50%, vem também a pergunta extra.

**Independent Test**: todos respondem → líder recebe relatório final; separadamente, um
cenário com <50% de sim → líder recebe a pergunta extra.

### Tests for User Story 2 ⚠️

- [ ] T026 [P] [US2] Teste: `Ensaio` muda de `VOTACAO_ABERTA` para `ENCERRADA`
      automaticamente assim que o último integrante elegível vota, mesmo antes das 12h
      (FR-006) em `src/test/java/com/jansen/bot/rehearsal/domain/EnsaioTest.java`
- [ ] T027 [P] [US2] Teste: `Ensaio.gerarRelatorio(RegraDeQuorum)` retorna
      `RelatorioVotacao` com as contagens corretas e `abaixoDoQuorum=true` quando sim<50%,
      `false` quando sim>=50% (Edge Case da fronteira de 50%) no mesmo arquivo de T026
- [ ] T028 [P] [US2] Teste: ao encerrar a votação, `RehearsalVotingService` chama
      `NotificationPort.notificarLider` com o relatório e, se `abaixoDoQuorum`, inclui a
      pergunta extra sobre manter o ensaio (FR-010, FR-011); quando não está abaixo do
      quórum, a mensagem NÃO inclui a pergunta extra (FR-012) em
      `src/test/java/com/jansen/bot/rehearsal/application/RehearsalVotingServiceTest.java`
- [ ] T029 [US2] Rodar `RehearsalServiceCharacterizationTest` + testes de US1 de novo
      (regressão)

### Implementation for User Story 2

- [ ] T030 [US2] Implementar em `Ensaio.java` a verificação de "todos votaram" logo após
      registrar um voto, fechando a votação (`status=ENCERRADA`) quando aplicável, até T026
      passar (depende de T008)
- [ ] T031 [US2] Implementar `Ensaio.gerarRelatorio(RegraDeQuorum)` até T027 passar, no
      mesmo arquivo (depende de T004, T006)
- [ ] T032 [US2] Estender `RehearsalVotingService.registrarVoto` (de T022) para, após
      registrar o voto, checar se `Ensaio` encerrou e, se sim, gerar o relatório e chamar
      `NotificationPort.notificarLider` (com a pergunta extra quando `abaixoDoQuorum`) até
      T028 passar, em `application/RehearsalVotingService.java` (depende de T030, T031)
- [ ] T033 [US2] Validar manualmente os 3 Acceptance Scenarios de US2 no `quickstart.md`

**Checkpoint**: US1 + US2 funcionam juntas — o fluxo completo "líder marca → todos votam →
líder recebe relatório (com ou sem pergunta de quórum baixo)" já é demonstrável.

---

## Phase 5: User Story 3 - Prazo de 12h, lembrete e "não respondeu" (Priority: P2)

**Goal**: quem não responde recebe lembrete 1h antes do prazo; se ainda assim não responder,
a votação encerra às 12h com esse integrante marcado como "não respondeu" (tag distinta de
recusa explícita), e o relatório vai pra líder mesmo assim.

**Independent Test**: avançar o relógio de teste até 1h antes das 12h → lembrete enviado;
avançar até as 12h → votação encerra, relatório chega, pendente aparece como "não
respondeu".

### Tests for User Story 3 ⚠️

- [ ] T034 [P] [US3] Teste: com `ClockPort` fake em `criadoEm + 11h`, integrante pendente
      recebe exatamente um lembrete (não duplicado se o método rodar de novo antes da 1h
      seguinte) — FR-008 — em
      `src/test/java/com/jansen/bot/rehearsal/domain/EnsaioTest.java`
- [ ] T035 [P] [US3] Teste: com `ClockPort` fake em `criadoEm + 12h`, `Ensaio` encerra
      mesmo com pendências, e cada pendente vira `Voto` com `escolha=NAO_RESPONDEU` — tag
      diferente de `NAO` explícito (FR-009) no mesmo arquivo de T034
- [ ] T036 [P] [US3] Teste: integrante que respondeu entre o lembrete e as 12h mantém sua
      resposta original no relatório final, sem a tag de "não respondeu" (Acceptance
      Scenario 3 de US3) no mesmo arquivo de T034
- [ ] T037 [P] [US3] Teste: `RehearsalVotingService.processarPrazos()` — usando
      `RehearsalRepositoryPort.buscarComVotacaoAberta()` — envia lembretes e fecha votações
      vencidas para todos os `Ensaio` abertos, chamando `NotificationPort.notificarLider`
      com o relatório atualizado quando fecha por prazo, em
      `src/test/java/com/jansen/bot/rehearsal/application/RehearsalVotingServiceTest.java`
- [ ] T038 [US3] Rodar toda a suíte de novo (regressão de US1+US2+legado)

### Implementation for User Story 3

- [ ] T039 [US3] Implementar em `Ensaio.java` os métodos de prazo: identificar quem precisa
      de lembrete dado um `Instant agora`, e encerrar marcando `NAO_RESPONDEU` quando
      `agora >= prazoVotacaoEm`, até T034-T036 passarem (depende de T008, T030)
- [ ] T040 [US3] Implementar `RehearsalVotingService.processarPrazos()` orquestrando os
      métodos de T039 para todos os ensaios abertos, reaproveitando a geração de relatório
      de T031/T032 ao fechar por prazo, até T037 passar, em
      `application/RehearsalVotingService.java` (depende de T039, T009)
- [ ] T041 [US3] Criar `RehearsalVotingScheduler` (`@Scheduled`, mesmo padrão de
      `RehearsalCloserScheduler` já existente) chamando `processarPrazos()` a cada poucos
      minutos em `src/main/java/com/jansen/bot/scheduler/RehearsalVotingScheduler.java`
      (depende de T040)
- [ ] T042 [US3] Validar manualmente os passos 3-5 do `quickstart.md` § 4 (usando um
      `ClockPort` de teste acelerado, não esperar 11h de verdade)

**Checkpoint**: votações que ninguém fecha manualmente agora se resolvem sozinhas — lembrete
e "não respondeu" funcionam sem intervenção humana.

---

## Phase 6: User Story 4 - Líder remarca o ensaio (Priority: P2)

**Goal**: a qualquer momento, com votação aberta ou já encerrada, a líder pode remarcar —
reinicia a votação do zero (todos voltam a pendente) e o prazo de 12h reconta a partir da
remarcação.

**Independent Test**: pedir remarcação (com votação aberta e, em outro cenário, já
encerrada) e verificar aviso claro a todos + nova rodada de votação.

### Tests for User Story 4 ⚠️

- [ ] T043 [P] [US4] Teste: `remarcar` chamado por quem não é líder lança exceção e não
      altera o `Ensaio` (FR-015) em
      `src/test/java/com/jansen/bot/rehearsal/application/RehearsalVotingServiceTest.java`
- [ ] T044 [P] [US4] Teste: `Ensaio.remarcar(novaDataHora, agora)` com votação ainda aberta
      encerra a rodada atual, reseta todos os `Voto` para pendente, define novo
      `prazoVotacaoEm = agora + 12h`, e registra a remarcação em `historicoRemarcacoes`
      (FR-013, FR-014, FR-017) em
      `src/test/java/com/jansen/bot/rehearsal/domain/EnsaioTest.java`
- [ ] T045 [P] [US4] Teste: o mesmo `remarcar` funciona igual quando a votação já estava
      `ENCERRADA` (FR-013, Acceptance Scenario 2 de US4) no mesmo arquivo de T044
- [ ] T046 [P] [US4] Teste: duas remarcações consecutivas antes de qualquer resposta —
      sempre vale a mais recente, e o prazo reconta a partir dela (Edge Case do spec.md) no
      mesmo arquivo de T044
- [ ] T047 [P] [US4] Teste: `RehearsalVotingService.remarcar` chama
      `NotificationPort.notificarTodos` com uma mensagem que deixa claro que o ensaio foi
      remarcado, incluindo a nova data/hora (FR-014) no mesmo arquivo de T043
- [ ] T048 [US4] Rodar toda a suíte de novo (regressão de US1+US2+US3+legado)

### Implementation for User Story 4

- [ ] T049 [US4] Implementar `Ensaio.remarcar(novaDataHora, agora)` até T044-T046 passarem,
      em `domain/Ensaio.java` (depende de T008, T039)
- [ ] T050 [US4] Implementar `RehearsalVotingService.remarcar` (autoriza via
      `LeaderPolicyPort`, chama `Ensaio.remarcar`, persiste, notifica todos) até T043/T047
      passarem, em `application/RehearsalVotingService.java` (depende de T049, T012)
- [ ] T051 [US4] Adicionar `BotAction.REMARCAR_ENSAIO` em
      `src/main/java/com/jansen/bot/model/BotAction.java` e o novo case em
      `ActionDispatcher.dispatch` chamando `RehearsalVotingService.remarcar` em
      `service/ActionDispatcher.java` (depende de T050)
- [ ] T052 [US4] Atualizar `src/main/resources/system-prompt.txt` para ensinar a IA a
      reconhecer pedidos de remarcação da líder e mapeá-los para `REMARCAR_ENSAIO` (mesmo
      formato das entradas existentes para `AGENDAR_ENSAIO`)
- [ ] T053 [US4] Validar manualmente o passo 6 do `quickstart.md` § 4

**Checkpoint**: as 4 User Stories do spec-001 funcionam de ponta a ponta.

---

## Phase 7: Polish & Cross-Cutting

**Objetivo**: fechar o Strangler Fig — só aqui o legado sai de cena, não antes.

- [ ] T054 Migrar `BotAction.CONCLUIR_ENSAIO` e qualquer outro uso restante de
      `RehearsalService` em `ActionDispatcher.java` para `RehearsalVotingService`, ou
      documentar explicitamente por que fica de fora do escopo de spec-001
- [ ] T055 Confirmar que nenhum `BotAction` de ensaio chama mais `RehearsalService` (buscar
      por `rehearsalService\.` em `ActionDispatcher.java`)
- [ ] T056 Remover `src/main/java/com/jansen/bot/service/RehearsalService.java` **somente**
      depois de T055 confirmado, e mover/adaptar
      `RehearsalServiceCharacterizationTest.java` para characterization do que sobrar (ou
      removê-lo, já que o código que ele caracteriza deixou de existir)
- [ ] T057 [P] Rodar `quickstart.md` do início ao fim como checklist de aceite final
- [ ] T058 [P] Revisar `AGENTS.md` — acrescentar `specs/001-votacao-ensaio/` como spec de
      referência para regras de negócio de ensaio, já que `specsPAST/` ficou para as specs
      antigas

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)** → sem dependências, começa imediatamente
- **Foundational (Phase 2)** → depende do Setup — **bloqueia todas as User Stories**
- **US1 (Phase 3)** → depende só do Foundational
- **US2 (Phase 4)** → depende do Foundational **e** de US1 (estende `registrarVoto` criado
  em T022) — não é 100% independente de código, mas é independentemente **testável** (US2
  tem seus próprios testes e Independent Test no spec)
- **US3 (Phase 5)** → depende do Foundational e reaproveita `gerarRelatorio` de US2 (T031)
- **US4 (Phase 6)** → depende do Foundational; reaproveita `Ensaio` de US1/US2/US3 mas sua
  lógica de remarcação é isolada
- **Polish (Phase 7)** → depende de todas as stories que forem entregues

### Dentro de cada User Story

Testes (escritos primeiro, devem falhar) → domínio → aplicação (`RehearsalVotingService`) →
integração com `ActionDispatcher` (só quando a story exige mudar um `BotAction`) → validação
manual do `quickstart.md`.

### Paralelismo

- T003, T005, T006, T007 (Foundational) tocam arquivos diferentes → podem ser feitas em
  paralelo
- T009-T012 (as 4 portas) → arquivos diferentes, paralelas entre si
- T013-T016 (os 4 adapters) → arquivos diferentes, paralelas entre si, mas dependem das
  portas correspondentes já existirem
- Dentro de cada User Story, as tasks de teste marcadas `[P]` podem ser escritas em paralelo
  (arquivos diferentes ou testes independentes no mesmo arquivo); as tasks de implementação
  que seguem geralmente não são `[P]` porque tocam o mesmo `RehearsalVotingService.java` ou
  `Ensaio.java` incrementalmente

---

## Implementation Strategy

### MVP primeiro (só US1)

1. Phase 1 (Setup) → Phase 2 (Foundational) → Phase 3 (US1)
2. Parar e validar US1 sozinha (quickstart.md § 4, passos 1-3)
3. Nesse ponto já dá pra "demonstrar": líder marca ensaio, todos confirmam presença — só
   falta quórum/prazo/remarcação

### Entrega incremental

Setup+Foundational → US1 (MVP) → US2 (relatório e quórum) → US3 (prazo automático) → US4
(remarcação) → Polish (remove o legado). Cada checkpoint acima é um ponto de
demo/deploy real — nenhuma story deixa as anteriores quebradas.

---

## Notes para quem está aprendendo o processo

- **Por que Foundational vem antes de tudo?** Porque `Ensaio`/`Voto` e as portas são o
  "vocabulário" que as 4 stories compartilham. Se cada story criasse sua própria versão de
  `Ensaio`, você teria 4 conceitos de ensaio divergindo com o tempo.
- **Por que os testes vêm antes da implementação em cada story?** Esse é o ciclo
  Red-Green-Refactor: o teste que falha (Red) prova que ele está testando a coisa certa —
  um teste que já nasce verde não prova nada, porque ele passaria mesmo sem a implementação.
- **Por que tanta task fala em "rodar a suíte de novo"?** É a mecânica central do Michael
  Feathers: a rede de characterization tests só protege contra regressão se você
  efetivamente rodar ela depois de cada corte do legado (T020, T029, T038, T048, T055) — ter
  a rede e nunca puxá-la de novo é o mesmo que não ter rede.
- **Por que `RehearsalService` só morre na Phase 7, não na Phase 3?** Strangler Fig: o
  código velho e o novo convivem enquanto a migração está em andamento. Remover cedo demais
  tira sua rede de segurança (comparação de comportamento) antes de terminar.
