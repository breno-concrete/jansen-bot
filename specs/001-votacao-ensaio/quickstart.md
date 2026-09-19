# Quickstart: validando a votação de ensaio

## Pré-requisitos

- JDK 21 ativo (`JAVA_HOME` apontando para `C:\Users\breno\.jdks\ms-21.0.10-1` — Mockito/Byte
  Buddy falha ao mockar classes sob JDK 25, ver nota de projeto já registrada).
- Nenhuma infra externa necessária para os testes abaixo (Sheets/Evolution são mockados ou
  substituídos por fakes das portas).

## 1. Rede de regressão do legado (rodar antes e depois de cada migração de `BotAction`)

```powershell
./mvnw test -Dtest=RehearsalServiceCharacterizationTest
```

Esperado: todos os testes continuam verdes durante toda a migração — se algum quebrar, a
mudança alterou comportamento observável do legado antes da hora.

## 2. Testes de domínio novos (sem mocks — regras puras)

Local sugerido: `src/test/java/com/jansen/bot/rehearsal/domain/`

- `RegraDeQuorumTest`: tabela de casos cobrindo a fronteira dos 50% (D6) — ex. 4 de 8 "sim"
  (exatamente 50%) NÃO deve disparar a pergunta extra; 3 de 8 "sim" deve disparar.
- `EnsaioTest`: transições de `status` — encerra quando todos votam antes do prazo; encerra
  ao atingir `prazoVotacaoEm` mesmo com pendências; voto após `ENCERRADA` é descartado sem
  reabrir (FR-016); remarcação reinicia votos e prazo (FR-017).

## 3. Caso de uso ponta a ponta com fakes das portas

`RehearsalVotingServiceTest` usando fakes de `RehearsalRepositoryPort`/`NotificationPort`/
`ClockPort` (não Mockito — são poucas operações, um fake simples é mais legível e é o
padrão recomendado para domínio hexagonal). Cenário guia: exatamente o "Independent Test" de
cada User Story em `spec.md` (US1 a US4).

## 4. Validação manual (smoke test via WhatsApp/Evolution sandbox)

1. Como líder (telefone em `banda.admin-phones`), pedir para marcar ensaio numa data.
2. Confirmar que todos os integrantes elegíveis (exceto projeção) recebem o pedido de
   presença — igual ao comportamento atual de `handleScheduleRehearsal`.
3. Responder "sim"/"não" com 2+ integrantes e deixar 1 pendente.
4. Confirmar que o pendente recebe lembrete quando faltar 1h para as 12h (ajustar
   `ClockPort`/relógio de teste para acelerar, não esperar 11h de verdade).
5. Deixar o prazo se esgotar e confirmar: relatório chega à líder, pendente aparece como
   "não respondeu" (tag distinta de recusa explícita).
6. Como líder, pedir remarcação — confirmar aviso claro a todos e reinício da votação.
7. Repetir o passo 1 como um integrante comum e confirmar rejeição (FR-001).

## Definição de pronto para esta feature

- Testes das seções 1-3 verdes.
- `ActionDispatcher` não tem mais nenhum `BotAction` de ensaio apontando para
  `RehearsalService` legado (migração completa) — ou, se parcial, `tasks.md` deixa explícito
  quais `BotAction`s ainda faltam.
- `RehearsalService` legado removido apenas depois que o último `BotAction` for migrado
  (não antes — characterization tests perdem o sentido de existir sem o código que
  caracterizam, mas não apagar até ter certeza de que nada mais depende dele).
