# Novas abas do Google Sheets

Adicione estas abas à planilha existente (além de Members, Rehearsals, Setlist, Responses, ConversationState).

---

## Aba: Shows

| Coluna | Tipo | Descrição | Exemplo |
|--------|------|-----------|---------|
| A: id | texto | Identificador único | sh001 |
| B: nome | texto | Nome do show | Show Bar do Zé |
| C: data | texto | Data (yyyy-MM-dd ou dd/MM/yyyy) | 2026-08-15 |
| D: local | texto | Local do show | Bar do Zé |
| E: horario | texto | Horário (HH:mm) | 21:00 |
| F: criado_em | texto | Timestamp de criação | 2026-06-22 14:30:00 |

**Linha 1:** `id | nome | data | local | horario | criado_em`

---

## Aba: ShowReminders

| Coluna | Tipo | Descrição | Exemplo |
|--------|------|-----------|---------|
| A: id | texto | Identificador único | sr001 |
| B: show_id | texto | ID do show | sh001 |
| C: tipo | texto | 3dias ou 1dia | 3dias |
| D: enviado | boolean | Lembrete já enviado | TRUE |
| E: enviado_em | texto | Quando foi enviado | 2026-08-12 09:00:00 |

**Linha 1:** `id | show_id | tipo | enviado | enviado_em`

---

## Aba: Arrivals

| Coluna | Tipo | Descrição | Exemplo |
|--------|------|-----------|---------|
| A: id | texto | Identificador único | ar001 |
| B: member_phone | texto | Telefone do membro | 5511999990002 |
| C: rehearsal_id | texto | ID do ensaio | r001 |
| D: horario_chegada | texto | Hora da chegada (HH:mm) | 19:32 |
| E: data | texto | Data (yyyy-MM-dd) | 2026-06-28 |

**Linha 1:** `id | member_phone | rehearsal_id | horario_chegada | data`

---

## Aba: MemberOfMonthVotes

| Coluna | Tipo | Descrição | Exemplo |
|--------|------|-----------|---------|
| A: id | texto | Identificador único | mv001 |
| B: voter_phone | texto | Quem votou | 5511999990002 |
| C: voted_phone | texto | Em quem votou | 5511999990004 |
| D: mes | número | Mês (1-12) | 7 |
| E: ano | número | Ano | 2026 |

**Linha 1:** `id | voter_phone | voted_phone | mes | ano`

---

## Aba: MonthlyReport

| Coluna | Tipo | Descrição | Exemplo |
|--------|------|-----------|---------|
| A: id | texto | Identificador único | mr001 |
| B: mes | número | Mês do relatório | 7 |
| C: ano | número | Ano | 2026 |
| D: gerado_em | texto | Timestamp | 2026-08-01 08:00:00 |
| E: dados_json | texto | JSON com relatórios | {"pontualidade":"..."} |

**Linha 1:** `id | mes | ano | gerado_em | dados_json`

---

## Atualização da aba Setlist

Adicione as colunas F, G, H, I:

| Coluna | Tipo | Descrição | Exemplo |
|--------|------|-----------|---------|
| F: status | texto | setlist, sugerida, aprovada, rejeitada | aprovada |
| G: link | texto | Link YouTube/Spotify | https://... |
| H: suggested_by | texto | Telefone de quem sugeriu | 5511999990002 |
| I: descricao | texto | Descrição da sugestão | combina com nosso estilo |

**Linha 1 atualizada:** `id | rehearsal_id | musica | ordem | artista | status | link | suggested_by | descricao`

---

## Estados de ConversationState (coluna B)

Valores adicionais:
- `AGUARDANDO_CONFIRMACAO_SHOW`
- `VOTACAO_MEMBRO_ABERTA`
- `VOTACAO_MEMBRO_MES`
- `AGUARDANDO_APROVACAO_MUSICA`
