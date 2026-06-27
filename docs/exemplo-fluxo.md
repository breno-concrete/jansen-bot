# Exemplo de Fluxo Completo

Cenário: **Maria** manda `"não posso quinta"` no WhatsApp.

---

## 1. WhatsApp → Evolution API

Maria envia a mensagem pelo WhatsApp. A Evolution API (instância `jansen-banda`) recebe via protocolo WhatsApp Web e dispara um webhook HTTP POST.

**POST** `http://jansen-bot:8080/api/webhook/evolution`

```json
{
  "event": "messages.upsert",
  "instance": "jansen-banda",
  "data": {
    "key": {
      "remoteJid": "5511999990002@s.whatsapp.net",
      "fromMe": false
    },
    "message": {
      "conversation": "não posso quinta"
    },
    "pushName": "Maria"
  }
}
```

---

## 2. Spring Boot — WebhookController

`WebhookController.handleEvolutionWebhook()` recebe o payload:

1. Valida que é `messages.upsert` e `fromMe = false`
2. Extrai telefone `5511999990002` e texto `"não posso quinta"`
3. Delega para `WebhookService` em thread separada (responde 200 OK imediatamente)

---

## 3. WebhookService — Montagem de contexto

`ContextService.buildContext("5511999990002")` lê do Google Sheets:

- **Members:** encontra Maria (guitarra)
- **Rehearsals:** ensaio `r001` status `AGENDADO` para quinta 19h
- **Responses:** respostas anteriores desse ensaio
- **ConversationState:** estado `LIVRE`

Gera JSON de contexto (~membros, ensaios, setlist, respostas).

---

## 4. Claude API — Interpretação NLP

`ClaudeClient.processMessage("não posso quinta", contextJson)` envia:

**System prompt:** instruções do Jansen + contexto JSON da banda

**User message:** `"não posso quinta"`

**Claude retorna:**

```json
{
  "acao": "NEGAR_PRESENCA",
  "resposta": "Beleza Maria, anotei que você não vai na quinta! 👍",
  "dados": {
    "confirmacao": false
  }
}
```

A regra no system prompt mapeia "não posso" → `NEGAR_PRESENCA`.

---

## 5. ActionDispatcher — Execução da ação

`ActionDispatcher.dispatch("5511999990002", action)`:

1. Identifica ação `NEGAR_PRESENCA`
2. Busca ensaio agendado (`r001`)
3. Chama `RehearsalService.registerPresence("r001", "5511999990002", false)`
4. Grava na aba **Responses**:

   | id | rehearsal_id | member_phone | tipo | valor | timestamp |
   |----|--------------|--------------|------|-------|-----------|
   | a3f2 | r001 | 5511999990002 | CONFIRMACAO | NAO | 2026-06-22 14:30:00 |

5. Retorna resposta: `"Show! Te espero no ensaio 🎸"` ou usa a da Claude

---

## 6. Evolution API — Envio da resposta

`EvolutionClient.sendTextMessage("5511999990002", resposta)`:

**POST** `http://evolution-api:8080/message/sendText/jansen-banda`

```json
{
  "number": "5511999990002",
  "text": "Beleza Maria, anotei que você não vai na quinta! 👍"
}
```

Evolution API envia a mensagem de volta ao WhatsApp de Maria.

---

## 7. Resultado final

Maria recebe no WhatsApp:

> Beleza Maria, anotei que você não vai na quinta! 👍

Na planilha Google Sheets, aba **Responses**, consta a negação de presença.

Se alguém perguntar `"quem vai no ensaio?"`, o bot consulta Responses + Members e monta:

```
📋 Presença no ensaio:

✅ Confirmados:
  • João
  • Pedro

❌ Não vão:
  • Maria

⏳ Sem resposta:
  • Ana
  • Lucas
  ...
```

---

## Diagrama do fluxo

```
Maria (WhatsApp)
    │
    ▼
Evolution API ──POST webhook──▶ WebhookController
                                    │
                                    ▼
                              WebhookService
                                    │
                    ┌───────────────┼───────────────┐
                    ▼               ▼               ▼
              Google Sheets    Claude API    ActionDispatcher
              (ler contexto)  (interpretar)  (executar ação)
                    │                               │
                    │               ┌───────────────┘
                    │               ▼
                    │         Google Sheets
                    │         (gravar resposta)
                    │               │
                    └───────────────┼───────────────┐
                                    ▼               │
                              EvolutionClient ◀─────┘
                                    │
                                    ▼
                              Maria (WhatsApp)
```
