# Testes

## Java

Dois estilos, propositalmente separados:

- **`*UnitTest.java`** (`FormacaoServiceUnitTest`, `ExperienciaServiceUnitTest`)
  — Mockito, sem Spring context, sem banco. Rodam em milissegundos.
  `./scripts/run-tests.sh java`
- **`*Test.java`** (`UsuarioServiceTest`, `FormacaoServiceTest`)
  — `@SpringBootTest` + Testcontainers, sobem um MySQL real via Docker.
  Mais lentos, mas validam comportamento de verdade contra o banco (query,
  constraint, etc.). `./scripts/run-tests.sh java-all` — **exige Docker
  rodando**. Se sua máquina não roda Docker, esses testes ainda passam no
  GitHub Actions a cada push (os runners do GitHub já vêm com Docker).

Cobertura atual: `Usuario` e `Formacao`. O mesmo padrão (mock do repositório
+ `UsuarioService` mockado) se aplica diretamente a `Experiencia`, `Idioma`,
`Certificacao` e `HabilidadeTecnica` — só falta escrever.

## Python

`tools/tailor/test_tailor.py` — só biblioteca padrão (`unittest`), sem
instalar nada. Cobre normalização/tokenização, similaridade de cosseno,
reordenação por relevância, e um smoke test que roda o `tailor.py` de
verdade via linha de comando.

```bash
./scripts/run-tests.sh python
```

## CI

`.github/workflows/ci.yml` roda `mvn clean verify` a cada push/PR — inclui
os testes com Testcontainers, já que os runners do GitHub têm Docker
disponível por padrão.
