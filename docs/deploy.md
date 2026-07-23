# Deploy (Railway ou Render, a partir do GitHub)

## Banco

Ambas plataformas priorizam Postgres no free tier. O projeto usa MySQL
(JPA + Flyway) — use um MySQL gerenciado (Railway tem plugin de um clique;
Aiven/Clever Cloud são alternativas gratuitas) ou migre as migrations para
Postgres.

## Configuração do serviço

- Build: Dockerfile em `docker/Dockerfile`, contexto = raiz do repositório.
- Health check: `/actuator/health`.
- Variáveis de ambiente obrigatórias: `SPRING_DATASOURCE_URL`,
  `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`.
- `PORT` e `SPRING_PROFILES_ACTIVE=prod` já são tratados automaticamente
  (a plataforma injeta `PORT`; o `Dockerfile` já define o profile).
- **Não** defina um `startCommand` customizado apontando para
  `smartjobai-api/target/...` — esse caminho só existe na etapa de build do
  Dockerfile multi-stage, não na imagem final (que só tem `/app/app.jar`).
  Deixe o `ENTRYPOINT` do próprio Dockerfile cuidar disso.

## Local com Docker (opcional)

```bash
cd docker && JWT_SECRET=seu-segredo docker compose up -d
```

## Limitação de free tier

Serviços gratuitos costumam dormir após alguns minutos sem tráfego —
aceitável para portfólio, não para disponibilidade constante.
