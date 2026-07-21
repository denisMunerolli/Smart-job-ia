# Banco de dados

Schema versionado com Flyway. Migrations em
`smartjobai-core/src/main/resources/db/migration/`, aplicadas automaticamente
na subida da aplicação (`spring.flyway.enabled: true` em `application.yml`).

| Migration | Tabela(s) | Observação |
|---|---|---|
| V1 | `usuarios` | inclui os campos de rede/portfólio da Fase 2 |
| V2 | `curriculos` | |
| V3 | `formacoes` | índice em `usuario_id` |
| V4 | `experiencias` + `experiencia_tecnologias` | índices em `usuario_id`, `cargo` e `tecnologia` (usado pelo dashboard de palavras-chave, Fase 7/9) |
| V5 | `idiomas` | |
| V6 | `certificacoes` | |
| V7 | `habilidades_tecnicas` | índice em `nome` (busca) |
| V8 | `vagas` | índice composto em `(fonte, id_externo)` para evitar importação duplicada |

`spring.jpa.hibernate.ddl-auto` está como `validate`: o Hibernate confere que
as entidades batem com o schema, mas quem cria/altera tabelas é sempre o
Flyway. Se você seguiu a sugestão original de gerar o schema via
`schema.sql`/`data.sql` do Hibernate, remova essa geração automática — as
duas abordagens juntas conflitam.
