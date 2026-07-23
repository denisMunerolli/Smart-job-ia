# Testes

## Java

- `*UnitTest.java` — Mockito, sem Spring context, sem banco. `./scripts/run-tests.sh java`
- `*Test.java` — `@SpringBootTest` + Testcontainers, MySQL real via Docker.
  `./scripts/run-tests.sh java-all` (exige Docker rodando; sem Docker local,
  o GitHub Actions roda igual a cada push).

## Python

`tools/tailor/test_tailor.py` — só `unittest`, zero dependências externas.
`./scripts/run-tests.sh python`

## CI

`.github/workflows/ci.yml` roda `mvn clean verify` a cada push/PR.
