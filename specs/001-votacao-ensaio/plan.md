# Implementation Plan: Votação de Ensaio com Quórum e Remarcação

**Branch**: `001-votacao-ensaio` | **Date**: 2026-09-12 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-votacao-ensaio/spec.md`

## Summary

Substituir a confirmação simples de presença do `RehearsalService` legado por um fluxo de
votação com quórum: a líder cria um ensaio, os integrantes votam sim/não, a votação se
encerra quando todos responderem ou após 12h, um relatório vai para a líder (com pergunta
extra se o "sim" ficar abaixo de 50%), e a líder pode remarcar a qualquer momento reiniciando
a votação. A abordagem técnica é migrar a lógica de negócio para o esqueleto hexagonal já
existente em `rehearsal/{domain,ports,application,adapters}` usando o padrão **Strangler Fig**
(Fowler) sobre o `RehearsalService` legado, que já está protegido por characterization tests
(Feathers) — o legado não é reescrito de uma vez, é substituído caso de uso por caso de uso.

## Technical Context

**Language/Version**: Java 21, Spring Boot 3.4.1

**Primary Dependencies**: Spring Web/WebFlux, Spring Scheduling (`@Scheduled`, já usado em
`scheduler/*`), google-api-services-sheets (persistência atual), JUnit 5 + Mockito
(`spring-boot-starter-test`)

**Storage**: Google Sheets via `GoogleSheetsRepository` (persistência atual de `Rehearsal` e
`ResponseRecord`). Há infraestrutura Postgres/Flyway/JPA já adicionada ao `pom.xml` e
`docker-compose.yml`, mas ainda **não** conectada a nenhuma entidade — ver Decisão D4 em
`research.md`: esta feature mantém Sheets como adapter e isola o acesso atrás de uma porta,
para que a troca futura para Postgres seja uma implementação nova do adapter, não um
retrabalho do domínio.

**Testing**: JUnit 5 + Mockito. Characterization tests existentes em
`RehearsalServiceCharacterizationTest` continuam como rede de segurança de regressão do
legado durante toda a migração; novas classes de domínio ganham testes de unidade sem mocks
(são POJOs/records puros).

**Target Platform**: Serviço Spring Boot único (bot de WhatsApp via webhook Evolution API)

**Project Type**: Single project — monólito modular (sem frontend/mobile separado)

**Performance Goals**: N/A — banda de 8 pessoas, sem requisito de throughput. O único
requisito de tempo é o prazo de votação de 12h e o lembrete 1h antes, que dependem de um
scheduler rodando em intervalos curtos (ver Decisão D5), não de performance de request.

**Constraints**:
- Respeitar o intervalo de 20s entre mensagens sequenciais do `EvolutionClient`
  (`sendTextMessageSeries`, já implementado — reaproveitar, não reimplementar).
- "Líder" = número de telefone presente em `AppProperties.getAdminPhones()`, validado hoje
  por `ActionDispatcher.isAdmin(phone)`. Não introduzir um segundo conceito de "líder"
  paralelo ao `Member.admin`/`adminPhones` já usado no resto do bot.
- Votações concorrentes e independentes por ensaio (spec, seção Edge Cases) — o estado de
  votação deve viver por `Ensaio`/`rehearsalId`, nunca em um singleton compartilhado.

**Scale/Scope**: 8 integrantes, tipicamente 1 ensaio ativo por vez, mas o domínio deve
suportar N votações abertas em paralelo sem interferência entre elas.

## Constitution Check

`.specify/memory/constitution.md` ainda é o template não preenchido (nenhum princípio
ratificado) — não há gates formais do projeto a verificar. Na ausência de uma constituição,
este plano adota como guia os dois padrões de livro que já estão implícitos no código
existente e foram confirmados com o usuário como direção desejada:

1. **Working Effectively with Legacy Code** (Feathers) — nenhuma mudança de comportamento
   sem characterization test cobrindo o caminho antes; refactor incremental via *seams*.
2. **Ports & Adapters / Hexagonal Architecture** (Cockburn, também descrito em *Clean
   Architecture*, Martin) — já esboçado no projeto (`rehearsal/domain`, `rehearsal/ports`,
   `rehearsal/application`, `rehearsal/adapters/{in,out}`), este plano apenas o preenche em
   vez de propor uma estrutura nova.

*Sem violações a justificar — Complexity Tracking fica vazio.*

## Project Structure

### Documentation (this feature)

```text
specs/001-votacao-ensaio/
├── plan.md              # Este arquivo
├── research.md          # Fase 0 — decisões técnicas (D1-D6)
├── data-model.md         # Fase 1 — entidades de domínio
├── quickstart.md         # Fase 1 — como validar a feature
├── contracts/
│   └── rehearsal-ports.md  # Fase 1 — contratos das portas (in/out) do domínio
└── tasks.md              # Fase 2 — gerado por /speckit-tasks (ainda não criado)
```

### Source Code (repository root)

Reaproveita o esqueleto hexagonal já existente sob `rehearsal/` (hoje só com `.gitkeep`);
o legado em `service/RehearsalService.java` e `service/ActionDispatcher.java` permanece
intacto até cada caso de uso ser migrado (Strangler Fig — ver `research.md` D2):

```text
src/main/java/com/jansen/bot/rehearsal/
├── domain/
│   ├── Ensaio.java              # aggregate root: status, prazo, histórico de remarcações
│   ├── Voto.java                # value object: integranteId, escolha (SIM/NAO/NAO_RESPONDEU)
│   ├── RelatorioVotacao.java    # value object: contagens + percentual + flag de quórum
│   └── RegraDeQuorum.java       # Strategy isolando o cálculo de "abaixo de 50%"
├── ports/
│   ├── RehearsalRepositoryPort.java   # in-port de persistência (implementado por adapter out)
│   ├── NotificationPort.java          # out-port de envio de mensagem
│   └── ClockPort.java                 # out-port de tempo (testabilidade do prazo de 12h)
├── application/
│   └── RehearsalVotingService.java    # orquestra os casos de uso (novo "cérebro", sem Spring/infra)
├── adapters/in/web/
│   └── (chamado a partir do ActionDispatcher existente — sem endpoint HTTP novo)
├── adapters/out/persistence/
│   └── GoogleSheetsRehearsalAdapter.java  # implementa RehearsalRepositoryPort sobre GoogleSheetsRepository
└── adapters/out/messaging/
    └── EvolutionNotificationAdapter.java  # implementa NotificationPort sobre EvolutionClient

src/main/java/com/jansen/bot/service/
├── RehearsalService.java        # legado — some quando o último caso de uso for migrado
└── ActionDispatcher.java        # passa a delegar AGENDAR_ENSAIO/CONFIRMAR_PRESENCA/etc.
                                  # para RehearsalVotingService, um BotAction por vez
```

**Structure Decision**: Single project (monólito Spring Boot). Diretório único `rehearsal/`
concentra o novo domínio hexagonal; nada muda na estrutura de outros módulos (`show`,
`memberOfMonth`, etc.), que continuam no padrão Service/Controller antigo até terem seu
próprio refactor.

## Complexity Tracking

*Vazio — nenhuma violação de constituição a justificar.*
