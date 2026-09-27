# Plano de Evolução — 6 meses

> Companion do `CLAUDE.md`. Este arquivo é o roteiro executável.
> Season 1 é detalhada dia a dia, bloco a bloco. Seasons 2 e 3 ficam em nível de
> objetivo e entregável — são redesenhadas quando a Season anterior fecha,
> porque dependem do que ela revelar.
>
> **Revisão de 25/09/2026.** Reescrito para a agenda nova (blocos de manhã,
> ensaio de terça encerrado, noites encurtadas para proteger 8h30 de sono).
> A ordem da Season 1 mudou: testes subiram para as semanas 3–4.

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
| Noite B 20:00–20:45 | Seg, Ter, Qua, Qui | 45min | **Execução** — mão na massa |
| Sábado 08:45–09:45 | Sáb | 60min | LeetCode longo |
| Sábado 13:00–14:00 | Sáb | 60min | Margem ou aprofundamento |
| Domingo 13:30–14:45 | Dom | 75min | Ritual (20min) + margem |

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
Mão na massa na tarefa da semana. Execução do que a manhã definiu. Se você se
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
| 0 | 26–27/09 | Setup | Ambiente pronto, teoria da semana 1 carregada |
| 1 | 28/09–04/10 | Docker do zero | Container escrito à mão, explicável linha a linha |
| 2 | 05–11/10 | Docker que você opera | `docker compose up` sobe app + banco do zero |
| 3 | 12–18/10 | Testes de unidade do zero | Suíte escrita por você, verde |
| 4 | 19–25/10 | Testes de integração | Testcontainers rodando, unidade separada de integração |
| 5 | 26/10–01/11 | Deploy real, parte 1 | Algo seu no ar, acessível por outra pessoa |
| 6 | 02–08/11 | Deploy real, parte 2 | Domínio + HTTPS + restart automático, documentado |
| 7 | 09–15/11 | Primeiro IA em código | Serviço seu que faz chamada de LLM estruturada |
| 8 | 16–22/11 | RAG mínimo + spec | RAG funcionando + spec de 1 página do MVP |

---

## Semana 0 — Setup · 26–27/09

Dois dias para carregar a teoria e preparar o ambiente, de modo que a segunda-feira
(que não tem manhã) possa executar em vez de decidir.

**Sábado 26/09**
- `08:45–09:45` LeetCode: #1 Two Sum, #2 Contains Duplicate, #3 Valid Anagram.
  Três fáceis de propósito — o objetivo é abrir a corrente, não provar nada.
- `13:00–14:00` Teoria Docker:
    - Imagem vs container: imagem é template parado, container é instância rodando.
    - Layers: cada instrução do Dockerfile vira uma camada, e camadas são
      cacheadas — por isso a *ordem* importa (o que muda menos vai primeiro).
    - Base image para Java: `eclipse-temurin:21-jre`, não `-jdk`. O JDK é maior e
      só é necessário para *buildar*, não para *rodar*.
    - Rodar `mvn clean package` no bot da banda e confirmar que o jar existe.
- **Pronto quando:** o jar está em `target/` e você consegue explicar, em voz
  alta, por que a ordem das instruções afeta o tempo de build.

**Domingo 27/09**
- `13:30–13:50` Ritual: criar o deck de Anki "Docker". Escrever numa linha, no
  papel, o alvo de segunda-feira.
- `13:50–14:45` Instalar ou atualizar o Docker Desktop. Rodar `docker run
  hello-world`.
- **Pronto quando:** `docker --version` responde e o hello-world roda.
- **Leve:** começar *The Mom Test* no trajeto (a partir de segunda).

---

## Semana 1 — Docker do zero · 28/09–04/10

Escrever um `Dockerfile` à mão, **sem IA gerar**, e entender cada linha.

**Segunda 28/09** *(sem manhã — execução pura)*
- `18:45–19:10` LeetCode #4 Group Anagrams.
- `19:10–19:30` Anki: 2 cards da teoria de sábado (imagem vs container; por que
  a ordem das layers importa).
- `20:00–20:45` Criar o `Dockerfile` no repo do bot da banda. Escrever você
  mesmo `FROM`, `WORKDIR` e `COPY` (copiando o jar gerado para dentro da
  imagem). Comentar cada linha com suas palavras.
- **Pronto quando:** `docker build -t band-bot .` termina sem erro. Não precisa
  rodar ainda — só buildar.
- **Erro comum:** esquecer de buildar o jar antes, e o `COPY` falhar porque o
  arquivo não existe. Se acontecer, é sinal de que você entendeu a ordem certa:
  build da aplicação vem antes do build da imagem.

**Terça 29/09**
- `05:40–06:30` Teoria: `RUN` roda **durante o build** (ex: instalar algo);
  `CMD` é o comando padrão ao **rodar**, mas pode ser sobrescrito; `ENTRYPOINT`
  é o comando fixo que sempre roda (e o `CMD`, se existir, vira argumento dele).
  Para Java, o padrão é `ENTRYPOINT` com `java -jar`. Escrever o `ENTRYPOINT`
  do seu jeito, sem copiar exemplo pronto.
