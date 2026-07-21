# SmartJobAI — testes pelo terminal (sem Eclipse/VS Code)

## Python (`python/`)

Não precisa instalar nada, é só biblioteca padrão.

```bash
cd python
./run-tests.sh
# ou, direto:
python3 -m unittest test_tailor.py -v
```

11 testes: normalização/tokenização (incluindo o bug do "boot." que a gente
corrigiu), similaridade de cosseno, reordenação por relevância, e um smoke
test que roda o `tailor.py` de verdade via linha de comando e confere se os
3 arquivos de saída foram gerados. Roda em menos de 1 segundo.

Copie `test_tailor.py` para dentro da sua pasta `smartjobai-tailor/` (junto
com `tailor.py`, `curriculo_exemplo.json` e `vaga_exemplo.txt`) — ele espera
esses arquivos como vizinhos.

## Java (`java/`)

Copie `FormacaoServiceUnitTest.java` e `ExperienciaServiceUnitTest.java`
para `smartjobai-core/src/test/java/com/smartjobai/core/service/` (mesma
pasta do `FormacaoServiceTest.java` que já existe).

### ⚠️ Antes de rodar: falta uma dependência no `pom.xml` — sem ela nem compila

O `UsuarioService` usa `PasswordEncoder` diretamente, mas o `pom.xml` do
módulo `smartjobai-core` (como veio no esqueleto original) não declara
nenhuma dependência que forneça essa classe. Sem isso, `smartjobai-core`
não compila — nem os testes, nem o build normal. Adicione em
`smartjobai-core/pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

E, para os testes (unitários e os com Testcontainers) compilarem:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mysql</artifactId>
    <scope>test</scope>
</dependency>
```

(As versões já são resolvidas pelo `testcontainers-bom` que está no `pom.xml`
pai — não precisa especificar versão nessas duas últimas.)

### Rodando

```bash
cd java   # depois de copiar os arquivos pro lugar certo no seu repo
./run-tests.sh          # só os testes unitários — rápido, sem Docker
./run-tests.sh all      # suíte completa, incluindo Testcontainers (precisa do Docker rodando)
```

**Diferença entre os dois tipos de teste:**
- `*UnitTest.java` (novos) — mocka o repositório com Mockito, não sobe
  Spring context nem banco. Roda em milissegundos, ótimo pra rodar toda
  hora enquanto você mexe no código.
- `FormacaoServiceTest.java` / `UsuarioServiceTest.java` (já existiam) —
  sobem um MySQL real via Testcontainers. Mais lentos e exigem Docker
  ativo, mas validam o comportamento de verdade contra o banco (query,
  constraint, etc.). Vale rodar antes de um commit importante, não a cada
  salvamento.

Nenhum dos dois precisa de Eclipse ou VS Code abertos — só terminal, JDK e
Maven (e Docker, no caso do `all`).
