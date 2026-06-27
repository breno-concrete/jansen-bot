# Exemplos de fluxo — Novas features

---

## Feature 1: Cadastro de show

**Admin:** "show dia 15/08 às 21h no Bar do Zé"

1. Evolution → WebhookController → WebhookService
2. ContextService monta contexto (shows existentes, estado=LIVRE)
3. Claude → `{ "acao": "CADASTRO_SHOW", "dados": { "show_data": "15/08/2026", "show_horario": "21:00", "show_local": "Bar do Zé" } }`
4. ActionDispatcher → ShowService.requestShowRegistration()
5. Salva ConversationState: `AGUARDANDO_CONFIRMACAO_SHOW` + JSON pendente
6. Resposta: "Vou cadastrar: Show no Bar do Zé, 15/08 às 21:00. Confirma?"

**Admin:** "sim"

7. Claude → `CONFIRMAR_SHOW`
8. ShowService.confirmShowRegistration() → grava aba **Shows**
9. Resposta: "Show cadastrado! 🎉"

**Scheduler (09h diário):** ShowReminderScheduler verifica shows a 3 dias e 1 dia → envia lembretes → grava **ShowReminders**.

---

## Feature 2: Registro de chegada

**Maria:** "cheguei"

1. Claude → `REGISTRAR_CHEGADA`
2. ArrivalService.registerArrival()
3. Busca ensaio AGENDADO de hoje em **Rehearsals**
4. Se não houver: "Não tem ensaio hoje, Maria 😄"
5. Se houver: grava **Arrivals** (phone, rehearsal_id, 19:32, data)
6. Resposta: "✅ Chegada registrada às 19:32, Maria!"

**Scheduler (dia 1º, 08h):** MonthlyReportScheduler → ArrivalService.buildPunctualityRanking() → broadcast do ranking.

---

## Feature 3: Alertas de faltas

**Scheduler (22h diário):** AbsenceCheckScheduler

1. Busca últimos 2 ensaios REALIZADO
2. Para cada membro ativo, verifica **Arrivals**
3. Se faltou nos 2: mensagem PRIVADA ao admin
   "⚠️ Pedro faltou os últimos 2 ensaios consecutivos (20/06 e 27/06)."

**Relatório mensal (dia 1º, 08h):** presença com ✅/⚠️/❌ baseado em % de chegadas vs ensaios realizados.

---

## Feature 4: Pódio de presença

Gerado junto com relatório mensual:

```
🏆 Presença do mês — Julho
🥇 Ana — 10 ensaios
🥈 Carlos — 9 ensaios
🥉 Maria — 8 ensaios
Parabéns aos mais presentes! 👏
```

---

## Feature 5: Membro do mês

**Admin:** "abrir votação membro do mês"

1. Claude → `ABERTURA_VOTACAO_MEMBRO`
2. MemberOfMonthService.openVoting()
3. Admin state → `VOTACAO_MEMBRO_ABERTA`
4. DM para cada membro com lista numerada
5. Cada membro state → `VOTACAO_MEMBRO_MES`

**Maria:** "3"

6. Claude → `VOTAR_MEMBRO_MES`, vote_number=3
7. Grava **MemberOfMonthVotes** (voter=Maria, voted=membro #3)
8. Se já votou: "Você já votou nesta votação!"

**Admin:** "encerrar votação"

9. Contabiliza votos → broadcast vencedor
10. Se empate → alerta privado ao admin

---

## Feature 6: Contador de ensaios

**Admin:** "ensaio concluído"

1. Claude → `CONCLUIR_ENSAIO`
2. RehearsalCounterService.completeRehearsal()
3. Atualiza status → REALIZADO em **Rehearsals**
4. Conta total REALIZADO → broadcast:
   "🎸 Mais um ensaio no bolso! Esse foi o de número 47 da banda."

**Qualquer membro:** "quantos ensaios"

5. Claude → `CONTAR_ENSAIOS`
6. Resposta: "A banda já fez 47 ensaios juntos 🎸"

---

## Feature 7: Sugestão de músicas

**Carlos:** "acho que esse estilo combina https://open.spotify.com/track/xyz"

1. Claude → `SUGERIR_MUSICA` (link + descrição)
2. MusicSuggestionService.suggestMusic()
3. Grava **Setlist** com status=sugerida
4. DM ao admin com detalhes + "APROVAR ou REJEITAR"
5. Admin state → `AGUARDANDO_APROVACAO_MUSICA`
6. Resposta a Carlos: "Sugestão recebida! Vou encaminhar pro João avaliar 🎵"

**Admin:** "APROVAR"

7. Claude → `APROVAR_MUSICA`
8. Atualiza status → aprovada
9. DM a Carlos: "Boa notícia! Sua sugestão foi aprovada 🎉"

**Qualquer membro:** "repertório"

10. Claude → `CONSULTAR_REPERTORIO`
11. Lista músicas status=aprovada com links

---

## Schedulers — resumo

| Job | Cron | Função |
|-----|------|--------|
| ReminderScheduler | `0 0 9 * * *` | Lembrete ensaio 24h antes |
| ShowReminderScheduler | `0 0 9 * * *` | Lembretes show 3d e 1d |
| MonthlyReportScheduler | `0 0 8 1 * *` | Ranking pontualidade + presença + pódio |
| AbsenceCheckScheduler | `0 0 22 * * *` | Alertas faltas consecutivas ao admin |

Todos com timezone `America/Sao_Paulo`.
