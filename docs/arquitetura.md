# Arquitetura

## Por que multi-módulo Maven

O projeto é dividido em 6 módulos com responsabilidades separadas:

| Módulo | Responsabilidade |
|---|---|
| `smartjobai-commons` | Utilitários sem dependência de domínio (`TextUtils`) |
| `smartjobai-core` | Entidades JPA, repositórios, regras de negócio (services), exceções |
| `smartjobai-infrastructure` | Integrações externas (conectores de vaga) |
| `smartjobai-ai` | Algoritmos de matching (TF-IDF + similaridade de cosseno) |
| `smartjobai-batch` | Jobs agendados (importação de vagas) |
| `smartjobai-api` | Camada HTTP: controllers, DTOs, segurança JWT, aplicação principal |

**Regra de dependência:** `api` depende de todos os outros; nenhum módulo
"de baixo" depende de `api`. Isso permite trocar a implementação de IA (de
TF-IDF para embeddings, por exemplo) sem tocar em controller ou entidade.

## Consequência prática nos testes

`core` não pode depender de `api` (seria circular). Como a única classe
`@SpringBootApplication` do projeto vive em `api`, os testes de integração
que ficam fisicamente em `core` (`UsuarioServiceTest`, `FormacaoServiceTest`)
precisam de um contexto Spring próprio — é o que `CoreTestApplication`
resolve. Sem essa classe, `@SpringBootTest` não encontra onde subir o
contexto e os testes falham antes mesmo de rodar qualquer asserção.

## Inconsistência conhecida (documentada de propósito)

`Usuario` e as entidades da Fase 2 estendem `EntidadeBase`
(id/dataCriacao/dataAtualizacao centralizados). `Curriculo` e `Vaga` ainda
seguem o padrão original da Fase 1 (`@Data` direto). Migrar essas duas é bom
primeiro passo ao implementar a Fase 3 (Banco de Currículos).

## Camada de segurança

- `SecurityConfig` (api): define quais rotas exigem autenticação.
- `JwtTokenProvider` / `JwtAuthenticationFilter` (api): emissão e validação do token.
- `CustomUserDetailsService` (api): carrega o `Usuario` do banco para o Spring Security.
- `PasswordEncoder`: bean em `SecurityConfig` (api). Como `UsuarioService`
  (core) o usa diretamente, `smartjobai-core/pom.xml` também precisa de
  `spring-boot-starter-security`.
