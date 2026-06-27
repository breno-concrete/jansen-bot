# Jansen Bot 🎸

Bot de WhatsApp para gerenciamento de banda musical com 10 membros.

## Stack

- **Java 21** + **Spring Boot 3.x** + **Maven**
- **Evolution API** — integração WhatsApp
- **Claude API** (Haiku 4.5) — processamento de linguagem natural
- **Google Sheets** — banco de dados (custo zero)
- **Docker Compose** — Evolution API + PostgreSQL + Spring Boot

## Funcionalidades

1. Agendamento de ensaio com votação de datas
2. Confirmação de presença (quem vai / quem não vai)
3. Broadcast de avisos para todos os membros
4. Lembrete automático 24h antes do ensaio
5. Gestão de setlist
6. **Cadastro de shows com lembretes (3d e 1d antes)**
7. **Registro de chegada e ranking de pontualidade**
8. **Alertas de faltas consecutivas (privado ao admin)**
9. **Relatório mensal de presença com pódio**
10. **Votação membro do mês**
11. **Contador de ensaios realizados**
12. **Sugestão e aprovação de músicas + repertório**
13. Linguagem 100% português, tom informal

## Início rápido

```bash
# 1. Configurar variáveis
cp .env.example .env

# 2. Colocar credenciais Google
mkdir credentials
cp /caminho/google-credentials.json credentials/

# 3. Subir tudo
docker compose up -d --build

# 4. Conectar WhatsApp (ver docs/CONFIGURACAO.md)
```

## Documentação

- [Configuração completa](docs/CONFIGURACAO.md) — Evolution, Google Sheets, Claude, Oracle Cloud
- [Schema Google Sheets](docs/google-sheets-schema.md) — estrutura das abas originais
- [Novas abas Google Sheets](docs/google-sheets-schema-novas-abas.md) — Shows, Arrivals, Votos, etc.
- [Exemplo de fluxo original](docs/exemplo-fluxo.md) — "não posso quinta" passo a passo
- [Exemplos das novas features](docs/exemplos-features.md) — fluxos completos por feature

## Arquitetura

```
WhatsApp → Evolution API → WebhookController → Claude API
                                    ↓
                              ActionDispatcher
                                    ↓
                           Google Sheets + Evolution API → WhatsApp
```

## Estrutura do projeto

```
jansen-bot/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── .env.example
├── docs/
│   ├── CONFIGURACAO.md
│   ├── google-sheets-schema.md
│   └── exemplo-fluxo.md
└── src/main/
    ├── java/com/jansen/bot/
    │   ├── JansenBotApplication.java
    │   ├── config/          # WebClient, Google Sheets, properties
    │   ├── controller/      # Webhook REST
    │   ├── client/          # Evolution, Claude HTTP clients
    │   ├── repository/      # Google Sheets CRUD
    │   ├── service/         # Lógica de negócio
    │   ├── scheduler/       # Lembretes 24h
    │   ├── model/           # DTOs e records
    │   └── util/            # PhoneUtils
    └── resources/
        ├── application.properties
        └── system-prompt.txt
```

## Licença

Uso interno da banda.
