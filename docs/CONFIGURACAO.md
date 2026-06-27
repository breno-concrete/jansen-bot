# Configuração Completa — Jansen Bot

Guia passo a passo para configurar Evolution API, Google Sheets, Claude API e deploy na Oracle Cloud.

---

## Pré-requisitos

- Java 21 (desenvolvimento local)
- Docker + Docker Compose
- Conta Google (gratuita)
- Conta Anthropic (créditos grátis iniciais)
- Chip/número WhatsApp dedicado ao bot
- Instância Oracle Cloud Free Tier (ARM, Ubuntu 22.04/24.04)

---

## 1. Google Sheets API

### 1.1 Criar projeto no Google Cloud

1. Acesse [Google Cloud Console](https://console.cloud.google.com/)
2. Crie um projeto: **jansen-bot**
3. Ative a API: **Google Sheets API** (menu APIs & Services → Library)

### 1.2 Criar Service Account

1. APIs & Services → Credentials → **Create Credentials** → Service Account
2. Nome: `jansen-bot-sheets`
3. Role: não precisa de role especial
4. Crie uma **Key** → JSON → baixe o arquivo
5. Renomeie para `google-credentials.json` e coloque em `./credentials/`

### 1.3 Criar planilha

1. Crie uma planilha no Google Sheets
2. Adicione as 5 abas conforme [google-sheets-schema.md](google-sheets-schema.md)
3. Preencha a aba **Members** com os 10 membros (telefones reais)
4. Copie o ID da planilha da URL:
   `https://docs.google.com/spreadsheets/d/`**ESTE_ID**`/edit`
5. Compartilhe a planilha com o email da Service Account (campo `client_email` do JSON) como **Editor**

---

## 2. Claude API

1. Acesse [Anthropic Console](https://console.anthropic.com/)
2. Crie uma API Key
3. Copie a key (começa com `sk-ant-...`)
4. Modelo usado: `claude-haiku-4-5-20251001` (econômico, ideal para bot)

---

## 3. Evolution API

A Evolution API roda via Docker Compose (já incluída no projeto).

### 3.1 Configurar variáveis

```bash
cp .env.example .env
# Edite .env com suas chaves
```

### 3.2 Subir serviços

```bash
docker compose up -d
```

### 3.3 Criar instância WhatsApp

```bash
curl -X POST http://localhost:8081/instance/create \
  -H "apikey: sua-chave-evolution" \
  -H "Content-Type: application/json" \
  -d '{"instanceName": "jansen-banda", "qrcode": true, "integration": "WHATSAPP-BAILEYS"}'
```

### 3.4 Conectar WhatsApp (QR Code)

```bash
curl http://localhost:8081/instance/connect/jansen-banda \
  -H "apikey: sua-chave-evolution"
```

Escaneie o QR Code com o WhatsApp do chip dedicado.

### 3.5 Configurar webhook

Após o Spring Boot estar rodando:

```bash
curl -X POST http://localhost:8081/webhook/set/jansen-banda \
  -H "apikey: sua-chave-evolution" \
  -H "Content-Type: application/json" \
  -d '{
    "webhook": {
      "enabled": true,
      "url": "http://jansen-bot:8080/api/webhook/evolution",
      "webhookByEvents": false,
      "events": ["MESSAGES_UPSERT"]
    }
  }'
```

> Em produção na Oracle Cloud, substitua a URL pelo IP interno Docker ou hostname do container.

---

## 4. Deploy na Oracle Cloud (Free Tier ARM)

### 4.1 Criar VM

1. Oracle Cloud Console → Compute → Instances → Create
2. Shape: **Ampere A1** (ARM) — 4 OCPUs, 24 GB RAM (free tier)
3. Image: **Ubuntu 22.04** (aarch64)
4. Adicionar SSH key
5. Abrir portas no Security List:
   - 8080 (Spring Boot)
   - 8081 (Evolution API — opcional, só para admin)
   - 22 (SSH)

### 4.2 Instalar Docker na VM

```bash
ssh ubuntu@SEU_IP

# Atualizar sistema
sudo apt update && sudo apt upgrade -y

# Instalar Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker ubuntu

# Instalar Docker Compose
sudo apt install docker-compose-plugin -y

# Relogar para aplicar grupo docker
exit
ssh ubuntu@SEU_IP
```

### 4.3 Enviar projeto para VM

```bash
# Na sua máquina local
scp -r jansen-bot/ ubuntu@SEU_IP:~/
scp credentials/google-credentials.json ubuntu@SEU_IP:~/jansen-bot/credentials/
```

Ou clone via Git se tiver repositório.

### 4.4 Configurar e subir

```bash
cd ~/jansen-bot
cp .env.example .env
nano .env   # preencher todas as variáveis

# Ajustar EVOLUTION_SERVER_URL para IP público
EVOLUTION_SERVER_URL=http://SEU_IP_PUBLICO:8081

docker compose up -d --build
```

### 4.5 Conectar WhatsApp na VM

Repita os passos 3.3 e 3.4 apontando para `http://SEU_IP:8081`.

### 4.6 Verificar saúde

```bash
curl http://localhost:8080/api/webhook/health
# Resposta: Jansen Bot OK 🎸
```

---

## 5. Desenvolvimento local (sem Docker)

```bash
# Colocar credenciais
mkdir credentials
cp /caminho/google-credentials.json credentials/

# Variáveis de ambiente
export CLAUDE_API_KEY=sk-ant-...
export GOOGLE_SHEETS_ID=1abc...
export GOOGLE_CREDENTIALS_PATH=./credentials/google-credentials.json
export EVOLUTION_API_URL=http://localhost:8081
export EVOLUTION_API_KEY=sua-chave

# Rodar
mvn spring-boot:run
```

---

## 6. Comandos úteis do bot (via WhatsApp)

| Comando (natural)        | Ação                          | Quem pode      |
|--------------------------|-------------------------------|----------------|
| "marca ensaio quinta ou sábado" | Inicia votação de datas | Admin          |
| "1" ou "quinta"          | Vota na opção                 | Todos          |
| "confirmo" / "vou sim"   | Confirma presença             | Todos          |
| "não posso" / "to fora"  | Nega presença                 | Todos          |
| "quem vai?"              | Lista presenças               | Todos          |
| "qual a setlist?"        | Mostra setlist                | Todos          |
| "avisa: ensaio cancelado"| Broadcast                     | Admin          |

---

## 7. Troubleshooting

| Problema | Solução |
|----------|---------|
| Bot não responde | Verifique webhook: `curl http://localhost:8081/webhook/find/jansen-banda -H "apikey: ..."` |
| Erro Google Sheets | Confirme sharing com service account e ID da planilha |
| Claude timeout | Verifique API key e créditos em console.anthropic.com |
| Evolution desconecta | Reconecte via QR Code; chip precisa estar ativo |
| Lembrete não envia | Confirme cron/timezone e formato data `yyyy-MM-dd HH:mm` |

---

## 8. Custos

| Serviço | Custo |
|---------|-------|
| Google Sheets | Grátis |
| Claude Haiku | Créditos grátis iniciais (~$5) |
| Oracle Cloud Free Tier | Grátis (ARM Ampere) |
| Evolution API | Grátis (self-hosted) |
| Chip WhatsApp | Único custo (~R$20/mês pré-pago) |
