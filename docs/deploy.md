# Deploy (Railway ou Render, a partir do GitHub)

## Banco: PostgreSQL

O projeto usa PostgreSQL (migrado de MySQL para aproveitar o banco gerenciado
gratuito nativo do Railway/Render, que é Postgres). Crie o serviço de banco
direto na plataforma:
- **Railway**: `+ New` → `Database` → `Add PostgreSQL`
- **Render**: `New +` → `PostgreSQL`

## Configuração do serviço da aplicação

- Build: Dockerfile em `docker/Dockerfile`, contexto = raiz do repositório.
- Health check: `/actuator/health`.
- Variáveis de ambiente obrigatórias: `SPRING_DATASOURCE_URL`,
  `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`.
- `PORT` e `SPRING_PROFILES_ACTIVE=prod` já são tratados automaticamente.
- **Não** defina um `startCommand` customizado apontando para
  `smartjobai-api/target/...` — esse caminho só existe na etapa de build do
  Dockerfile multi-stage. Deixe o `ENTRYPOINT` do próprio Dockerfile cuidar
  disso.
- No Railway, verifique também se o campo **Dockerfile Path** nas
  configurações do serviço (dashboard) não ficou sobrescrito com um valor
  antigo — ele tem prioridade sobre o `railway.json` do repositório.

### SPRING_DATASOURCE_URL no Railway (referenciando o serviço Postgres)

```
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
```

Troque `Postgres` pelo nome exato do serviço de banco no seu projeto Railway,
se for diferente.

## Local com Docker (opcional)

```bash
cd docker && JWT_SECRET=seu-segredo docker compose up -d
```

## Limitação de free tier

Serviços gratuitos costumam dormir após alguns minutos sem tráfego —
aceitável para portfólio, não para disponibilidade constante.
