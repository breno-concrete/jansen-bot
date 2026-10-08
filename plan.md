# Plano de Evolução — 6 meses

> Companion do `CLAUDE.md`. Este arquivo é o roteiro executável.
> Season 1 é detalhada dia a dia, bloco a bloco. Seasons 2 e 3 ficam em nível de
> objetivo e entregável — são redesenhadas quando a Season anterior fecha,
> porque dependem do que ela revelar.
>
> **Revisão de 25/09/2026.** Reescrito para a agenda nova (blocos de manhã,
> ensaio de terça encerrado, noites encurtadas para proteger 8h30 de sono).
> A ordem da Season 1 mudou: testes subiram para as semanas 3–4.
>
> **Revisão de 27/09/2026.** Teste de retenção de 10 minutos em todos os dias.
> A semana 1 foi replanejada a partir de onde você chegou em 27/09 (a semana 1
> original feita até a quinta). O que faltava da semana 2 original foi puxado
> para frente, e a semana 2 passou a ser a stack completa em compose, com a
> Evolution API. As semanas 3 a 8 mantêm as datas.
>
> **Revisão de 29/09/2026.** Regra nova: **metade Docker, metade projeto** (ver
> "Regra de divisão" na Season 1). De quarta 30/09 em diante, a semana 1 está
> escrita em durações, não em horários: você encaixa os blocos na noite ou onde
> der. As semanas 2+ seguem a regra quando forem replanejadas.

---

## O que mudou nesta revisão

1. **O orçamento de horas era outro.** A versão anterior assumia 2h45 corridas à
   noite, de segunda a quinta. A agenda hoje não tem isso: as noites foram
   encurtadas para 21:00 na cama, e em troca existem quatro manhãs de pico que
   antes não existiam.

2. **A ordem fixa diária virou alocação por tipo cognitivo.** Antes: LeetCode →
   teoria → mão na massa, todo dia, dentro de um bloco só. Agora os blocos são
   separados por natureza, e cada tipo de trabalho vai para o horário em que o
   cérebro serve para ele.

3. **Testes subiram para as semanas 3–4.** A versão anterior punha testes nas
   semanas 5–6, com deploy no meio. Mas a semana de teste de integração precisa
   de Docker — e deixar quatro semanas entre uma coisa e outra significa
   reaprender Docker para usá-lo. Agora Docker e testes são blocos vizinhos, e
   deploy vem depois, já com a suíte de testes protegendo o que sobe.

4. **Segunda-feira nunca carrega teoria nova.** Segunda é o único dia útil sem
   bloco de manhã, porque o domingo tem igreja até 22:45 e o sono da noite
   anterior é o mais curto da semana. Segunda executa; não decide.

5. **Sexta de manhã é o bloco mais forte da semana** (2h20 no total) e passa a
   carregar o fechamento do entregável, não sobra.

6. **A trilha "Leve" ganhou endereço.** Antes ela existia sem horário e, na
   prática, competia com o Deep Work. Agora tem lugar fixo fora dele.

---

## Orçamento semanal

| Bloco | Dias | Duração | Natureza |
|---|---|---|---|
| Manhã 05:40–06:30 | Ter, Qua, Qui, Sex | 50min | **Pico** — teoria e raciocínio novo |
| Sexta 07:30–09:00 | Sex | 90min | **Longo** — fecha o entregável da semana |
| Noite A 18:45–19:30 | Seg, Ter, Qua, Qui | 45min | **Baixa fricção** — LeetCode + Anki |
| Noite B 20:00–20:45 | Seg, Ter, Qua, Qui | 45min | **Execução** — 10min de retenção + mão na massa |
| Sábado 08:45–09:45 | Sáb | 60min | LeetCode longo |
| Sábado 13:00–14:00 | Sáb | 60min | Retenção (10min) + margem ou aprofundamento |
| Domingo 13:30–14:45 | Dom | 75min | Ritual (20min) + retenção (10min) + margem |

**Total: 14h05/semana**, sendo **4h10 em horário de pico**. A versão anterior
tinha 10h15 de blocos úteis, todos à noite. São ~4h a mais por semana, e a
parte que mais rende deixou de acontecer com o cérebro em fim de expediente.

### A regra que sustenta o resto

**Raciocínio novo vai para a manhã. Execução já decidida vai para a noite.**

Cérebro cansado executa bem e decide mal. Todo bloco de noite trabalha em cima
de uma decisão que já foi tomada de manhã — por isso o alvo do dia seguinte é
escrito na véspera, uma linha, no papel, antes de apagar a luz. Sem esse papel,
os 50 minutos da manhã viram 20.

### Estrutura de cada bloco

**Manhã (05:40–06:30) — Ter a Sex**
Teoria nova, ou a parte do problema que exige raciocínio inédito. Nunca
LeetCode, nunca vídeo, nunca "ver o que tem pra fazer". Uma coisa só, decidida
na véspera. Ler antes de abrir o editor, nunca durante.

**Noite A (18:45–19:30) — Seg a Qui**
- `18:45–19:10` LeetCode do dia. Um problema, regra fixa, nunca pula. É o
  aquecimento que não depende de energia alta.
- `19:10–19:30` Anki. Virar em card o conceito da manhã — enquanto está fresco,
  não no domingo. Card feito a partir do próprio erro, nunca copiado de material
  pronto.

**Noite B (20:00–20:45) — Seg a Qui**
- `20:00–20:10` Teste de retenção (regra no topo da Season 1).
- `20:10–20:45` Mão na massa na tarefa da semana.

Execução do que a manhã definiu. Se você se
pegar decidindo arquitetura aqui, a manhã falhou — anote isso e corrija amanhã.

**Sexta manhã**
- `05:40–06:30` Raciocínio pesado ou teste de retenção.
- `07:30–07:55` LeetCode.
- `07:55–09:00` Fechamento do entregável da semana. É o único bloco de 65
  minutos corridos que você tem — usa para a coisa que não cabe em 45.

**Segunda**
Sem manhã. Só Noite A + Noite B. Execução pura, tarefa já definida na sexta
anterior ou no domingo. Nunca teoria nova.

### Proporção

LeetCode (~2h05/semana) + mão na massa (~8h) = ~72% do tempo em código.
Teoria (~3h20 de manhã) = ~24%. Anki e ritual fecham o resto. A teoria pesa um
pouco mais que na versão anterior, de propósito: ela agora acontece no horário
em que gruda, então rende mais por minuto investido.

Desde 27/09, cerca de 1h por semana (10min por dia) vai para o teste de
retenção. Ele sai da mão na massa e da margem, sem aumentar o total de horas.
Continua sendo tempo em código, porque o teste é refazer o trabalho de memória.

---

## Onde a trilha "Leve" acontece

Nunca dentro de um bloco de Deep Work. Ela tem endereço próprio:

- **Leitura** (*The Mom Test* e os que vierem): trajeto de volta, bloco
  "Faculdade → Casa (Livro)", ~1h15 por dia útil.
