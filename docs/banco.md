# Banco de dados

Schema versionado com Flyway. Migrations em
`smartjobai-core/src/main/resources/db/migration/`. Banco: **PostgreSQL**
(migrado de MySQL — veja `docs/deploy.md` para o motivo).

| Migration | Tabela(s) | Observação |
|---|---|---|
| V1 | `usuarios` | inclui os campos de rede/portfólio da Fase 2 |
| V2 | `curriculos` | |
| V3 | `formacoes` | índice em `usuario_id` |
| V4 | `experiencias` + `experiencia_tecnologias` | índices em `usuario_id`, `cargo`, `tecnologia` |
| V5 | `idiomas` | |
| V6 | `certificacoes` | |
| V7 | `habilidades_tecnicas` | índice em `nome` |
| V8 | `vagas` | índice composto `(fonte, id_externo)` |

`ddl-auto: validate` — o Hibernate confere, o Flyway cria/altera.

Diferenças de sintaxe em relação à versão MySQL original: `BIGSERIAL` no
lugar de `BIGINT AUTO_INCREMENT`, `TIMESTAMP` no lugar de `DATETIME`, `TEXT`
no lugar de `LONGTEXT`, sem `ENGINE=InnoDB`/`DEFAULT CHARSET` (não existem
no Postgres).
