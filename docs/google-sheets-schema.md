# Schema do Google Sheets

Crie uma planilha no Google Sheets e adicione as 5 abas abaixo com os cabeçalhos exatos.

## Aba: Members

| Coluna | Tipo   | Descrição                          | Exemplo           |
|--------|--------|------------------------------------|-------------------|
| A: id  | texto  | Identificador único                | m001              |
| B: nome| texto  | Nome do membro                     | João              |
| C: telefone | texto | WhatsApp com DDI (sem +)      | 5511999999999     |
| D: instrumento | texto | Instrumento/função           | Guitarra          |
| E: ativo | boolean | Membro ativo na banda          | TRUE              |
| F: admin | boolean | Pode agendar/broadcast/setlist | FALSE             |

**Linha 1 (cabeçalho):** `id | nome | telefone | instrumento | ativo | admin`

**Dados de exemplo (10 membros):**

```
m001 | João    | 5511999990001 | Vocal        | TRUE | TRUE
m002 | Maria   | 5511999990002 | Guitarra     | TRUE | FALSE
m003 | Pedro   | 5511999990003 | Baixo        | TRUE | FALSE
m004 | Ana     | 5511999990004 | Bateria      | TRUE | FALSE
m005 | Lucas   | 5511999990005 | Teclado      | TRUE | FALSE
m006 | Carla   | 5511999990006 | Violão       | TRUE | FALSE
m007 | Rafael  | 5511999990007 | Sax          | TRUE | FALSE
m008 | Julia   | 5511999990008 | Backing vocal| TRUE | FALSE
m009 | Bruno   | 5511999990009 | Guitarra     | TRUE | FALSE
m010 | Fernanda| 5511999990010 | Percussão    | TRUE | FALSE
```

---

## Aba: Rehearsals

| Coluna | Tipo    | Descrição                              | Exemplo                |
|--------|---------|----------------------------------------|------------------------|
| A: id  | texto   | Identificador único                    | r001                   |
| B: data_hora | texto | Data/hora do ensaio (yyyy-MM-dd HH:mm) | 2026-06-28 19:00       |
| C: local | texto | Endereço ou nome do local            | Estúdio Central        |
| D: status | texto | PROPOSTO, VOTACAO, AGENDADO, CANCELADO, REALIZADO | AGENDADO |
| E: opcoes_voto | texto | Opções separadas por \|           | quinta\|sábado\|domingo |
| F: vencedor | texto | Data vencedora da votação          | 2026-06-28 19:00       |
| G: lembrete_enviado | boolean | Lembrete 24h já enviado    | FALSE                  |

**Linha 1:** `id | data_hora | local | status | opcoes_voto | vencedor | lembrete_enviado`

---

## Aba: Setlist

| Coluna | Tipo   | Descrição                    | Exemplo    |
|--------|--------|------------------------------|------------|
| A: id  | texto  | Identificador único          | s001       |
| B: rehearsal_id | texto | ID do ensaio          | r001       |
| C: musica | texto | Nome da música             | Wonderwall |
| D: ordem | número | Ordem na setlist           | 1          |
| E: artista | texto | Artista/banda              | Oasis      |

**Linha 1:** `id | rehearsal_id | musica | ordem | artista`

---

## Aba: Responses

| Coluna | Tipo   | Descrição                              | Exemplo            |
|--------|--------|----------------------------------------|--------------------|
| A: id  | texto  | Identificador único                    | resp001            |
| B: rehearsal_id | texto | ID do ensaio                    | r001               |
| C: member_phone | texto | Telefone do membro              | 5511999990002      |
| D: tipo | texto | CONFIRMACAO ou VOTO                    | CONFIRMACAO        |
| E: valor | texto | SIM/NAO (confirmação) ou data (voto)  | NAO                |
| F: timestamp | texto | Quando respondeu (yyyy-MM-dd HH:mm:ss) | 2026-06-22 14:30:00 |

**Linha 1:** `id | rehearsal_id | member_phone | tipo | valor | timestamp`

---

## Aba: ConversationState

| Coluna | Tipo   | Descrição                         | Exemplo                          |
|--------|--------|-----------------------------------|----------------------------------|
| A: member_phone | texto | Telefone do membro         | 5511999990002                    |
| B: estado | texto | LIVRE, VOTACAO, AGUARDANDO      | LIVRE                            |
| C: contexto_json | texto | JSON com contexto extra  | {"rehearsalId":"r001"}           |
| D: updated_at | texto | Última atualização          | 2026-06-22 14:30:00              |

**Linha 1:** `member_phone | estado | contexto_json | updated_at`

---

## Permissões

Compartilhe a planilha com o email da Service Account (campo `client_email` do JSON de credenciais) com permissão de **Editor**.