- `18:45–19:10` LeetCode #5 Top K Frequent Elements.
- `19:10–19:30` Anki: RUN vs CMD vs ENTRYPOINT.
- `20:00–20:45` `docker build` de novo, depois `docker run`. **Deixe dar erro.**
  Provavelmente vai faltar variável de ambiente, porta, ou conexão com algo
  externo — é esperado, não é fracasso.
- **Pronto quando:** você tem pelo menos um erro real anotado: mensagem exata +
  sua hipótese do que causou.

**Quarta 30/09**
- `05:40–06:30` Raciocínio (sem teoria nova): ler o erro de ontem e formular por
  escrito as três hipóteses mais prováveis —
  (1) variável de ambiente que existia no `.env` local mas o container não
  enxerga sozinho, e precisa vir com `--env-file` ou `-e`;
  (2) porta não exposta — falta `EXPOSE` no Dockerfile e `-p` no `docker run`;
  (3) endereço de serviço externo (banco, Evolution API) apontando para
  `localhost`, que dentro do container não é o host, é o próprio container.
  Ordenar por probabilidade antes de testar qualquer uma.
- `18:45–19:10` LeetCode #6 🔺 Product of Array Except Self.
- `19:10–19:30` Anki.
- `20:00–20:45` Testar as hipóteses 1 e 2, uma de cada vez.
- **Pronto quando:** pelo menos uma hipótese confirmada ou descartada **com
  evidência** — não com impressão.

**Quinta 01/10**
- `05:40–06:30` Teoria: rede do container. Por que `localhost` dentro do
  container é o próprio container, o que é a rede bridge default, e o que
  `host.docker.internal` resolve.
- `18:45–19:10` LeetCode #7 🔺 Longest Consecutive Sequence.
- `19:10–19:30` Anki: o card mais importante da semana é este.
- `20:00–20:45` Aplicar e fechar os erros restantes.
- **Pronto quando:** `docker ps` mostra o container de pé por mais de 60
  segundos sem crashar. Tudo bem se a aplicação ainda não funcionar 100%.

**Sexta 02/10**
- `05:40–06:30` **Teste de retenção.** Mover o Dockerfile para outro lugar (sem
  apagar, só tirar da vista) e reescrever do zero, cronometrando. Comparar o
  tempo com a primeira vez.
- `07:30–07:55` LeetCode #8 Valid Palindrome.
- `07:55–09:00` Escrever um README curto no repo: comando de build, comando de
  run, e — separado — onde você hesitou no teste de retenção. Anotar específico
  ("não lembrei se COPY vinha antes ou depois de WORKDIR"), nunca vago
  ("preciso estudar mais Docker").
- **Pronto quando:** o Dockerfile novo builda igual ao antigo, você explica cada
  linha em voz alta sem olhar a tela, e outra pessoa conseguiria rodar seu
  container só lendo o README.

**Fim de semana 03–04/10**
- `Sáb 08:45–09:45` LeetCode #9 3Sum, #10 Container With Most Water.
- `Sáb 13:00–14:00` Margem: recuperar o que escorregou, ou aprofundar.
- `Dom 13:30–13:50` Ritual semanal.
- **Leve:** terminar *The Mom Test* no trajeto.

**Entregável da semana:** projeto rodando em container que você escreveu à mão e
sabe explicar linha por linha.

---

## Semana 2 — Docker que você opera · 05–11/10

O que a IA fazia e você não entendia: networking, volumes, multi-stage.
Quebrar de propósito e consertar.

**Segunda 05/10** *(execução)*
- Noite A: LeetCode + Anki.
- Noite B: subir um Postgres em container isolado e conectar nele de fora
  (DBeaver ou `psql`).
- **Pronto quando:** você conecta e lista as tabelas.

**Terça 06/10**
- Manhã: Teoria — networking em Docker. Rede bridge, resolução por nome de
  serviço, diferença entre expor porta para o host e containers se falarem entre si.
- Noite B: app + Postgres em containers separados, conversando.
- **Pronto quando:** a app conecta no banco **pelo nome do serviço**, não por IP.

**Quarta 07/10**
- Manhã: Teoria — volumes. Bind mount vs named volume; o que persiste e o que
  evapora quando o container morre.
- Noite B: volume no Postgres. Derrubar tudo e subir de novo.
- **Pronto quando:** as linhas que você inseriu antes ainda estão lá.

**Quinta 08/10**
- Manhã: Teoria — multi-stage build. Por que buildar com JDK e rodar com JRE em
  estágios separados encolhe a imagem.
- Noite B: reescrever o Dockerfile em multi-stage.
- **Pronto quando:** `docker images` comprova a redução, e você anotou o antes e
  depois em MB.

**Sexta 09/10**
- `05:40–06:30` Raciocínio: desenhar o `docker-compose.yml` **no papel** antes
  de escrever — quais serviços, quem depende de quem, o que é volume, o que é
  variável de ambiente.
