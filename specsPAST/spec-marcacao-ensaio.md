# Spec: Marcação e Confirmação de Evento (Ensaio, Culto ou Show)

## 1. Objetivo
Permitir que o admin marque um evento da banda (ensaio, culto ou show — todos com a mesma proposta: tocar), coletar a confirmação individual de cada integrante, e registrar o horário de chegada de cada confirmado durante a janela do evento.

## 2. Comportamento Esperado

**Marcação**
- O admin solicita ao bot, no grupo, que marque um evento — ensaio, culto ou show —, sempre incluindo o tipo, data e horário de início na mensagem.
- O bot interpreta a intenção da mensagem (texto livre, sem comando fixo) com base num conjunto conhecido de intenções (enum) — ex: `MARCAR_EVENTO`, `CHEGUEI`, `EVENTO_ENCERRADO`, `REMARCAR_EVENTO`. Esse enum não precisa ficar fechado agora — cresce conforme o bot ganha novos comandos.
- Só o admin (número de WhatsApp configurado no `.env`) pode marcar evento. Mensagem de qualquer outro número com essa intenção é ignorada/rejeitada.

**Confirmação**
- Após a marcação, o bot envia uma mensagem individual pra cada um dos 8 membros pedindo confirmação de presença, com intervalo de 20 segundos entre cada envio (evita bloqueio por rate limit da Evolution API).
- Confirmação é uma resposta binária: `sim` ou `não`. Qualquer outra resposta não é reconhecida como confirmação nem recusa.
- Se o membro confirmar (`sim`), o evento (com esse membro marcado como confirmado) é salvo no banco de dados.
- Se o membro recusar (`não`), o bot marca esse membro como "não vai" (fica de fora do modo atento daquele evento) e responde confirmando o registro da recusa.
- Se o membro não responder nada até o início do range do modo atento (30 minutos antes do horário marcado), é automaticamente marcado como "não vai".

**Modo Atento (dia do evento)**
- O bot entra em modo atento 30 minutos antes do horário marcado.
- Fica ativo até 3 horas após o horário marcado, **ou até receber a mensagem `EVENTO_ENCERRADO`** — o que vier primeiro. O timestamp dessa mensagem vira a nova deadline; as 3 horas são só o padrão de segurança, caso ninguém mande o encerramento manual.
- Só o admin (mesmo número do `.env` que marca o evento) pode mandar `EVENTO_ENCERRADO`.
- Detecção de chegada: o membro manda a palavra-chave `CHEGUEI` **no chat privado com o bot** (não no grupo). Se estiver dentro do range (30min antes até o fim do modo atento, seja pelas 3h ou pelo encerramento manual), o bot registra o horário.
- Se `CHEGUEI` chegar fora do range, o bot responde avisando que não há evento acontecendo naquele momento (não registra, e não fica em silêncio).

**Remarcação**
- O admin pode remarcar o horário de um evento já marcado; o bot reenvia a mensagem pra todos os membros avisando da remarcação.
- A remarcação reseta as confirmações de todos os membros — cada um precisa confirmar de novo (`sim`/`não`) pro novo horário, seguindo o mesmo fluxo de confirmação inicial.
- Cancelamento total usa o mesmo aviso da remarcação, mas sem esperar resposta do membro — é só uma notificação, não reabre confirmação.

## 3. Dados
- Evento marcado: tipo (ensaio / culto / show), data, horário
- Lista dos membros contatados, com status individual: pendente / confirmado / não vai
- Timestamp da confirmação (ou recusa) de cada membro
- Timestamp da chegada registrada durante o modo atento — este dado alimenta o cálculo de pontualidade da spec **Funcionário do Mês**
- Timestamp de encerramento do modo atento (fixo em +3h por padrão, ou manual via `EVENTO_ENCERRADO`)
- Local **não** entra nos dados — é fixo/implícito, sempre o mesmo lugar.

## 4. Non-goals
- Esta spec não calcula pontos de assiduidade nem decide o funcionário do mês — isso é responsabilidade da spec "Funcionário do Mês", que consome o horário de chegada registrado aqui.

## 5. Critérios de Aceite
- Dado um pedido de marcação com os 8 membros no grupo → bot envia 8 mensagens individuais de confirmação, respeitando 20s de intervalo entre cada uma.
- Dado que um membro responde `sim` → status dele muda pra "confirmado" e o evento passa a existir no banco de dados.
- Dado que um membro responde `não` → status dele muda pra "não vai" e o bot confirma o recebimento da recusa.
- Dado que chega o horário do evento menos 30 minutos → bot passa a aceitar e registrar mensagens de chegada.
- Dado uma mensagem de chegada de um membro confirmado, dentro da janela (30min antes até o fim do modo atento) → registra o horário de chegada daquele membro.
- Dado que a mensagem `EVENTO_ENCERRADO` é recebida antes das 3h padrão → o modo atento encerra naquele timestamp, em vez de esperar as 3h.
- Dado uma mensagem de chegada fora da janela → bot responde avisando que não há evento acontecendo, e não registra nada.
- Dado que o admin remarca um evento já marcado → bot reenvia a mensagem de aviso pra todos os membros contatados originalmente, e reseta o status de todos pra "pendente", disparando o fluxo de confirmação de novo.
- Dado que um membro não responde sim/não até o início do modo atento → seu status muda automaticamente pra "não vai".
- Dado que o admin cancela um evento → bot avisa todos os membros contatados, sem esperar nem exigir resposta.
