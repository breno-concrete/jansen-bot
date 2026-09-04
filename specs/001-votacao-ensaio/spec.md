# Feature Specification: Votação de Ensaio com Quórum e Remarcação

**Feature Branch**: `[001-votacao-ensaio]`

**Created**: 2026-09-04

**Status**: Draft

**Input**: User description: "Quero uma feature que o usuário (Lídera da banda) pssa mandar um mesnagem ao bot pedindo para marcar um ensaio em determinada hora e data. O bot pega essa infos e manda para todos os integrantes, os integrantes (usuarios também) devem responder com sim ou não e o bot avisa qunado todos responderem e marca a porcentagem. quando apenas menos de 50% dos usuários marcaram que sim (isso após todos marcarem) o sistema deve perguntar ao líder se ele irá fazer o ensaio. Quando todo marcarem, o relatorio é mandado pro Líder e ele decide a confrimação do ensaio. Dado que o líder queria remarcar o ensaio, quando a votação stá acontecedno ou se encerrou, então o sistema deve fazer uma nova votação e deixar claro que o ensaio está sendo remarcado. Dado que nem todos repsonderam depois de 12horas, quando a votação se encerrar(em 12horas), então o sistema deve notificar o usuario que não repsondeu uma hora anntes relembrando e, caso ele nainda não repsonda, devolver o relatório atualizado pro líder com ele estando como se tivese repsndido NÂO porém com uma tag não respondeu. Só lide rpode criar ensaios. Criterios de aceite: o relatório chegou ao lider ao final da votação. O usuário recebem a emsnagem de confirmação quando repsondem sim ou não. o lider pode remarcar o ensaio a qualquer momento a partir do dia que foi criado um ensaio"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Líder marca um ensaio e integrantes votam (Priority: P1)

A líder da banda envia uma mensagem ao bot pedindo para marcar um ensaio numa data e horário específicos. O bot identifica a data/hora e envia a todos os integrantes um pedido de confirmação de presença. Cada integrante responde "sim" ou "não" e recebe uma mensagem confirmando que a resposta foi registrada.

**Why this priority**: É o fluxo central da feature — sem ele não existe votação nem relatório. Todo o resto (quórum, lembrete, remarcação) depende deste fluxo básico existir e funcionar.

**Independent Test**: Pode ser testado enviando, como líder, um pedido de ensaio com data/hora, verificando que cada integrante recebe o pedido de confirmação, e que cada resposta sim/não gera uma mensagem de confirmação de volta para quem respondeu.

**Acceptance Scenarios**:

1. **Given** a líder envia uma mensagem pedindo para marcar um ensaio em uma data e hora específicas, **When** o bot processa a mensagem, **Then** todos os integrantes recebem uma mensagem pedindo confirmação de presença para aquele ensaio.
2. **Given** um integrante recebeu o pedido de confirmação, **When** ele responde "sim", **Then** seu status é registrado como confirmado e ele recebe uma mensagem confirmando o registro do "sim".
3. **Given** um integrante recebeu o pedido de confirmação, **When** ele responde "não", **Then** seu status é registrado como recusado e ele recebe uma mensagem confirmando o registro do "não".
4. **Given** um usuário que não é a líder, **When** ele pede ao bot para marcar um ensaio, **Then** o bot rejeita o pedido e nenhum ensaio é criado.

---

### User Story 2 - Encerramento da votação e relatório para a líder (Priority: P1)

Assim que todos os integrantes tiverem respondido (ou o prazo de votação se esgotar), a votação se encerra e a líder recebe um relatório com o resultado — quantos confirmaram, quantos recusaram e a porcentagem de "sim". Se a porcentagem de "sim" ficar abaixo de 50%, o bot também pergunta explicitamente à líder se o ensaio vai acontecer mesmo assim.

**Why this priority**: É o que dá utilidade prática à votação: sem o relatório e a decisão final da líder, a votação não leva a nenhuma ação concreta sobre o ensaio.

