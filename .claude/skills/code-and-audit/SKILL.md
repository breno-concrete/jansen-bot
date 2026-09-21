---
name: "code-and-audit"
description: "Claude escreve o código das tasks do spec-001 (testes primeiro, depois implementação) e o Breno audita. Todo código deve ser rastreável a um requisito do spec; nada é inventado — dúvida vira pergunta."
argument-hint: "Opcional: ID(s) da task (ex: T003 ou T003-T004). Sem argumento, pega a próxima task não marcada."
user-invocable: true
disable-model-invocation: false
---

## User Input

```text
$ARGUMENTS
```

## Papéis

- **Claude** escreve testes e implementação.
- **Breno** audita. Ele revisa, questiona e aprova; não escreve a parte lógica.
- Este fluxo substitui o modo "ensinar → Breno escreve → corrigir" para as tasks de `tasks.md`.

## Fontes de verdade (ler ANTES de escrever qualquer linha)

Para a task escolhida, leia só o que ela referencia, mas sempre:

1. `specs/001-votacao-ensaio/tasks.md`: a task, suas dependências e o arquivo alvo.
2. `specs/001-votacao-ensaio/spec.md`: os FR-xxx, Acceptance Scenarios e Edge Cases citados.
3. `specs/001-votacao-ensaio/data-model.md` e `contracts/rehearsal-ports.md`: campos, assinaturas e mapeamentos.
4. `specs/001-votacao-ensaio/research.md`: decisões (D1..Dn) já tomadas. Não reabrir.
5. `.specify/memory/constitution.md` e `AGENTS.md`: padrões do projeto.
6. Código vizinho existente (ex.: `RehearsalService`, `ActionDispatcher`, schedulers) para copiar estilo, nomes e idioma.

## Regras invioláveis

1. **Nada inventado.** Todo comportamento, campo, nome, valor e regra deve vir de um documento acima ou do código existente. Se não está escrito, não existe.
2. **Dúvida = pergunta.** Se o spec for ambíguo, omisso ou contraditório, PARE e pergunte ao Breno (AskUserQuestion) antes de codar aquele ponto. Nunca preencha lacuna com suposição "razoável".
3. **Rastreabilidade.** Todo teste e método público deve citar o requisito que cobre (ex.: `// FR-009`, ou `@DisplayName` mencionando o FR). Se não houver requisito para citar, não escreva.
4. **Escopo mínimo.** Faça só a task pedida. Sem extras, sem refactor fora dela, sem antecipar tasks futuras.
5. **TDD conforme o tasks.md.** Teste primeiro. Rode e mostre que FALHA (Red) pelo motivo certo; só então implemente até passar (Green). Não pule o Red.
6. **Não tocar no legado** além do que a task manda (`ActionDispatcher`, `RehearsalService`). O `RehearsalServiceCharacterizationTest` deve continuar verde.
7. **Estilo local.** Comentários, nomes (domínio em português, como no spec) e idioma seguem o código vizinho.
8. **Sem commit** a menos que o Breno peça.

## Fluxo por task

1. **Escolher**: a task de `$ARGUMENTS` ou a primeira `- [ ]` cujas dependências estejam marcadas.
2. **Ler** as fontes de verdade e montar a lista: FR/cenários cobertos e pontos ambíguos.
3. **Perguntar** sobre ambiguidades (se houver) e esperar resposta.
4. **Codar** seguindo TDD; rodar com `JAVA_HOME=C:\Users\breno\.jdks\ms-21.0.10-1` (o projeto não tem `mvnw`, usar `mvn`).
5. **Relatório de auditoria** para o Breno, curto, neste formato:
   - **Task**: ID e título.
   - **Arquivos** criados/alterados.
   - **Rastreabilidade**: tabela `requisito → onde foi implementado/testado`.
   - **Resultado dos testes**: Red observado, Green final, baseline do legado.
   - **Decisões/pontos a conferir**: qualquer escolha que ele deva olhar com atenção, e o que foi perguntado e respondido.
6. **Aguardar aprovação.** Só marcar `- [x]` no `tasks.md` depois que o Breno aprovar (ou pedir para marcar).
7. Não avançar sozinho para a próxima task; ofereça-a.

## Ao final

Se algo do spec estiver desatualizado ou contraditório com o que foi decidido, aponte para o Breno atualizar o spec (`/speckit-clarify`). Não corrija o spec por conta própria.
