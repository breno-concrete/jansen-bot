# Funcionário do Mês

## 1. Objetivo
Eleger mensalmente um "funcionário do mês" com base na assiduidade dos membros da banda, e comunicar o resultado ao grupo.

## 2. Comportamento Esperado
- No dia 1 de cada mês, o sistema calcula a pontuação de assiduidade individual do mês anterior.
- Só existem 3 categorias de evento: **ensaio**, **culto**, **evento**.
- Fórmula de pontos por evento:
  - Ensaio (no horário): 2 pontos
  - Ensaio, porém com atraso maior que 20 minutos: 1 ponto
  - Culto: 3 pontos
  - Evento: 4 pontos
- Regra de atraso (>20min = pontuação reduzida) vale só pra ensaio — culto e evento não têm variante de atraso, por decisão de design (só ensaio tem horário rígido cobrado).
- Critério de desempate, em ordem:
  1. Maior pontuação de assiduidade no mês.
  2. Se empate, menor número de eventos com atraso no mês (quanto menos atrasos, mais pontual).
  3. Se empate ainda, quem já ganhou mais vezes "funcionário do mês" no histórico.
- O resultado só é revelado no dia 1 do mês seguinte (nunca antes).

## 3. Dados
Por pessoa, por mês:
- Pontos de assiduidade acumulados (numérico)
- Registro individual por evento (data, tipo de evento — ensaio/culto/evento, horário de chegada, pontos ganhos naquele evento) — salvo por evento, e depois somado pra formar o histórico mensal e o total acumulado.
- Quantidade de eventos com atraso no mês (usado no desempate)
- Quantidade de prêmios "funcionário do mês" vencidos (histórico acumulado, não zera por mês)

## 4. Non-goals
- Não pode haver mais de um ganhador no mesmo mês.
- Não envia lembrete automático antes do ensaio (fora de escopo por ora).
- Não revela ranking parcial durante o mês.

## 5. Critérios de Aceite
- Dado um mês com 1º e 2º lugar com pontuação diferente → sistema anuncia o 1º sem entrar no desempate.
- Dado um empate na pontuação, mas pontualidade diferente → desempata pelo mais pontual.
- Dado empate em pontuação E pontualidade → desempata por quem tem mais prêmios no histórico.
- Mensagem de resultado só é enviada no dia 1 do mês seguinte, nunca antes.