**Independent Test**: Pode ser testado fazendo todos os integrantes responderem e checando que a líder recebe o relatório final; e testando separadamente um cenário com menos de 50% de "sim", verificando que a líder recebe a pergunta extra sobre manter o ensaio.

**Acceptance Scenarios**:

1. **Given** todos os integrantes já responderam sim/não, **When** o último integrante responde, **Then** a votação se encerra imediatamente e a líder recebe o relatório final com a contagem e a porcentagem de confirmados.
2. **Given** a votação se encerrou com menos de 50% dos integrantes confirmando "sim", **When** o relatório é enviado à líder, **Then** o bot pergunta explicitamente à líder se o ensaio será realizado mesmo com a baixa confirmação.
3. **Given** a votação se encerrou com 50% ou mais dos integrantes confirmando "sim", **When** o relatório é enviado à líder, **Then** a líder recebe o relatório e decide a confirmação do ensaio, sem a pergunta extra de baixo quórum.

---

### User Story 3 - Prazo de 12 horas, lembrete e marcação de "não respondeu" (Priority: P2)

Se nem todos os integrantes responderem, a votação permanece aberta por até 12 horas a partir da criação do ensaio. Uma hora antes do prazo terminar, o bot lembra quem ainda não respondeu. Se mesmo assim a pessoa não responder até o fim das 12 horas, a votação se encerra, esse integrante é tratado como "não" no relatório, mas com uma marcação especial de "não respondeu" (diferente de quem recusou explicitamente).

**Why this priority**: Garante que a votação sempre chegue a uma conclusão dentro de um tempo previsível, mesmo com integrantes inativos — importante para a confiabilidade da feature, mas depende do fluxo básico (US1/US2) já existir.

**Independent Test**: Pode ser testado marcando um ensaio, deixando pelo menos um integrante sem responder, avançando o tempo até 1 hora antes do prazo de 12h e conferindo que ele recebe um lembrete; avançando até o fim das 12h e conferindo que a votação se encerra, o relatório chega à líder, e o integrante aparece como "não respondeu" (e não como uma recusa comum).

**Acceptance Scenarios**:

1. **Given** um ensaio foi marcado e a votação está aberta há 11 horas, **When** um integrante ainda não respondeu, **Then** ele recebe uma mensagem de lembrete pedindo para confirmar presença.
2. **Given** a votação está aberta há 12 horas e um integrante nunca respondeu, **When** o prazo se esgota, **Then** a votação se encerra, esse integrante é contabilizado como "não" no relatório com a tag "não respondeu", e o relatório atualizado é enviado à líder.
3. **Given** um integrante recebeu o lembrete e respondeu sim/não antes do fim das 12 horas, **When** o prazo se esgota, **Then** sua resposta original (sim ou não) é usada no relatório, sem a tag "não respondeu".

---

### User Story 4 - Líder remarca o ensaio (Priority: P2)

A qualquer momento a partir do dia em que o ensaio foi criado — com a votação ainda em andamento ou já encerrada — a líder pode pedir para remarcar o ensaio para uma nova data/hora. O bot inicia uma nova votação do zero para o novo horário e avisa claramente a todos os integrantes que o ensaio foi remarcado.

**Why this priority**: É um ajuste importante para lidar com mudanças de agenda, mas o fluxo de votação e relatório (US1-US3) precisa existir primeiro para a remarcação ter algo para reiniciar.

**Independent Test**: Pode ser testado pedindo à líder para remarcar um ensaio já criado (tanto com votação ainda aberta quanto já encerrada) e verificando que todos os integrantes recebem um aviso claro de remarcação com a nova data/hora, e que uma nova rodada de votação começa (todos voltam ao status pendente para o novo horário).

**Acceptance Scenarios**:

1. **Given** um ensaio já criado com a votação ainda em andamento, **When** a líder pede para remarcar para uma nova data/hora, **Then** a votação atual é encerrada, uma nova votação começa para a nova data/hora, e todos os integrantes recebem um aviso deixando claro que o ensaio foi remarcado.
2. **Given** um ensaio já criado com a votação já encerrada (com relatório entregue), **When** a líder pede para remarcar para uma nova data/hora, **Then** uma nova votação começa do zero para a nova data/hora, e todos os integrantes recebem um aviso deixando claro que o ensaio foi remarcado.
3. **Given** um usuário que não é a líder, **When** ele tenta remarcar um ensaio, **Then** o bot rejeita o pedido e o ensaio permanece inalterado.

---

### Edge Cases

- O que acontece se a líder enviar uma data/hora que o bot não consegue interpretar (ambígua ou incompleta)? O bot deve pedir esclarecimento em vez de marcar um ensaio com dados incorretos.
- O que acontece se a líder pedir um novo ensaio enquanto já existe uma votação em andamento para outro ensaio? Ambos devem ser tratados como votações independentes, cada uma com seu próprio relatório e prazo.
- O que acontece se um integrante responder algo diferente de "sim" ou "não"? A resposta não deve ser reconhecida como voto; o integrante continua pendente e pode tentar novamente.
- O que acontece se um integrante marcado como "não respondeu" enviar sua resposta depois que o relatório já foi enviado à líder? A resposta chega fora do prazo; o voto tardio não deve reabrir a votação nem alterar o relatório já entregue.
- O que acontece se a data/hora do ensaio marcado for menos de 12 horas no futuro? O prazo de votação de 12 horas continua valendo normalmente, mesmo que ultrapasse o horário do próprio ensaio.
- O que acontece exatamente na fronteira de 50% (ex.: metade dos integrantes confirmou "sim")? Considera-se que atingiu o quórum — a pergunta extra de baixo quórum só é disparada quando a confirmação fica **abaixo** de 50%, não em caso de empate.
- O que acontece se a líder remarcar o ensaio múltiplas vezes seguidas antes que qualquer integrante responda à remarcação anterior? Cada remarcação reinicia a votação e o prazo de 12 horas, valendo sempre a remarcação mais recente.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema MUST permitir que apenas a líder da banda solicite a criação de um ensaio (data e horário); pedidos de qualquer outro integrante MUST ser rejeitados sem criar ensaio.
- **FR-002**: Ao receber um pedido válido de ensaio, o sistema MUST extrair a data e o horário informados pela líder e, caso não consiga interpretá-los com confiança, MUST pedir esclarecimento à líder em vez de criar o ensaio.
- **FR-003**: Ao criar um ensaio, o sistema MUST enviar a todos os integrantes uma mensagem pedindo confirmação de presença (sim/não) para aquela data e horário.
- **FR-004**: O sistema MUST aceitar apenas respostas "sim" ou "não" como voto válido; qualquer outra resposta MUST ser ignorada como voto (o integrante permanece pendente).
- **FR-005**: Ao registrar o voto de um integrante (sim ou não), o sistema MUST enviar a esse integrante uma mensagem confirmando que a resposta foi recebida e registrada.
- **FR-006**: O sistema MUST encerrar a votação assim que todos os integrantes tiverem respondido, mesmo antes do prazo de 12 horas.
- **FR-007**: O sistema MUST encerrar a votação automaticamente 12 horas após a criação do ensaio (ou após uma remarcação), mesmo que nem todos tenham respondido.
- **FR-008**: Uma hora antes do encerramento das 12 horas, o sistema MUST enviar um lembrete a cada integrante que ainda não respondeu.
- **FR-009**: Ao encerrar a votação por prazo (12 horas) com integrantes sem resposta, o sistema MUST contabilizá-los como "não" no relatório, marcados com uma tag distinta de "não respondeu" (diferenciando-os de quem recusou explicitamente).
- **FR-010**: Ao encerrar a votação (seja por todos terem respondido, seja por prazo esgotado), o sistema MUST enviar à líder um relatório com a contagem de confirmados, recusados, não respondidos e a porcentagem de "sim" sobre o total de integrantes.
- **FR-011**: Quando a porcentagem de "sim" no relatório final for menor que 50%, o sistema MUST perguntar explicitamente à líder se o ensaio será realizado mesmo assim.
- **FR-012**: Quando a porcentagem de "sim" no relatório final for 50% ou mais, o sistema MUST entregar o relatório à líder para que ela decida a confirmação do ensaio, sem necessariamente exigir uma resposta explícita de baixo quórum.
- **FR-013**: O sistema MUST permitir que a líder remarque um ensaio para uma nova data/hora a qualquer momento a partir do dia em que o ensaio foi criado, independentemente de a votação estar em andamento ou já ter se encerrado.
- **FR-014**: Ao remarcar um ensaio, o sistema MUST iniciar uma nova rodada de votação (todos os integrantes voltam ao status pendente para a nova data/hora) e MUST enviar a todos uma mensagem deixando claro que o ensaio foi remarcado (incluindo a nova data/hora).
- **FR-015**: O sistema MUST rejeitar pedidos de remarcação vindos de qualquer usuário que não seja a líder, mantendo o ensaio inalterado.
- **FR-016**: Um voto recebido após o encerramento da votação (relatório já entregue) MUST ser descartado sem alterar o relatório já enviado nem reabrir a votação.
- **FR-017**: O sistema MUST reiniciar o prazo de 12 horas de votação a cada remarcação, contando a partir do momento da remarcação.

