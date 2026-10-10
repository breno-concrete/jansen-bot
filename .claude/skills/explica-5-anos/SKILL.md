---
name: "explica-5-anos"
description: "Explica um conceito ou erro técnico em duas camadas: primeiro como se o Breno tivesse 5 anos (analogia do dia a dia, sem jargão), depois a parte técnica ligada ao código real do projeto. Use quando o Breno pedir 'explica como se eu tivesse 5 anos', 'seja mais didático', 'não entendi', ou quando ele estiver estudando algo do plan.md (Docker, testes, deploy, IA)."
argument-hint: "O conceito, erro ou pergunta a explicar (ex: 'por que localhost não funciona no container')."
user-invocable: true
disable-model-invocation: false
---

## User Input

```text
$ARGUMENTS
```

## Objetivo

O Breno está aprendendo a desterceirizar a base (Docker, testes, deploy, IA). O roteiro está no `plan.md`.
A explicação existe para ele **entender e conseguir refazer sozinho**, não para resolver por ele.

## Formato da resposta (sempre nesta ordem)

### 1. Resposta curta (1–2 linhas)
Responda a pergunta direto: sim, não, ou "em parte", e o porquê em uma frase.
Se a premissa da pergunta estiver errada, diga isso aqui, com gentileza e clareza.

### 2. Como se você tivesse 5 anos
- **Uma** analogia do dia a dia (casa, carta, telefone, escola, cozinha...). Escolha uma e mantenha a mesma até o fim, sem misturar metáforas.
- Nenhum jargão técnico nesta seção: nada de "container", "porta", "variável", "rede".
- Frases curtas. No máximo ~8 linhas.
- Se houver várias causas, cada uma vira uma parte da mesma história (ex.: "você escreveu o endereço errado **e** o amigo não mora nessa rua").

### 3. Agora a parte técnica
- Traduza a analogia, peça por peça, para o termo técnico real. Uma tabela "na história → no Docker/Java" funciona bem.
- Ancore no **código real do projeto**: leia o arquivo antes de citar (`application.properties`, `Dockerfile`, `docker-compose.yml`...) e mostre a linha exata, com `caminho:linha`.
- Explique o mecanismo (o que acontece de verdade, em que ordem), não só o nome do conceito.

### 4. Confira se entendeu
Termine com **uma** pergunta ou experimento curto que o Breno possa fazer para provar a si mesmo que entendeu.
O formato é "antes de rodar, preveja o resultado; depois confira".

## Regras

- **Ensine antes de pedir.** Nunca mande o Breno escrever algo cujo conceito ou sintaxe ainda não foram ensinados (nesta conversa ou em `estudo-docker-progresso`). Antes de pedir, mostre um **exemplo mínimo e análogo** (não o código dele, ex.: um `healthcheck` de outro serviço, ou o Dockerfile de brinquedo) com a sintaxe comentada. Só depois peça para ele aplicar no projeto. Pista só vale sobre o que ele já viu.
- **Não entregue a solução pronta** quando a tarefa for dele no `plan.md` (ex.: "escrever sem IA gerar"). Explique o conceito, mostre o exemplo análogo e diga onde olhar, mas não escreva o comando, o Dockerfile ou o compose final por ele.
- Se ele pedir a solução explicitamente, lembre em uma linha que o plano pede para ele escrever. Se ele insistir, entregue.
- Corrija conceitos errados na hora, sem suavizar a ponto de ficar ambíguo.
- Não invente fato técnico. Se não tiver certeza, diga, e sugira como ele pode verificar.
- Português, tom de conversa, sem floreio.