- `07:30–07:55` LeetCode.
- `07:55–09:00` Escrever o compose juntando tudo.
- **Pronto quando:** `docker compose up` sobe app + banco de uma vez, partindo
  do zero, sem nenhum passo manual.

**Leve:** mapear 10 barbearias acessíveis (conhecidos, bairro, a sua própria) e
escrever o roteiro de conversa no estilo Mom Test — perguntar sobre a vida
deles, nunca sobre a sua ideia. Sábado 10:30–12:00.

**Entregável:** stack completa subindo com um comando + lista de 10 barbearias e
roteiro pronto.

---

## Semana 3 — Testes de unidade do zero · 12–18/10

Sair do Mockito guiado. Ponto de partida honesto: você fechou o Nível 1
(`verify()` direto, sem captor) no `RehearsalService`. O Nível 2 é
`ArgumentCaptor`, e é onde esta semana começa.

Ritual produtor da semana: **esboçar quais casos testar antes de escrever
qualquer código.**

**Segunda 12/10** *(execução)*
- Noite A: LeetCode + Anki.
- Noite B: escolher o projeto e listar por escrito os casos a testar — caminho
  feliz e casos de erro. Sem código nenhum.
- **Pronto quando:** existe uma lista nomeada de 8 a 12 casos.

**Terça 13/10**
- Manhã: Teoria — `ArgumentCaptor`. Por que `verify()` sozinho não basta quando
  o objeto é criado **dentro** do método e não sai por lugar nenhum: você não
  tem referência para comparar, então precisa capturar o que foi passado ao mock.
- Noite B: os dois primeiros testes com captor, em `registerVote`.
- **Pronto quando:** 2 testes verdes usando `ArgumentCaptor`.

**Quarta 14/10**
- Manhã: Teoria — stubbing vs verificação. `when(...).thenReturn(...)` prepara o
  que o método **consome**; `verify(...)` confere o que o método **produz**.
  Confundir os dois é o erro mais comum de quem está começando.
- Noite B: cobrir 3 casos de caminho feliz da lista de segunda.
- **Pronto quando:** 3 testes verdes.

**Quinta 15/10**
- Manhã: Teoria — testar erro. `assertThrows` para a exceção esperada, e
  `verify(mock, never())` para provar que o colaborador **não** foi chamado
  quando deveria ter abortado. O segundo é o que a maioria esquece.
- Noite B: cobrir os casos de erro.
- **Pronto quando:** cada caso de erro da lista de segunda tem um teste.

**Sexta 16/10**
- `05:40–06:30` Raciocínio: ler a suíte inteira e encontrar o caso que você
  **não** cobriu. Sempre existe um.
- `07:30–07:55` LeetCode.
- `07:55–09:00` Fechar a suíte.
- **Pronto quando:** `mvn test` verde e todos os casos da lista de segunda
  cobertos.

**Leve:** 2 primeiras conversas de descoberta com barbearias, aplicando o
roteiro. Documentar cru, sem interpretar ainda.

**Entregável:** suíte de testes de unidade real, escrita por você, num repo seu.

---

## Semana 4 — Testes de integração · 19–25/10

Onde Docker e testes se encontram — e o motivo de esta semana estar aqui, e não
quatro semanas depois: o Docker das semanas 1 e 2 ainda está fresco.

**Segunda 19/10** *(execução)*
- Noite A: LeetCode + Anki.
- Noite B: adicionar a dependência do Testcontainers no `pom.xml`.
- **Pronto quando:** o projeto compila com a dependência nova.

**Terça 20/10**
- Manhã: Teoria — unidade vs integração. O que cada um prova, e o que **nenhum
  dos dois** prova. Por que um Postgres real em container vale mais que um H2 em
  memória: o banco de mentira aceita coisas que o de verdade recusa, e o teste
  passa enquanto a produção quebra.
- Noite B: subir um Postgres via Testcontainers dentro de um teste.
- **Pronto quando:** o teste sobe o container e conecta.

**Quarta 21/10**
- Manhã: Teoria — ciclo de vida do container no teste. `@Container` estático
  (uma vez por classe) vs por método, e o custo de tempo de cada escolha.
- Noite B: primeiro teste de integração ponta a ponta — salva e lê do banco real.
- **Pronto quando:** 1 teste de integração verde.

**Quinta 22/10**
- Manhã: Raciocínio — escolher o segundo fluxo a cobrir e desenhar o cenário
  antes de codar: o que entra, o que deve estar no banco no fim.
- Noite B: implementar.
- **Pronto quando:** 2º teste de integração verde.

**Sexta 23/10**
- `05:40–06:30` Raciocínio: rodar a suíte inteira cronometrando. Se integração e
  unidade rodam juntas, cada `mvn test` passa a custar minutos e você vai parar
  de rodar — separar é o que mantém o hábito vivo.
- `07:30–07:55` LeetCode.
- `07:55–09:00` Configurar a separação (tag do JUnit 5 ou profile do Maven).
- **Pronto quando:** `mvn test` roda só unidade e é rápido; um comando separado
  roda a integração.

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