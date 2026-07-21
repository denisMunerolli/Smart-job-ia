# SmartJobAI

Backend em Java/Spring Boot (multi-módulo Maven) para gestão de currículo,
com um utilitário Python separado para comparar currículo x vaga.

**Estado atual:** Fase 1 (fundação) + Fase 2 (cadastro completo) implementadas
e testáveis. Fases 3+ (banco de currículos versionado, IA adaptativa, busca
de vagas real, dashboard) seguem como roadmap — ver `docs/arquitetura.md`.

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

Motivo da separação em módulos, e uma dependência circular que ela evita
(e o porquê disso afetar como os testes são organizados): `docs/arquitetura.md`.

## Rodando localmente (sem Docker)

```bash
brew install mysql
brew services start mysql
mysql -u root -e "CREATE DATABASE smartjobai;"

mvn clean install -DskipTests
cd smartjobai-api
mvn spring-boot:run   # profile "dev" por padrão (application.yml)
```

`POST /api/auth/register` para criar um usuário, depois `POST /api/auth/login`
para pegar o token JWT. Endpoints do cadastro completo em
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

`docs/deploy.md` — Render a partir do GitHub, com CI em
`.github/workflows/ci.yml`.

## Ferramenta de tailoring de currículo (`tools/tailor/`)

Script Python standalone, zero dependências externas, que compara seu
currículo com a descrição de uma vaga e gera rascunhos de currículo
ajustado e carta de apresentação — sem enviar nada automaticamente e sem
inventar habilidades que não estejam no seu currículo de entrada. Detalhes
em `tools/tailor/README.md` — ainda não integrado ao backend Java.

## Gaps corrigidos nesta consolidação

Ao juntar tudo num repositório coerente, alguns problemas do esqueleto
original apareceram e foram corrigidos — listados com o motivo em
`docs/arquitetura.md` e nos comentários dos arquivos afetados:
`BusinessException`/`ResourceNotFoundException` inexistentes,
`CustomUserDetailsService` ausente (login não funcionava), dependência de
`PasswordEncoder` faltando no `pom.xml` do `core`, `pom.xml` de
`infrastructure`/`batch` nunca criados, `VagaRepository` referenciado mas
nunca definido, `Vaga` com `@Builder` sem `@NoArgsConstructor` (quebra o
Hibernate), e ausência de contexto Spring próprio para os testes do módulo
`core`.
