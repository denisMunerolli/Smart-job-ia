# SmartJobAI

Backend em Java/Spring Boot (multi-módulo Maven) para gestão de currículo,
com um utilitário Python separado para comparar currículo x vaga.

**Estado atual:** Fase 1 (fundação) + Fase 2 (cadastro completo)
implementadas e testáveis. Fases 3+ seguem como roadmap.

## Módulos

```
smartjobai-commons        utilitários sem dependência de domínio
smartjobai-core           entidades, repositórios, regras de negócio
smartjobai-infrastructure conectores externos (vagas)
smartjobai-ai              matching por TF-IDF
smartjobai-batch          jobs agendados
smartjobai-api             controllers, DTOs, segurança JWT, main class
tools/tailor               script Python: compara currículo x descrição de vaga
```

Detalhes de arquitetura (e uma dependência circular que ela evita):
`docs/arquitetura.md`.

## Rodando localmente (sem Docker)

```bash
brew install mysql
brew services start mysql
mysql -u root -e "CREATE DATABASE smartjobai;"

mvn clean install -DskipTests
cd smartjobai-api
mvn spring-boot:run
```

`POST /api/auth/register` → `POST /api/auth/login` → token JWT →
`/api/usuarios/me/**` (formações, experiências, idiomas, certificações,
habilidades técnicas).

## Rodando com Docker (opcional)

```bash
cd docker && JWT_SECRET=seu-segredo docker compose up -d
```

## Testes — só terminal, sem IDE

```bash
./scripts/run-tests.sh          # unitários Java (rápido, sem Docker) + Python
./scripts/run-tests.sh java-all # suíte completa Java, incluindo Testcontainers (precisa Docker)
```

Detalhes: `docs/testes.md`.

## Deploy

`docs/deploy.md` — Railway/Render a partir do GitHub, CI em
`.github/workflows/ci.yml`.

## Ferramenta de tailoring de currículo (`tools/tailor/`)

Script Python standalone que compara currículo x vaga e gera rascunhos —
sem enviar nada automaticamente. `tools/tailor/README.md`.

## Gaps corrigidos ao longo do desenvolvimento

`BusinessException`/`ResourceNotFoundException` inexistentes,
`CustomUserDetailsService` ausente (login não funcionava), dependência de
`PasswordEncoder` faltando no `pom.xml` do `core`, `pom.xml` de
`infrastructure`/`batch` nunca criados, `VagaRepository` referenciado mas
nunca definido, `Vaga` com `@Builder` sem `@NoArgsConstructor` (quebra o
Hibernate), ausência de contexto Spring próprio para os testes do módulo
`core`, `startCommand` de deploy apontando para um caminho que não existe na
imagem Docker final. Detalhes em `docs/arquitetura.md` e `docs/deploy.md`.