### Key Entities

- **Ensaio**: Representa um evento de ensaio marcado pela líder — data/hora atual, data/hora de criação, status da votação (aberta, encerrada), status de decisão final (confirmado, cancelado, pendente de decisão), e histórico de remarcações.
- **Integrante**: Membro da banda que pode votar em ensaios — identificação, papel (líder ou membro comum).
- **Voto**: Resposta de um integrante a um ensaio específico — integrante, escolha (sim, não, não respondeu), timestamp da resposta (quando houver).
- **Relatório de Votação**: Resumo gerado ao encerrar uma votação — contagem de sim/não/não-respondeu, porcentagem de confirmação, indicação se ficou abaixo do quórum de 50%.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% dos pedidos válidos de ensaio feitos pela líder resultam no envio do pedido de confirmação a todos os integrantes.
- **SC-002**: Todo integrante que responde "sim" ou "não" recebe uma mensagem de confirmação da sua resposta.
- **SC-003**: A líder recebe o relatório final de toda votação encerrada, seja por todos terem respondido, seja pelo prazo de 12 horas esgotado.
- **SC-004**: 100% dos integrantes que não responderam até 1 hora antes do prazo recebem um lembrete antes do encerramento da votação.
- **SC-005**: Em votações com confirmação abaixo de 50%, a líder sempre recebe a pergunta explícita sobre manter ou não o ensaio, sem exceção.
- **SC-006**: Toda remarcação de ensaio feita pela líder resulta em aviso claro de remarcação para 100% dos integrantes e no início de uma nova rodada de votação.

## Assumptions

- A lista de "todos os integrantes" corresponde ao conjunto de membros da banda já cadastrados/reconhecidos pelo bot; a líder não precisa especificar destinatários manualmente a cada ensaio.
- A líder não é contabilizada como uma das votantes no cálculo da porcentagem de "sim" — ela recebe o relatório e decide, mas não vota "sim/não" para seu próprio pedido.
- O sistema suporta múltiplos ensaios com votações independentes ocorrendo em paralelo, cada um com seu próprio prazo de 12 horas e relatório.
- Quando a confirmação fica abaixo de 50% e a líder decide seguir com o ensaio mesmo assim (ou cancelar), essa decisão é comunicada aos integrantes, mas o desenho detalhado dessa notificação de decisão final fica a cargo do planejamento técnico (`/speckit-plan`), não desta especificação.
- "Remarcar" sempre reinicia a votação do zero (todos voltam a pendente); não existe modo de remarcação que preserve votos antigos.
- Cancelamento explícito de um ensaio (sem remarcação para nova data) está fora do escopo desta especificação — o fluxo descrito cobre apenas criação, votação, quórum e remarcação.
