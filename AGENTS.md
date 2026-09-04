# AGENTS.md — Bot WhatsApp da Banda

## Visão geral do projeto
Bot de WhatsApp que organiza ensaios, cultos e eventos de uma banda de 8 pessoas, com ranking mensal de assiduidade ("funcionário do mês").

Stack: Java 21, Spring Boot, Evolution API (integração com WhatsApp), Google Sheets como banco de dados.

## Specs — leia antes de implementar qualquer regra de negócio
Toda regra de negócio nova começa em `specsPAST/<nome-da-feature>.md`, seguindo o template: Objetivo, Comportamento Esperado, Dados, Non-goals, Critérios de Aceite.

Specs existentes:
- `specsPAST/funcionario-do-mes.md`
- `specsPAST/marcacao-evento.md`

**Regra importante:** se uma decisão de negócio não estiver escrita na spec correspondente, não implemente por conta própria — marque como `[PENDENTE]` na spec e pergunte antes de codar.

## Comandos de build e teste
- Build: `./mvnw clean install`
- Testes: `./mvnw test`
<!-- ajuste os comandos acima conforme o setup real do projeto -->

## Padrões de código
- Lógica de negócio sempre na camada Service — nunca direto no Controller.
- DTOs separados de entidades de persistência.
<!-- vá completando essa lista conforme padrões forem se repetindo -->

## Integrações externas
- **Evolution API**: respeitar intervalo de 20 segundos entre mensagens individuais enviadas em sequência (evita bloqueio por rate limit).
- **Google Sheets**: usado como banco de dados. Tratar erro de quota/rate limit da API.

## Segurança
- Ações administrativas (marcar, encerrar, remarcar, cancelar evento) só podem ser executadas pelo número de WhatsApp configurado como admin no `.env` — nunca hardcoded no código.