- **Conversas de descoberta com barbearias**: sábado 10:30–12:00 (Livre) ou
  domingo 15:30–17:30 (Free). São os dois blocos da semana que já são livres e
  caem em horário comercial de barbearia.
- **Escrita de síntese e veredito**: domingo, na margem depois do ritual.

Se numa semana a trilha Leve não couber, ela escorrega — o Deep Work não.

---

## Trilha paralela: Inglês falado

Custo zero de Deep Work. Roda em tempo que hoje não produz nada:

- **06:30–07:30, todo dia útil** (a hora de se arrumar e comer): podcast técnico
  em inglês, sem legenda, repetindo em voz alta as frases que não pegou. 5h/semana.
- **Trajeto de volta, 30min**: gravar áudio para si mesmo, em inglês, explicando
  o que resolveu no estágio naquele dia.

Isso existe porque B2 lê documentação bem e trava numa entrevista de system
design de 45 minutos. Entre o salário de hoje e o alvo, esse é o item de maior
retorno por hora — e não custa um minuto de código.

---

# Season 1 · 26/09 – 22/11 — Desterceirizar a base + validar demanda

Objetivo: sair de "a IA faz por mim" em Docker, testes, deploy e IA, e chegar ao
fim com a demanda das barbearias **decidida** (validada, não validada ou
pivotar) e a spec do MVP pronta para construir na Season 2.

| Semana | Datas | Tema | Entregável |
|---|---|---|---|
| 0 | 26–27/09 | Setup | ✅ Feito. Adiantou a semana 1 original até a quinta |
| 1 | 28/09–04/10 | Docker no projeto real + US1 andando | Dockerfile multi-stage, banco com volume, README + T022D auditada e T022E escrita por você |
| 2 | 05–11/10 | Stack completa em compose | `docker compose up` sobe bot + banco + Evolution do zero e o bot responde no WhatsApp |
| 3 | 12–18/10 | Testes de unidade do zero | Suíte escrita por você, verde |
| 4 | 19–25/10 | Testes de integração | Testcontainers rodando, unidade separada de integração |
| 5 | 26/10–01/11 | Deploy real, parte 1 | Algo seu no ar, acessível por outra pessoa |
| 6 | 02–08/11 | Deploy real, parte 2 | Domínio + HTTPS + restart automático, documentado |
| 7 | 09–15/11 | Primeiro IA em código | Serviço seu que faz chamada de LLM estruturada |
| 8 | 16–22/11 | RAG mínimo + spec | RAG funcionando + spec de 1 página do MVP |

---

## Regra de divisão: metade Docker, metade projeto

Vale de 30/09 em diante. Todo dia tem duas metades de **tempo igual**:

1. **Docker (ou o tema da semana):** teoria, retenção e prática do jeito que a
   semana descreve, sempre no bot da banda, nunca em projeto de brinquedo.
2. **Projeto:** marcar checkboxes do `specs/001-votacao-ensaio/tasks.md`, **na
   ordem do arquivo**, sem pular dependência. Você escreve, Claude audita
   (`/revisa-meu-codigo`: plano → testes Red → implementação Green). A task só
   vira `[x]` depois da auditoria aprovada e da suíte inteira verde.

O dia é escrito em **durações, não em horários**. Encaixe os blocos onde der
(noite, direto, fim de semana). O que não cabe no dia passa para o dia seguinte
**na mesma metade**: projeto atrasado não come tempo de Docker, e vice-versa.

LeetCode e Anki ficam fora das duas metades, como extras. Se o dia apertar, eles
são os primeiros a sair.

---

## Teste de retenção diário

Vale para **todos os dias** da Season 1, inclusive sábado e domingo. Dura 10
minutos. É a primeira coisa do bloco de mão na massa, antes de começar a tarefa
nova.

**Como fazer:**
1. Sem olhar nada (arquivo, histórico do terminal, chat, anotação), refazer do
   zero o que foi feito na sessão anterior: reescrever o arquivo ou o comando, ou
   explicar o conceito em voz alta como se fosse para outra pessoa.
