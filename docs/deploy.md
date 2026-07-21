# Deploy no Render a partir do GitHub

## 1. Banco de dados

O free tier de banco do Render é só Postgres. Para manter MySQL (usado em
todas as migrations), use um MySQL gerenciado gratuito externo (Aiven,
Clever Cloud) — ou migre as migrations para sintaxe Postgres, se preferir
usar o banco free nativo do Render.

## 2. Criar o Web Service

1. Suba o repositório para o GitHub.
2. No Render: **New +** → **Web Service** → conecte o repositório.
3. Em **Settings**:
   - **Runtime**: Docker
   - **Dockerfile Path**: `docker/Dockerfile`
   - **Docker Build Context Directory**: `.` (raiz do repositório)
   - **Health Check Path**: `/actuator/health`
4. Em **Environment**, adicione:

   | Nome | Valor |
   |---|---|
   | `SPRING_DATASOURCE_URL` | connection string do MySQL |
   | `SPRING_DATASOURCE_USERNAME` | usuário do banco |
   | `SPRING_DATASOURCE_PASSWORD` | senha do banco |
   | `JWT_SECRET` | string aleatória longa (32+ caracteres) |

   `PORT` e `SPRING_PROFILES_ACTIVE` não precisam ser setados manualmente —
   o Render injeta `PORT` sozinho, e o `Dockerfile` já define
   `SPRING_PROFILES_ACTIVE=prod`.

5. Deploy. Todo `git push` na branch conectada dispara um novo deploy.

## 3. Rodando localmente com Docker (opcional)

```bash
cd docker
JWT_SECRET=seu-segredo docker compose up -d
```

Isso sobe MySQL + Redis + a aplicação, usando o profile `prod` com as
variáveis de ambiente definidas no próprio `docker-compose.yml`. Se você não
tem Docker disponível, veja `docs/testes.md` e rode a aplicação com MySQL
nativo (`brew install mysql`) e o profile `dev` (`application-dev.yml`).

## 4. Gate de deploy via GitHub Actions (opcional)

Por padrão o Render builda e publica a cada push. Para só publicar quando os
testes passarem:

1. No Render: **Settings** → **Deploy Hook** → copie a URL.
2. No GitHub: **Settings** → **Secrets and variables** → **Actions** →
   `RENDER_DEPLOY_HOOK_URL`.
3. Desative o **Auto-Deploy** no Render.

## 5. Limitação do free tier

O serviço gratuito do Render dorme após ~15 min sem tráfego; o próximo
acesso demora uns 30-60s pra acordar. Ótimo para portfólio, não para
disponibilidade constante — migrar de plano depois não exige mudança de
código.
