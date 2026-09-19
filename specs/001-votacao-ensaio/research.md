# Research: Votação de Ensaio com Quórum e Remarcação

## D1 — Estilo de arquitetura: Ports & Adapters (Hexagonal)

- **Decision**: Modelar o novo comportamento em `rehearsal/domain` (POJOs/records puros, zero
  dependência de Spring), expostos via `rehearsal/ports` e orquestrados por
  `rehearsal/application/RehearsalVotingService`. Infra (Sheets, Evolution, relógio) entra
  como `rehearsal/adapters/out/*` implementando as portas.
- **Rationale**: O projeto já tem esse esqueleto criado (pastas com `.gitkeep`), então é a
  direção que alguém já decidiu antes — completar em vez de propor outra. Também é o padrão
  descrito em *Clean Architecture* (Martin) e no artigo original de Cockburn: isola regra de
  negócio (quórum, prazo, transições de status) de detalhes trocáveis (Sheets vs. Postgres,
  WhatsApp vs. outro canal), o que facilita testar quórum/prazo sem mocks.
- **Alternatives considered**: Continuar tudo em `RehearsalService` (mais rápido no curto
  prazo, mas repete o problema que motivou as characterization tests — lógica de negócio
  presa a `GoogleSheetsRepository`/`EvolutionClient` concretos).

## D2 — Estratégia de migração do legado: Strangler Fig

- **Decision**: `RehearsalService` e `ActionDispatcher` não são reescritos de uma vez. Cada
  `BotAction` relacionado a ensaio (`AGENDAR_ENSAIO`, `CONFIRMAR_PRESENCA`,
  `NEGAR_PRESENCA`, ...) é migrado individualmente para chamar
  `RehearsalVotingService`, um de cada vez, cada migração coberta por:
  1. characterization test já existente (garante que o comportamento antigo não regride
     onde ainda não foi migrado);
  2. teste novo (unidade de domínio + teste do caso de uso migrado).
- **Rationale**: É exatamente o processo descrito em *Working Effectively with Legacy Code*
  (Feathers, cap. "I Need to Change a Monster Method" / *Sprout/Wrap* + *Strangler*): usar a
  rede de testes de caracterização como grade de segurança enquanto se estrangula o código
  legado incrementalmente, em vez de um "big rewrite" arriscado.
- **Alternatives considered**: Reescrever `RehearsalService` inteiro numa tacada — rejeitado
  porque perde o valor das characterization tests recém-escritas e aumenta o raio de
  explosão de qualquer erro.

## D3 — Identidade de "líder"

- **Decision**: Reaproveitar `AppProperties.getAdminPhones()` / `ActionDispatcher.isAdmin()`
  como definição de "líder" para FR-001/FR-004/FR-013/FR-015. Não criar um segundo campo ou
  conceito de liderança.
- **Rationale**: É o mecanismo que **já** governa toda ação administrativa no bot
  (`handleScheduleRehearsal`, `handleShowRegistration`, `handleOpenMemberVote`, etc. todos
  chamam `isAdmin`). Duplicar esse conceito criaria duas fontes de verdade divergentes.
- **Alternatives considered**: Usar o campo `Member.admin` (existe no record `Member`, mas
  não é o que `ActionDispatcher` checa hoje) — rejeitado para não introduzir uma segunda
  noção de "admin" incompatível com o resto do bot.

## D4 — Persistência: manter Google Sheets, isolado atrás de porta

- **Decision**: `GoogleSheetsRehearsalAdapter` implementa `RehearsalRepositoryPort` chamando
  o `GoogleSheetsRepository` existente. Migração para Postgres (infra já parcialmente
  adicionada em `pom.xml`/`docker-compose.yml`, mas sem entidades JPA ainda) fica fora do
  escopo desta feature.
- **Rationale**: Evita acoplar o refactor de votação a uma migração de banco não pedida na
  spec — escopo mínimo primeiro. Como a porta já isola o domínio do Sheets, trocar para
  Postgres depois é implementar um novo adapter, não alterar `RehearsalVotingService`.
- **Alternatives considered**: Migrar para Postgres já nesta feature — rejeitado por escopo;
  nada na spec-001 pede troca de banco.

## D5 — Disparo do prazo de 12h e do lembrete de 1h antes

- **Decision**: Novo `RehearsalVotingScheduler` (`@Scheduled`, mesmo padrão de
  `RehearsalCloserScheduler`/`ReminderScheduler` já existentes), rodando a cada poucos
  minutos, consultando `Ensaio` via `ClockPort` (relógio injetável, não `LocalDateTime.now()`
  direto) para decidir quem precisa de lembrete e quais votações devem se encerrar.
- **Rationale**: Consistente com o resto do código (todos os prazos do bot já usam
  `@Scheduled` + polling, não filas/agendadores externos). `ClockPort` é o *seam* (Feathers)
  que permite testar "passou 11h"/"passou 12h" sem `Thread.sleep` ou mexer no relógio real.
- **Alternatives considered**: `ScheduledExecutorService` por ensaio (um timer por votação)
  — rejeitado por complexidade extra sem ganho real na escala do projeto (8 pessoas, poucos
  ensaios simultâneos) e por fugir do padrão já estabelecido no código.

## D6 — Regra de quórum na fronteira de 50%

- **Decision**: `RegraDeQuorum` calcula `abaixoDoQuorum = (votosSim * 2) < totalVotantes`
  (aritmética inteira, sem `double`).
- **Rationale**: A spec (Edge Cases) é explícita: exatamente 50% conta como quórum atingido,
  a pergunta extra só dispara **abaixo** de 50%. Multiplicar em vez de dividir evita erro de
  arredondamento de ponto flutuante — problema clássico de "off-by-a-hair" em regras de
  corte percentual.
- **Alternatives considered**: `percentual = votosSim / (double) total; abaixo = percentual < 0.5`
  — funciona na prática mas é mais frágil a bugs de precisão; preferida a versão inteira.

## Resumo — status das "NEEDS CLARIFICATION"

Nenhum item do Technical Context ficou como `NEEDS CLARIFICATION`: todas as lacunas técnicas
foram resolvidas acima (D1-D6) usando o que já existe no código (scaffold hexagonal,
`isAdmin`, schedulers) em vez de inventar mecanismos novos.