2. Cronometrar.
3. Só então abrir o original e comparar.
4. Anotar no caderno onde hesitou ou errou, de forma específica ("pus a imagem
   antes do `-e`"), nunca vaga ("preciso revisar Docker").
5. O que você errou vira card de Anki na Noite A do dia seguinte.

Se acertar o mesmo item três dias seguidos sem hesitar, ele sai da rotação e
entra o próximo. O alvo de cada dia já está escrito no cronograma abaixo.

**Onde acontece:**

| Dia | Horário | Observação |
|---|---|---|
| Seg a Qui | `20:00–20:10` | Início da Noite B. A execução passa para `20:10–20:45` |
| Sexta | Início do bloco de raciocínio ou do bloco longo | Vira a **retenção semanal**: refazer do zero o entregável principal da semana, não só o da véspera |
| Sábado | `13:00–13:10` | Início da margem |
| Domingo | `13:50–14:00` | Logo depois do ritual |

---

## Semana 0 — Setup · 26–27/09 ✅

Fechada, e passou do alvo. Em 26–27/09 você já fez o que a semana 1 original
previa até a quinta:

- Dockerfile escrito à mão (`FROM`, `WORKDIR`, `COPY`, `EXPOSE`, `ENTRYPOINT`),
  com comentários. Dois comentários ainda estão errados e são corrigidos na
  segunda.
- `docker build` e `docker run` funcionando. Uma imagem gera vários containers,
  e isso foi provado com dois `docker run`.
- Três erros reais anotados e resolvidos, cada um com previsão antes de rodar:

| Erro | Causa | Correção |
|---|---|---|
| `Connection refused` em `localhost:5432` | Sem `BOT_DB_URL`, o Spring usou o padrão; `localhost` no container é o próprio container | `-e BOT_DB_URL=...postgres-bot...` |
| `UnknownHostException: postgres-bot` | O bot estava na rede `bridge` padrão, que não resolve nome, e o Postgres em outra rede | `--network jansen-bot_default` |
| (evitado) senha vazia | O padrão de `BOT_DB_PASSWORD` é vazio; o Postgres espera `postgres` | `-e BOT_DB_PASSWORD=postgres` |

- O bot ficou de pé, conectado ao `postgres-bot` **pelo nome do serviço**.

Conferir no ritual de domingo: LeetCode #1–3 e o deck de Anki "Docker" foram
feitos? Se não, entram na margem de sábado 03/10.

---

## Semana 1 — Docker no projeto real + US1 andando · 28/09–04/10

**Duas frentes, um projeto só.** Todo exercício de Docker desta semana é feito
no bot da banda, com os dados do spec-001 (ensaio, voto, tipo). Em paralelo, o
spec-001 anda na ordem do `tasks.md`: **T022D** (já escrita, você audita),
**T022E** (você escreve do zero) e, se sobrar tempo no domingo, o plano da
**T022F**. Desde 30/09 o projeto tem metade do tempo de cada dia (ver "Regra de
divisão").

**Como as tasks do projeto andam: você escreve, Claude audita
(`/revisa-meu-codigo`).** Escrever sozinho fixa mais, e custa mais tempo. Cada
task passa por três auditorias,
para o erro ser pego cedo:
1. **Plano** (5 min): em 3–5 linhas, quais FR a task cobre, quais testes você vai
   escrever e quais arquivos vai tocar. `/revisa-meu-codigo T022E plano`.
2. **Testes (Red):** escrever os testes, ver falharem pelo motivo certo.
   `/revisa-meu-codigo T022E testes`.
3. **Implementação (Green):** fazer passar, com a suíte inteira verde.
   `/revisa-meu-codigo T022E implementação`.

A auditoria aponta o problema e dá uma pista. A correção é sua. Código pronto só
se você pedir, ou depois de duas tentativas sem sair do lugar.

A exceção desta semana é a **T022D**: o código já existe no repositório (adapter
e 4 testes), escrito antes deste plano. Nela os papéis se invertem, e você
audita. Só aprove quando souber dizer qual FR cada teste cobre e explicar cada
método com suas palavras.

**Raça, sim; sono, não.** Esta semana usa cada minuto dos blocos, inclusive as
margens de sábado e domingo. O que ela não usa é a noite depois das 20:45.
Cortar sono para render mais é a falha silenciosa que derruba o plano inteiro
na terceira semana.

**Preparar no domingo 27/09, antes de dormir (5 min):**
- Deixar o Docker Desktop configurado para abrir junto com o Windows.
- Escrever no papel o alvo de segunda: "`--env-file` + comentários do Dockerfile".

---

**Segunda 28/09** *(sem manhã — execução pura)* · ✅ parcial: comentários do
Dockerfile corrigidos; o `--env-file` ficou para terça e foi feito lá.
- `18:45–19:10` LeetCode #4 Group Anagrams.
- `19:10–19:30` Anki: 4 cards de 27/09 — Dockerfile vs imagem vs container;
  opções do `docker run` antes da imagem (o que vem depois vira argumento do
  `ENTRYPOINT`); rede `bridge` padrão sem DNS vs rede criada com DNS;
  `Connection refused` vs `UnknownHostException`.
- `20:00–20:10` **Retenção:** sem olhar, reescrever o `docker run` que subiu o
  bot em 27/09 (rede, as duas variáveis, `--rm`, imagem por último).
- `20:10–20:35` Trocar os `-e` por `--env-file .env`. Primeiro, colocar no `.env`
  as variáveis que o bot lê (`BOT_DB_URL`, `BOT_DB_USER`, `BOT_DB_PASSWORD`).
  Hoje ele só tem `POSTGRES_*`, que o bot ignora.
- `20:35–20:45` Reescrever com suas palavras os comentários do `COPY` (copia um
  arquivo, não uma pasta) e do `ENTRYPOINT` (roda no `docker run`, não no build).
- **Pronto quando:** o bot sobe com `--env-file` na rede `jansen-bot_default`, e
  os cinco comentários do Dockerfile estão corretos.
- **Erro comum:** colocar no `.env` o `BOT_DB_URL` com `localhost`. Dentro do
  container, `localhost` é o próprio container: é o erro 1 de 27/09 de novo.

**Terça 29/09**
- `05:40–06:30` Teoria: porta publicada vs rede interna. Por que o bot acha o
  banco em `postgres-bot:5432` e você, do Windows, acha o mesmo banco em
  `localhost:5432`. O que o `ports: "5432:5432"` do compose faz de verdade, e o
  que acontece se outro Postgres na sua máquina já estiver usando a 5432.
- `18:45–19:10` LeetCode #5 Top K Frequent Elements.
- `19:10–19:30` Anki: porta publicada vs rede interna (um card com a frase "de
  dentro da rede é o nome do serviço; de fora é localhost + porta publicada").
- `20:00–20:10` **Retenção:** explicar em voz alta a diferença entre a rede
  `bridge` padrão e uma rede criada (o "cachorro chamado Cachorro"). Escrever o
  comando que lista as redes e o que mostra quem está dentro de cada uma.
- `20:10–20:45` Conectar no `postgres-bot` pelo DBeaver ou `psql` e **ler o
  banco do projeto**: `flyway_schema_history` (quais migrations rodaram: V1, V2,
  V3?), a tabela de ensaio (onde está a coluna `tipo` da V3?) e a de voto.
  Comparar o que você vê com o `data-model.md` do spec-001.
- **Pronto quando:** você sabe dizer quais migrations rodaram e em que tabela e
  coluna o tipo do ensaio (VOCAL/INSTRUMENTAL/GERAL) fica guardado.
- ✅ **Feito em 29/09** (sem LeetCode e sem Anki; `--env-file` de segunda
  incluído). V1, V2 e V3 rodaram; o tipo fica em `ensaio.tipo VARCHAR(32) NOT
  NULL`. Achados: o `data-model.md` está desatualizado (virou **P-028**, resolve
  com a T022E), e a V3 rodou no banco sem estar no git. Erros para o caderno:
  `docker ps` não tem `--network`; faltou o `run`; entrou no banco `postgres` em
  vez de `jansenbot`.

**A partir daqui, durações em vez de horários** (regra de 29/09). Cada dia tem
a metade **Docker** e a metade **Projeto**, com o mesmo tempo. LeetCode e Anki
são extras, fora das metades.

**Quarta 30/09** · Docker ~1h35 · Projeto ~1h35
- **Docker**
  - `50 min` Teoria: volumes. Bind mount vs named volume; o que persiste e o
    que evapora quando o container morre. E o contraste que fecha o raciocínio:
    o **Testcontainers** faz o oposto de propósito, com um banco descartável que
    nasce e morre a cada teste. Por que um teste quer isso e a produção não?
  - `10 min` **Retenção:** sem olhar, escrever como conectar no Postgres de
    fora (host, porta, usuário, banco) e explicar por que o host é `localhost` e
    não `postgres-bot`. Bônus: o comando `psql` de dentro do container, e por
    que ali não precisa de host nem porta.
  - `20 min` Volume nomeado no `postgres-bot`. Inserir uma linha de ensaio à
    mão, rodar `docker compose down` e `up`, e conferir se ela continua lá.
  - `15 min` Rodar o `PostgresRehearsalAdapterTest` e, **em outro terminal ao
    mesmo tempo**, rodar `docker ps` algumas vezes. Ver o container do
    Testcontainers nascer, o teste rodar e o container sumir.
  - **Pronto quando:** a linha de ensaio sobrevive ao `down`/`up`, e você viu
    com os próprios olhos o container efêmero do teste aparecer e desaparecer.
- **Projeto: T022D, parte 1 (você audita)**
  - `15 min` Commitar o que já está `[x]` (T022A–T022C). A V3, o
    `TipoEnsaio.java` e as mudanças no service ainda estão fora do git: hoje só
    existem na sua máquina (descoberto em 29/09). Use o `git status` para separar
    o que é da T022A–C do que é da T022D (adapter e teste dela ficam para
    amanhã). Rode `./mvnw test` antes de commitar.
  - `80 min` Ler o `SheetsIntegranteAdapter` e os 4 testes. Para cada teste,
    escrever qual FR ele cobre (FR-003, FR-018, FR-019) e explicar cada método
    do adapter com suas palavras.
  - **Pronto quando:** você tem, no papel, uma linha "teste → FR" para os 4
    testes, e uma lista do que não entendeu ou achou suspeito.
- **Extras:** LeetCode #6 🔺 Product of Array Except Self · Anki: bind mount vs
  named volume; banco persistente (produção) vs banco descartável (teste).

**Quinta 01/10** · Docker ~1h35 · Projeto ~1h35
- **Docker**
  - `50 min` Teoria: multi-stage build. Por que buildar com JDK e rodar com
    JRE em estágios separados encolhe a imagem, e por que isso acaba com o passo
    manual de rodar o `mvn package` antes do `docker build`. Pergunta-guia: o
    que o `COPY --from` copia, e de onde?
  - `10 min` **Retenção:** explicar o que acontece com os dados num `down` e
    num `down -v`. Escrever onde o volume é declarado no compose (nos dois
    lugares).
  - `35 min` Reescrever o Dockerfile em multi-stage. Anotar o tamanho da imagem
    antes (`docker images`) e depois.
  - **Pronto quando:** a imagem nova builda **sem** você rodar `mvn package`
    antes, e você anotou o antes e o depois em MB.
  - **Erro comum:** o build do Maven dentro do Docker roda os testes, e o teste
    do Testcontainers precisa de Docker, que não existe dentro do build. Se o
    build travar ou falhar nesse teste, descubra por quê antes de pular os
    testes.
- **Projeto: T022D, parte 2 + T022E plano**
  - `50 min` Rodar os testes da T022D. Quebrar de propósito uma regra do adapter
    (ex.: deixar passar "projeção") e ver qual teste pega. Desfazer. Levar a
    lista de suspeitas de ontem para `/revisa-meu-codigo T022D`. Aprovar, marcar
    `[x]` no `tasks.md` e commitar.
  - `45 min` T022E, plano: ler FR-020 a FR-022 no `spec.md` e escrever, em 3–5
    linhas, quais FR a task cobre, quais testes você vai escrever e quais
    arquivos vai tocar. `/revisa-meu-codigo T022E plano`.
  - **Pronto quando:** T022D `[x]` e commitada; plano da T022E auditado.
- **Extras:** LeetCode #7 🔺 Longest Consecutive Sequence · Anki: multi-stage (o
  que fica no estágio de build e o que vai para a imagem final).

**Sexta 02/10** · Docker ~1h20 · Projeto ~1h20
- **Docker**
  - `50 min` **Retenção semanal.** Tirar da vista o Dockerfile multi-stage e o
    comando `docker run` completo. Reescrever os dois do zero, cronometrando.
    Comparar com o original e anotar cada hesitação.
  - `30 min` README curto no repo: comando de build, comando de run (com
    `--env-file` e `--network`) e, separado, onde você hesitou na retenção
    semanal, de forma específica.
  - **Pronto quando:** o Dockerfile reescrito builda igual ao original, e outra
    pessoa conseguiria rodar o container só lendo o README.
- **Projeto: T022E, testes (Red)**
  - `15 min` Resolver a **P-028** do `pendencias.md`: acrescentar o `tipo` na
    seção Ensaio do `data-model.md` e corrigir a linha 66 (a tabela `voto` já
    existe, V2). Você escreve, Claude revisa.
  - `10 min` Atualizar a assinatura de `registrarVoto` no contrato
    `contracts/rehearsal-ports.md` **antes** do código, como a task pede.
  - `55 min` Escrever os testes no `RehearsalVotingServiceTest`: (a) só
    convocado do tipo vota, líder e não convocados ignorados em silêncio
    (FR-020); (b) com mais de um ensaio pendente, a resposta só vale se disser o
    tipo, senão o bot pergunta (FR-021, FR-022). Vê-los falhar.
    `/revisa-meu-codigo T022E testes`.
  - **Pronto quando:** P-028 resolvida; existe um teste para cada regra (a) e
    (b), todos vermelhos pelo motivo certo, e a auditoria dos testes não tem
    item grave aberto.
- **Extras:** LeetCode #8 Valid Palindrome.

**Sábado 03/10** · Docker ~1h · Projeto ~1h
- **Docker**
  - `10 min` **Retenção:** explicar em voz alta, sem olhar, as três partes da
    história da carta (endereço, caminho, chave) e qual erro cada uma gera.
  - `50 min` Fechar o que ficou aberto no placar da semana. Se nada ficou, ler
    o `docker-compose.yml` linha a linha e anotar ao lado o que você **acha**
    que cada chave faz, sem pesquisar. É a preparação da semana 2: lá você
    confere as apostas.
- **Projeto: T022E, implementação (Green)**
  - `60 min` Fazer os testes de ontem passarem no `RehearsalVotingService`, com
    a menor mudança possível. Rodar a suíte inteira, incluindo o
    `RehearsalServiceCharacterizationTest` (a rede de segurança do legado).
    `/revisa-meu-codigo T022E implementação` e correções.
  - **Pronto quando:** T022E aprovada, marcada `[x]` no `tasks.md` e commitada.
- **Extras:** LeetCode #9 3Sum, #10 Container With Most Water.

**Domingo 04/10** · Docker ~45 min · Projeto ~45 min
- `20 min` Ritual semanal (fora das metades; conferir também: LeetCode #1–3 e o
  deck de Anki "Docker" da semana 0 foram feitos?).
- **Docker**
  - `10 min` **Retenção:** escrever, sem olhar, o Dockerfile multi-stage.
  - `35 min` Folga da metade Docker: o que sobrou da semana. Sem sobra, rodar o
    bot pelo compose (`docker compose up jansen-bot postgres-bot`) e explicar
    por que ele pode cair antes do banco ficar pronto (é o gancho da semana 2).
- **Projeto: T022E fecha ou T022F começa**
  - `45 min` Se a T022E não fechou no sábado, ela termina aqui (não estique o
    bloco). Se fechou: T022F, plano (`/revisa-meu-codigo T022F plano`): o novo
    campo de tipo em `ClaudeAction.ActionData` e o que muda no
    `system-prompt.txt` (FR-019, FR-022).
- Escrever no papel o alvo de segunda 05/10.
- **Leve:** terminar *The Mom Test* no trajeto.

---

**Entregável da semana:**
- **Docker:** bot rodando num container que você escreveu à mão e explica linha
  por linha: Dockerfile multi-stage, `--env-file`, banco com volume, README.
- **Projeto:** T022A–C commitadas; T022D auditada e aprovada por você; P-028 resolvida;
  T022E escrita por você, do teste à implementação, e aprovada na auditoria.
  Se sobrar tempo, T022F com o plano auditado. A US1 fica a quatro tasks do fim
  (T022F, T023, T024, T025).

**Placar para o ritual de domingo** (marcar cada item):
- [ ] 7 LeetCodes (#4–#10) *(extra; 29/09 ficou sem)*
- [ ] 7 retenções diárias + 1 semanal, com as hesitações anotadas
- [x] Bot sobe com `--env-file` *(29/09)*
- [x] Banco do projeto lido de fora (migrations e coluna `tipo`) *(29/09)*
- [ ] Volume provado com `down`/`up`
- [ ] Container do Testcontainers visto nascendo e morrendo
- [ ] Dockerfile multi-stage + MB antes e depois
- [ ] README
- [ ] T022A–C commitadas (V3, `TipoEnsaio`, service)
- [x] T022D auditada e aprovada *(07/10: achou a sobreposição dos testes 1 e 4 e
  apagou o 4; previu errado a quebra da linha 29 (achou que o 2 cairia e o 3 não) e
  entendeu: INSTRUMENTAL = "não é vocal", então só a linha 29 barra a projeção.
  Suíte: 56 verdes)*
- [ ] P-028 resolvida (`data-model.md` em dia)
- [ ] T022E: plano auditado
- [ ] T022E: testes vermelhos pelo motivo certo
- [ ] T022E: implementação verde e aprovada
- [ ] *(bônus)* T022F: plano auditado

---

## Semana 2 — Stack completa em compose · 05–11/10

O compose que você escreveu antes da hora (22/09) tem `jansen-bot`,
`postgres-bot`, `redis` e `evolution-postgres`, mas faltam o serviço
`evolution-api`, volumes, `healthcheck`/`depends_on`, `env_file` e as
credenciais do Google. O bot do compose caiu em 27/09 com
`Connection to postgres-bot:5432 refused`: ele acertou o endereço, mas subiu
antes do banco estar pronto. Esta semana conserta isso e coloca a stack inteira
de pé com um comando.

**Segunda 05/10** *(execução)*
- Noite A: LeetCode + Anki.
- `20:00–20:10` **Retenção:** reescrever o Dockerfile multi-stage sem olhar.
- `20:10–20:45` Limpar o ambiente: apagar os containers parados e a rede
  `jansen-network` que sobrou vazia do compose antigo (descobrir o comando que
  remove redes sem ninguém dentro). Criar um `.dockerignore` com o que não deve
  entrar no build (`.env`, `credentials/`, `.git`).
- **Pronto quando:** `docker ps -a` e `docker network ls` só mostram o que você
  sabe explicar.

**Terça 06/10**
- Manhã: Raciocínio — desenhar o `docker-compose.yml` **no papel** antes de
  mexer no arquivo: os cinco serviços, quem depende de quem, o que é volume, o
  que é variável de ambiente, o que é segredo. Depois comparar com o compose que
  você já tem.
- `20:00–20:10` **Retenção:** explicar o que o `.dockerignore` faz e por que o
  `.env` não pode entrar na imagem.
- Noite B: `jansen-bot` + `postgres-bot` no compose, com `env_file` e o volume
  da semana 1.
- **Pronto quando:** `docker compose up jansen-bot postgres-bot` sobe os dois. Se
  o bot cair por subir antes do banco, anote: é o assunto de amanhã.

**Quarta 07/10**
- Manhã: Teoria — `healthcheck` e `depends_on` com `condition: service_healthy`.
  A diferença entre "o container iniciou" e "o serviço está pronto". Usar o log
  de 27/09 como caso real.
- `20:00–20:10` **Retenção:** desenhar de novo, sem olhar, o compose de ontem no
  papel.
- Noite B: aplicar `healthcheck` no `postgres-bot` e `depends_on` no bot.
- **Pronto quando:** `docker compose down` e `up` três vezes seguidas, e o bot
  sobe sempre sem cair.

**Quinta 08/10**
- Manhã: Teoria — o que a Evolution API precisa para rodar: Postgres próprio,
  Redis, API key, e um volume para as instâncias (sem ele, você escaneia o QR
  code de novo a cada restart).
- `20:00–20:10` **Retenção:** escrever o `healthcheck` do Postgres sem olhar e
  explicar cada campo.
- Noite B: serviço `evolution-api` ligado ao `evolution-postgres` e ao `redis`.
  A senha do Redis tem que ser a mesma no `command` e na URI da Evolution.
- **Pronto quando:** a Evolution responde e mostra o QR code.

**Sexta 09/10**
- `05:40–06:30` **Retenção semanal.** Tirar o `docker-compose.yml` da vista e
  reescrever do zero, cronometrando. Comparar.
- `07:30–07:55` LeetCode.
- `07:55–09:00` Resto das variáveis do bot pelo `.env` (URL da Evolution pelo
  nome do serviço, Google Sheets, `BANDA_ADMIN_PHONES`), montar `./credentials`
  como somente leitura e conectar o WhatsApp.
- **Pronto quando:** partindo de nada rodando, `docker compose up` sobe a stack
  inteira sem nenhum passo manual, e o bot responde uma mensagem no WhatsApp.

**Fim de semana 10–11/10**
- `Sáb 13:00–13:10` **Retenção:** explicar a ordem em que os cinco serviços
  sobem e por quê.
- `Dom 13:50–14:00` **Retenção:** escrever o serviço `evolution-api` sem olhar.

**Leve:** mapear 10 barbearias acessíveis (conhecidos, bairro, a sua própria) e
escrever o roteiro de conversa no estilo Mom Test — perguntar sobre a vida
deles, nunca sobre a sua ideia. Sábado 10:30–12:00.

**Entregável:** stack completa subindo com um comando, bot respondendo no
WhatsApp + lista de 10 barbearias e roteiro pronto.

---

## Semana 3 — Testes de unidade do zero · 12–18/10

Sair do Mockito guiado. Ponto de partida honesto: você fechou o Nível 1
(`verify()` direto, sem captor) no `RehearsalService`. O Nível 2 é
`ArgumentCaptor`, e é onde esta semana começa.

Ritual produtor da semana: **esboçar quais casos testar antes de escrever
qualquer código.**

**Segunda 12/10** *(execução)*
- Noite A: LeetCode + Anki.
- `20:00–20:10` **Retenção:** reescrever o `docker-compose.yml` sem olhar.
- Noite B: escolher o projeto e listar por escrito os casos a testar — caminho
  feliz e casos de erro. Sem código nenhum.
- **Pronto quando:** existe uma lista nomeada de 8 a 12 casos.

**Terça 13/10**
- Manhã: Teoria — `ArgumentCaptor`. Por que `verify()` sozinho não basta quando
  o objeto é criado **dentro** do método e não sai por lugar nenhum: você não
  tem referência para comparar, então precisa capturar o que foi passado ao mock.
- `20:00–20:10` **Retenção:** reescrever de memória a lista de casos de ontem.
- Noite B: os dois primeiros testes com captor, em `registerVote`.
- **Pronto quando:** 2 testes verdes usando `ArgumentCaptor`.

**Quarta 14/10**
- Manhã: Teoria — stubbing vs verificação. `when(...).thenReturn(...)` prepara o
  que o método **consome**; `verify(...)` confere o que o método **produz**.
  Confundir os dois é o erro mais comum de quem está começando.
- `20:00–20:10` **Retenção:** reescrever um dos testes com `ArgumentCaptor` sem
  olhar (declaração, `verify` com `capture()`, `getValue()`).
- Noite B: cobrir 3 casos de caminho feliz da lista de segunda.
- **Pronto quando:** 3 testes verdes.

**Quinta 15/10**
- Manhã: Teoria — testar erro. `assertThrows` para a exceção esperada, e
  `verify(mock, never())` para provar que o colaborador **não** foi chamado
  quando deveria ter abortado. O segundo é o que a maioria esquece.
- `20:00–20:10` **Retenção:** explicar em voz alta a diferença entre `when` e
  `verify` e escrever um exemplo de cada, sem olhar.
- Noite B: cobrir os casos de erro.
- **Pronto quando:** cada caso de erro da lista de segunda tem um teste.

**Sexta 16/10**
- `05:40–06:30` Raciocínio: ler a suíte inteira e encontrar o caso que você
  **não** cobriu. Sempre existe um.
- `07:30–07:55` LeetCode.
- `07:55–08:10` **Retenção semanal:** escrever do zero um teste de caso de erro
  completo (`assertThrows` + `verify(mock, never())`), sem olhar.
- `08:10–09:00` Fechar a suíte.
- **Pronto quando:** `mvn test` verde e todos os casos da lista de segunda
  cobertos.

**Fim de semana 17–18/10**
- `Sáb 13:00–13:10` **Retenção:** explicar quando usar `ArgumentCaptor` e quando
  `verify` direto basta.
- `Dom 13:50–14:00` **Retenção:** escrever um teste com stubbing e verificação,
  sem olhar.

**Leve:** 2 primeiras conversas de descoberta com barbearias, aplicando o
roteiro. Documentar cru, sem interpretar ainda.

**Entregável:** suíte de testes de unidade real, escrita por você, num repo seu.

---

## Semana 4 — Testes de integração · 19–25/10

Onde Docker e testes se encontram — e o motivo de esta semana estar aqui, e não
quatro semanas depois: o Docker das semanas 1 e 2 ainda está fresco.

**Segunda 19/10** *(execução)*
- Noite A: LeetCode + Anki.
- `20:00–20:10` **Retenção:** escrever um teste de unidade com captor sem olhar.
- Noite B: adicionar a dependência do Testcontainers no `pom.xml`.
- **Pronto quando:** o projeto compila com a dependência nova.

**Terça 20/10**
- Manhã: Teoria — unidade vs integração. O que cada um prova, e o que **nenhum
  dos dois** prova. Por que um Postgres real em container vale mais que um H2 em
  memória: o banco de mentira aceita coisas que o de verdade recusa, e o teste
  passa enquanto a produção quebra.
- `20:00–20:10` **Retenção:** escrever de memória a dependência do
  Testcontainers no `pom.xml`.
- Noite B: subir um Postgres via Testcontainers dentro de um teste.
- **Pronto quando:** o teste sobe o container e conecta.

**Quarta 21/10**
- Manhã: Teoria — ciclo de vida do container no teste. `@Container` estático
  (uma vez por classe) vs por método, e o custo de tempo de cada escolha.
- `20:00–20:10` **Retenção:** reescrever sem olhar a declaração do container
  Postgres no teste e explicar o que ela faz.
- Noite B: primeiro teste de integração ponta a ponta — salva e lê do banco real.
- **Pronto quando:** 1 teste de integração verde.

**Quinta 22/10**
- Manhã: Raciocínio — escolher o segundo fluxo a cobrir e desenhar o cenário
  antes de codar: o que entra, o que deve estar no banco no fim.
- `20:00–20:10` **Retenção:** explicar `@Container` estático vs por método e o
  custo de cada um.
- Noite B: implementar.
- **Pronto quando:** 2º teste de integração verde.

**Sexta 23/10**
- `05:40–06:30` Raciocínio: rodar a suíte inteira cronometrando. Se integração e
  unidade rodam juntas, cada `mvn test` passa a custar minutos e você vai parar
  de rodar — separar é o que mantém o hábito vivo.
- `07:30–07:55` LeetCode.
- `07:55–08:10` **Retenção semanal:** escrever do zero um teste de integração
  completo com Testcontainers, sem olhar.
- `08:10–09:00` Configurar a separação (tag do JUnit 5 ou profile do Maven).
- **Pronto quando:** `mvn test` roda só unidade e é rápido; um comando separado
  roda a integração.

**Fim de semana 24–25/10**
- `Sáb 13:00–13:10` **Retenção:** explicar o que teste de unidade e de
  integração provam, e o que nenhum dos dois prova.
- `Dom 13:50–14:00` **Retenção:** escrever o comando que roda só a integração.

**Leve:** +3 conversas (total de 5). Domingo: escrever a síntese preliminar — a
dor é real? Pagariam? Quanto?

**Entregável:** testes de integração rodando sobre Postgres em container, com
unidade e integração separadas.

---

## Semana 5 — Deploy real, parte 1 · 26/10–01/11

Pegar a stack das semanas 1–2 e subir numa VPS ou no AWS free tier, acessível
pela internet. Sozinho — IA como consulta, não como piloto.

- **Seg** *(execução)*: decidir VPS vs AWS free tier e criar a conta/instância.
- **Ter** — Manhã: Teoria — SSH, par de chaves pública/privada, por que senha
  não serve. Noite: primeiro acesso e instalar Docker na instância.
- **Qua** — Manhã: Raciocínio — como a imagem chega lá: buildar local e dar push
  para um registry, ou buildar na própria máquina? Decidir com argumento escrito.
  Noite: executar a escolha.
- **Qui** — Noite: subir o container na instância.
- **Sex** — Manhã: debugar até responder pela internet. Bloco longo: documentar
  cada passo enquanto faz, porque isso vira a base do CI/CD da Season 2.

**Retenção diária** (10min, regra no topo da Season): comandos de SSH e de
chave; como a imagem chega na instância; os passos do deploy do dia anterior, na
ordem. Sexta: refazer do zero o deploy da semana a partir das suas anotações.

**Leve:** sintetizar as 5 conversas e escrever o veredito preliminar.
**Entregável:** algo seu no ar, que outra pessoa abre por um IP ou URL.

---

## Semana 6 — Deploy real, parte 2 · 02–08/11

- **Seg** *(execução)*: registrar ou apontar o domínio.
- **Ter** — Manhã: Teoria — DNS, registro A, propagação. Noite: apontar o
  domínio para o IP.
- **Qua** — Manhã: Teoria — HTTPS, Let's Encrypt e reverse proxy. Noite:
  configurar o certificado.
- **Qui** — Noite: restart automático (`restart: unless-stopped`). Testar
  matando o processo de propósito.
- **Sex** — Manhã: revisão fim a fim. Bloco longo: fechar o deploy documentado
  passo a passo.

**Retenção diária:** registro A e propagação de DNS; o caminho de uma requisição
HTTPS até o container (proxy, certificado, porta); as políticas de `restart`.
Sexta: explicar o deploy inteiro em voz alta, do domínio ao container.

**Leve:** fechar o documento de validação com um veredito claro — **validado /
não validado / pivotar de nicho**.
**Entregável:** app no ar com domínio e HTTPS + veredito de mercado escrito.

---

## Semana 7 — Primeiro IA em código · 09–15/11

Primeira chamada de API de LLM **dentro de código seu**. Nada de copiar do chat.

- **Seg** *(execução)*: criar a chave de API e um projeto vazio que compile.
- **Ter** — Manhã: Teoria — anatomia de uma chamada: request, response, tokens,
  custo. Noite: primeira chamada simples via código.
- **Qua** — Manhã: Teoria — prompt estruturado para saída previsível. Noite:
  ajustar até o JSON vir confiável cinco vezes seguidas.
- **Qui** — Noite: parsear a resposta e usar o resultado no programa.
- **Sex** — Bloco longo: transformar num serviço pequeno reutilizável.

**Retenção diária:** anatomia da chamada (request, response, tokens, custo);
reescrever sem olhar o prompt estruturado e o parse do JSON. Sexta: reescrever
do zero a chamada completa.

**Leve:** se validado, esboçar o escopo mínimo do agente da barbearia — o que
faz, qual o fluxo de conversa. Se não validado, escolher o próximo nicho.
**Entregável:** um serviço seu que faz uma chamada de LLM estruturada.

---

## Semana 8 — RAG mínimo + spec do MVP · 16–22/11

RAG à mão. Entender o mecanismo, não usar framework mágico.

- **Seg** *(execução)*: juntar o corpo de dados num arquivo (FAQ da barbearia,
  suas notas).
- **Ter** — Manhã: Teoria — o que é embedding, o que é vector DB, por que busca
  por significado não é busca por palavra. Noite: gerar embeddings de um texto
  pequeno.
- **Qua** — Manhã: Raciocínio — desenhar o fluxo pergunta → recupera → responde
  antes de codar. Noite: guardar os embeddings e testar uma busca.
- **Qui** — Noite: RAG ponta a ponta.
- **Sex** — Bloco longo: escrever a spec de 1 página do MVP. Fecha a Season.

**Retenção diária:** o que é embedding e por que busca por significado não é
busca por palavra; desenhar sem olhar o fluxo pergunta → recupera → responde.
Sexta: reescrever do zero a etapa de busca.

**Entregável:** RAG mínimo funcionando + spec do MVP pronta.

---

## Ritual semanal — domingo, 13:30–13:50

1. Revisar o que fechou e o que escorregou.
2. Conferir: a semana teve entregável? Se não, é alerta, não semana normal.
3. Contar quantos dias bateu o LeetCode sem falhar. Não é sobre resolver
   difícil, é sobre não quebrar a corrente.
4. Escrever o alvo de segunda-feira numa linha — segunda não tem manhã para
   decidir nada.
5. Mover para a semana seguinte só o que ficou pendente. Não acumular culpa.
6. Ler o caderno de retenção da semana. O que você hesitou mais de uma vez vira
   alvo da retenção da semana seguinte.

---

## Fim da Season 1 — você terá

Docker que você opera, uma suíte de testes de unidade e integração escrita do
zero, um deploy real no ar feito por você, o primeiro sistema de IA construído à
mão, a demanda decidida e a spec do MVP pronta. A base deixou de ser
terceirizada.

---

# Season 2 · 23/11 – 17/01 — Construir o MVP redondo

Objetivo: transformar a spec num MVP funcionando de verdade, no ar, com a base
construída na Season 1. Construir e — se validado — colocar na frente de uma
barbearia-piloto.

Entregáveis-alvo (detalhados quando a Season 1 fechar):
- MVP do agente em produção, mesmo que para um piloto só.
- Pipeline de deploy automatizado — o CI/CD que a semana 6 preparou à mão.
- Avaliação básica do agente: medir se responde bem, não "parece que tá bom".
- Primeiro piloto usando de verdade.

O peso migra: base técnica entra em modo aplicação (você aprende o que faltar
*construindo*), IA e produto viram o centro do tempo.

---

# Season 3 · meses 5–6 — Redondo e decisão

Objetivo: MVP estável, decisão honesta sobre o rumo do negócio, base técnica
consolidada em nível de senioridade real.

Entregáveis-alvo:
- MVP rodando redondo e estável.
- Decisão com dado na mão: insistir no negócio ou pivotar a energia de volta
  para aprofundar senioridade (conforme a prioridade nº 5 do `CLAUDE.md`).
- Retrospectiva escrita dos 6 meses: o que aprendeu, o que provou, o que vem.

Formalização (MEI/CNPJ), preço e múltiplos clientes só entram aqui **se** a
validação e o piloto justificarem. Não são meta; são consequência possível.

---

## Trilha paralela: SQL + concorrência, via trabalho

Roda dentro do estágio na PF, em paralelo a todas as Seasons — não consome nada
das 14h/semana de casa. Fecha as lacunas 4 e 5 do `CLAUDE.md` usando o ambiente
de aprendizado mais rico que existe de graça: código real, stakes reais e
revisão de gente mais sênior.

**Ordem:**

1. **SQL primeiro.** Puxa na frente porque toda tarefa do estágio já toca banco
   — é repetição diária, sem esforço extra de agenda. Sequência interna: JOINs
   (inner/left/right) → subqueries → índices (o que são, por que aceleram) →
   `EXPLAIN` / `EXPLAIN ANALYZE` (ler plano de execução) → transações e níveis
   de isolamento, que já é a ponte para concorrência.
2. **Concorrência em Java, fundamentos** (sem Quarkus ainda). Thread/Runnable →
   `ExecutorService` → `synchronized`/`volatile`/race condition → deadlock →
   virtual threads (Java 21, novo, vale currículo).
3. **Concorrência no Quarkus.** Só depois dos fundamentos: worker thread pool vs
   event loop reativo (Vert.x) → básico de Mutiny (`Uni`/`Multi`) →
   `@Blocking`/`@NonBlocking` e por que isso muda performance.

**Como encaixar sem tirar hora do trabalho:**
- Pedir tarefas que toquem query um pouco mais complexa (relatório, filtro,
  join) em vez de só CRUD puro. É o pedido mais natural de fazer a um líder
  técnico; ninguém estranha.
- Ativar o log de SQL gerado (Hibernate/Panache em debug, em dev) e ler o que
  sai, todo dia. Hábito de baixo custo, tipo o LeetCode.
- Em code review — dando ou recebendo — perguntar especificamente sobre índice
  numa query nova, ou por que uma escolha de concorrência foi feita.
- Downtime natural (build, deploy, CI rodando, esperando review) = 10–15min
  lendo doc oficial em vez de rolar feed.
- Se aparecer um bug real de race condition ou query lenta em produção,
  documentar como estudo de caso pessoal. Vale mais que teoria abstrata.

---

## Lista mestra de LeetCode (Blind 75, em blocos)

Alimenta o LeetCode do dia (Noite A de segunda a quinta, mais sexta de manhã) e
a sessão de sábado (2–3 por vez, incluindo os 🔺 mais difíceis, que cabem melhor
no bloco longo).

**Ritmo:** 5 por semana nos dias úteis + 2 a 3 no sábado = **7 a 8 por semana**.
Nesse ritmo a lista fecha em **~10 semanas**, dentro da Season 1 e 2 — bem antes
dos 5–6 meses estimados na versão anterior, porque a Noite A agora é um bloco
dedicado em vez de uma fatia disputada.

**Bloco 1 — Arrays & Hashing**
1. Two Sum — Easy
2. Contains Duplicate — Easy
3. Valid Anagram — Easy
4. Group Anagrams — Medium
5. Top K Frequent Elements — Medium
6. 🔺 Product of Array Except Self — Medium
7. 🔺 Longest Consecutive Sequence — Medium

**Bloco 2 — Two Pointers & Sliding Window**
8. Valid Palindrome — Easy
9. 3Sum — Medium
10. Container With Most Water — Medium
11. Best Time to Buy and Sell Stock — Easy
12. Longest Substring Without Repeating Characters — Medium
13. 🔺 Longest Repeating Character Replacement — Medium
14. 🔺 Minimum Window Substring — Hard

**Bloco 3 — Stack & Binary Search**
15. Valid Parentheses — Easy
16. Find Minimum in Rotated Sorted Array — Medium
17. Search in Rotated Sorted Array — Medium
18. Maximum Subarray — Medium
19. Maximum Product Subarray — Medium
20. 🔺 Sum of Two Integers — Medium
21. 🔺 Coin Change — Medium

**Bloco 4 — Linked List**
22. Reverse Linked List — Easy
23. Linked List Cycle — Easy
24. Merge Two Sorted Lists — Easy
25. Remove Nth Node From End of List — Medium
26. Reorder List — Medium
27. 🔺 Merge K Sorted Lists — Hard
28. 🔺 Course Schedule — Medium

**Bloco 5 — Trees, parte 1**
29. Maximum Depth of Binary Tree — Easy
30. Same Tree — Easy
31. Invert Binary Tree — Easy
32. Subtree of Another Tree — Easy
33. Binary Tree Level Order Traversal — Medium
34. 🔺 Validate Binary Search Tree — Medium
35. 🔺 Construct Binary Tree from Preorder and Inorder Traversal — Medium

**Bloco 6 — Trees, parte 2 + Tries** (concentra mais hard — é do assunto)
36. Kth Smallest Element in a BST — Medium
37. Lowest Common Ancestor of a BST — Easy
38. Implement Trie (Prefix Tree) — Medium
39. Add and Search Word — Medium
40. 🔺 Binary Tree Maximum Path Sum — Hard
41. 🔺 Serialize and Deserialize Binary Tree — Hard
42. 🔺 Word Search II — Hard

**Bloco 7 — Heap & Backtracking & Graphs, parte 1**
43. Find Median from Data Stream — Hard
44. Combination Sum — Medium
45. Word Search — Medium
46. Number of Islands — Medium
47. Clone Graph — Medium
48. 🔺 Pacific Atlantic Water Flow — Medium
49. 🔺 Alien Dictionary — Hard

**Bloco 8 — Intervals & Matrix**
50. Insert Interval — Medium
51. Merge Intervals — Medium
52. Non-overlapping Intervals — Medium
53. Set Matrix Zeroes — Medium
54. Spiral Matrix — Medium
55. 🔺 Rotate Image — Medium
56. 🔺 Graph Valid Tree — Medium

**Bloco 9 — Strings avançado**
57. Longest Palindromic Substring — Medium
58. Palindromic Substrings — Medium
59. Encode and Decode Strings — Medium
60. Number of Connected Components in an Undirected Graph — Medium
61. Climbing Stairs — Easy
62. 🔺 House Robber — Medium
63. 🔺 House Robber II — Medium

**Bloco 10 — DP 1D**
64. Decode Ways — Medium
65. Unique Paths — Medium
66. Jump Game — Medium
67. Missing Number — Easy
68. Number of 1 Bits — Easy
69. 🔺 Word Break — Medium
70. 🔺 Longest Increasing Subsequence — Medium

**Bloco 11 — Fechamento**
71. Counting Bits — Easy
72. Reverse Bits — Easy
73. Longest Common Subsequence — Medium
74. Meeting Rooms — Easy *(LeetCode Premium)*
75. 🔺 Meeting Rooms II — Medium *(LeetCode Premium)*

---

## Como este plano falha (e como não deixar)

- **Falha por perda de propósito** (seu padrão declarado): cada semana fecha com
  um entregável visível. Se uma semana passou sem entregável, é alerta, não
  semana normal.

- **Falha por terceirizar de novo:** se você se pegar pedindo à IA para escrever
  o Dockerfile, o teste ou o deploy inteiro em vez de entender, parou de
  evoluir a base. A IA é consulta, não piloto — até a base estar sua.

- **Falha por sobrecarga:** a versão anterior alertava que 3h toda noite depois
  de estágio presencial era demais. Isso foi **corrigido na estrutura**, não na
  força de vontade: a noite agora tem no máximo 1h30, dividida em dois blocos de
  45 minutos, e o trabalho pesado mudou para a manhã. Noite ruim continua
  valendo thread leve, sem culpa.

- **Falha pelo domingo:** a igreja vai até 22:45, e a noite de domingo é a mais
  curta da semana (~7h15). Por isso segunda é dia de execução e nunca de teoria
  nova. Se você tentar puxar segunda para cima, é a semana inteira que paga.

- **Falha por manhã sem alvo:** 50 minutos sem um alvo escrito na véspera viram
    20. O passo 4 do ritual de domingo e a última linha de cada noite existem
        exatamente para isso — não são burocracia.

- **Falha silenciosa por sono:** o plano inteiro assume 8h30 na cama, quatro
  noites às 21:00. Se em duas semanas você ainda estiver arrastando às 5:30, o
  problema não é o horário: é que 21:00 na cama não virou 21:00 dormindo. Tela
  desligada às 20:45 é parte do plano, não um detalhe.