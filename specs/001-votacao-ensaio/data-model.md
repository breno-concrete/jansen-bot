# Data Model: Votação de Ensaio com Quórum e Remarcação

Entidades vivem em `rehearsal/domain` como Java records/classes imutáveis onde possível —
sem anotações de Spring/JPA/Sheets (essas ficam nos adapters, que mapeiam de/para os records
de persistência existentes, `Rehearsal` e `ResponseRecord`).

## Ensaio (aggregate root)

| Campo | Tipo | Regra |
|---|---|---|
| `id` | `String` | gerado por `PhoneUtils.generateId()`, como hoje |
| `dataHora` | `String`/`LocalDateTime` | data/hora do ensaio (decidida pela líder, sem votação de datas — spec não pede múltiplas opções) |
| `local` | `String` | default `"A definir"` se não informado (comportamento já existente, preservar) |
| `tipo` | enum `TipoEnsaio`: `VOCAL`, `INSTRUMENTAL`, `GERAL` | obrigatório (FR-019); define quem é convocado, quem pode votar (FR-020) e o universo do quórum (FR-018). No banco: `ensaio.tipo VARCHAR(32) NOT NULL` (migration V3) |
| `criadoEm` | `Instant` (via `ClockPort`) | usado para calcular o prazo de 12h (FR-007) |
| `prazoVotacaoEm` | `Instant` | `criadoEm + 12h`; **reiniciado a cada remarcação** (FR-017) |
| `status` | enum `VOTACAO_ABERTA`, `ENCERRADA` | ver transições abaixo |
| `decisaoFinal` | enum `PENDENTE`, `CONFIRMADO`, `CANCELADO` | setado pela líder após receber o relatório (FR-011/FR-012) |
| `historicoRemarcacoes` | `List<Remarcacao>` | cada remarcação = novo `dataHora` + timestamp (FR-013/FR-014) |

**Transições de `status`**:
- `VOTACAO_ABERTA → ENCERRADA`: quando todos os integrantes elegíveis votaram (FR-006) **ou**
  quando `prazoVotacaoEm` é atingido (FR-007) — o que ocorrer primeiro.
- `ENCERRADA → VOTACAO_ABERTA` (nova instância lógica): ao remarcar (FR-013/FR-014), reinicia
  todos os `Voto` para pendente e recalcula `prazoVotacaoEm` a partir do momento da
  remarcação (FR-017), mantendo o mesmo `id` de `Ensaio` (o histórico é o que muda, não a
  identidade do ensaio).
- Voto recebido com `status = ENCERRADA` é descartado sem reabrir a votação (FR-016).

## Voto (value object)

| Campo | Tipo | Regra |
|---|---|---|
| `integranteId` | `String` (telefone normalizado) | — |
| `ensaioId` | `String` | referência ao `Ensaio` |
| `escolha` | enum `SIM`, `NAO`, `NAO_RESPONDEU` | `NAO_RESPONDEU` só é atribuído pelo sistema ao encerrar por prazo (FR-009), nunca escolhido pelo integrante |
| `respondidoEm` | `Instant` \| `null` | `null` enquanto pendente |

Qualquer resposta que não seja reconhecível como "sim"/"não" **não** cria um `Voto` — o
integrante continua pendente (FR-004, Edge Cases).

## RelatorioVotacao (value object, calculado — não persistido diretamente)

| Campo | Tipo |
|---|---|
| `ensaioId` | `String` |
| `totalIntegrantesElegiveis` | `int` (calculado do cadastro vigente, não fixo — FR-018; exclui a líder — ver Assumption da spec; exclui "projeção" como já faz `RehearsalService.checkAllResponded`) |
| `confirmados`, `recusados`, `naoRespondeu` | `int` (soma = `totalIntegrantesElegiveis`, invariante validada no construtor — FR-018) |
| `percentualSim` | derivado, apenas para exibição (a decisão de quórum usa `RegraDeQuorum`, não este campo — ver `research.md` D6) |
| `abaixoDoQuorum` | `boolean` |

## Integrante (papel, não uma nova entidade de persistência)

Não é uma tabela/aba nova — é a leitura de `Member` (já existente) mais o papel derivado de
`AppProperties.getAdminPhones()` (ver `research.md` D3): `LIDER` se o telefone normalizado
está na lista, `MEMBRO` caso contrário. `Integrante` no domínio é só essa combinação,
mapeada pelo adapter a partir de `Member` + `AppProperties`.

## Mapeamento para persistência

Persistência em Postgres via JPA/Flyway, no adapter (ver `research.md` D4). O domínio não
tem anotações JPA; o `PostgresRehearsalAdapter` mapeia `Ensaio` de/para uma entidade JPA.

- `Ensaio` (id, dataHora, local, tipo, criadoEm, prazoVotacaoEm, status, decisaoFinal) mapeia para
  uma tabela própria; `historicoRemarcacoes` (novo `dataHora` + timestamp) para uma tabela
  filha. Nomes de tabela/coluna e tipos são decididos na migration Flyway da T013.
- `Voto` é persistido na tabela `voto` (migration V2, T022): colunas `ensaio_id` (FK para
  `ensaio`, `ON DELETE CASCADE`), `integrante_id`, `escolha` e `respondido_em` (nulo enquanto
  pendente), com chave primária `(ensaio_id, integrante_id)`. `NAO_RESPONDEU` é atribuído
  pelo sistema ao encerrar por prazo (FR-009); se ele é gravado ou só calculado no relatório
  fica para a T030.
- O legado (`Rehearsal`/`ResponseRecord` no Google Sheets) segue existindo enquanto o Strangler
  Fig (D2) não terminar; os dois não devem coexistir para o mesmo `BotAction` (ver contratos).
