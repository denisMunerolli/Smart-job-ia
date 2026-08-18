<div align="center">

# SmartJobAI

**Plataforma inteligente de matching entre candidatos e vagas de emprego**

[![CI](https://github.com/denisMunerolli/Smart-job-ia/actions/workflows/ci.yml/badge.svg)](https://github.com/denisMunerolli/Smart-job-ia/actions/workflows/ci.yml)
[![Deploy](https://github.com/denisMunerolli/Smart-job-ia/actions/workflows/deploy-pages.yml/badge.svg)](https://github.com/denisMunerolli/Smart-job-ia/actions/workflows/deploy-pages.yml)
[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green?logo=spring)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue?logo=react)](https://react.dev/)
[![Railway](https://img.shields.io/badge/Deploy-Railway-purple?logo=railway)](https://railway.app/)

**[🚀 Acessar o App](https://denismunerolli.github.io/Smart-job-ia/)** &nbsp;|&nbsp; **[📖 API Docs (Swagger)](https://smartjobai-api-production-9e0b.up.railway.app/swagger-ui.html)**

</div>

---

## O que é o SmartJobAI?

O SmartJobAI é uma plataforma full-stack de matching inteligente entre candidatos e vagas de emprego. Diferente de sistemas que usam apenas similaridade textual (TF-IDF puro), o SmartJobAI implementa um **motor de matching multidimensional** que avalia o candidato em 6 dimensões distintas — separando habilidades técnicas reais de palavras de contexto como "remote", "flexible" e "benefits".

---

## Funcionalidades

### 🎯 Matching Multidimensional
Avaliação ponderada em 6 dimensões:

| Dimensão | Peso | O que avalia |
|---|---|---|
| Hard Skills técnicas | 25% | Java, Spring, Docker, PostgreSQL... |
| Qualificações requeridas | 25% | Termos técnicos presentes na vaga |
| Experiência | 15% | Projetos, aplicações em produção |
| Educação | 15% | Graduação, área de formação |
| Qualificações preferidas | 10% | Diferenciais opcionais |
| Similaridade TF-IDF | 10% | Sobreposição textual geral |

### 🤖 Otimização de Currículo com IA
Integração com a API do Claude (Anthropic) para reescrever o currículo automaticamente alinhado à vaga, destacando habilidades relevantes e usando a terminologia exata da descrição.

### 💼 Importação Automática de Vagas
- **Adzuna API** — vagas reais do mercado brasileiro (cron 6h e 18h UTC)
- **RemoteOK** — vagas remotas internacionais (API pública gratuita)
- **RSS feeds** — feeds públicos configuráveis via variável de ambiente

### 📊 Dashboard com Dados Reais
- Totais de vagas, candidaturas e currículos
- Gráfico donut de candidaturas por status
- Top 10 vagas recomendadas com score de compatibilidade

### 🔐 Autenticação JWT
- Access token de 7 dias
- Refresh token de 30 dias
- Renovação automática no frontend (sem logout forçado)

---

## Arquitetura

```
smartjobai/
├── smartjobai-commons        # Utilitários compartilhados (TextUtils)
├── smartjobai-core           # Entidades JPA, repositórios, serviços de domínio
├── smartjobai-ai             # Motor TF-IDF + SkillClassifier + MultiDimensionalMatcher
├── smartjobai-infrastructure # Conectores externos: Adzuna, RemoteOK, RSS
├── smartjobai-batch          # Jobs agendados de importação de vagas
├── smartjobai-api            # Controllers REST, segurança JWT, app principal
└── frontend/                 # React 18 + Vite 5 + Tailwind CSS
```

**Stack:**
- **Backend:** Java 21, Spring Boot 3, Spring Security, Spring Data JPA, Hibernate
- **Banco de dados:** PostgreSQL 15 + Flyway migrations
- **IA/ML:** TF-IDF próprio + SkillClassifier + MultiDimensionalMatcher + Claude API
- **Frontend:** React 18, Vite 5, Tailwind CSS, React Router, Axios
- **Infraestrutura:** Railway (backend + banco), GitHub Pages (frontend), GitHub Actions (CI/CD)

---

## Endpoints principais

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/auth/register` | Cadastro de usuário |
| POST | `/api/auth/login` | Login → JWT (7 dias) + refresh token (30 dias) |
| POST | `/api/auth/refresh` | Renovar access token |
| GET | `/api/usuarios/me/stats` | Estatísticas do usuário |
| GET | `/api/usuarios/me/recomendacoes` | Top vagas por score de compatibilidade |
| GET | `/api/vagas` | Listar vagas com filtros e paginação |
| POST | `/api/matching` | Calcular matching (texto livre ou por IDs) |
| POST | `/api/curriculos/otimizar/texto` | Otimizar currículo com IA para uma vaga |
| POST | `/api/usuarios/me/candidaturas` | Candidatar-se a uma vaga |
| POST | `/api/admin/vagas/importar` | Importar vagas manualmente |

Documentação completa: [Swagger UI](https://smartjobai-api-production-9e0b.up.railway.app/swagger-ui.html)

---

## Rodando localmente

### Pré-requisitos
- Java 21+
- Maven 3.9+
- Node.js 20+
- Docker (para PostgreSQL local)

### Backend

```bash
# 1. Clonar o repositório
git clone https://github.com/denisMunerolli/Smart-job-ia.git
cd Smart-job-ia

# 2. Subir PostgreSQL
docker run -d --name pg \
  -e POSTGRES_DB=smartjobai \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 postgres:15

# 3. Compilar
mvn clean install -DskipTests

# 4. Configurar e rodar
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/smartjobai
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export JWT_SECRET=$(openssl rand -hex 32)
cd smartjobai-api && mvn spring-boot:run
```

Acesse: http://localhost:8080/swagger-ui.html

### Frontend

```bash
cd frontend
npm install
cp .env.example .env   # edite VITE_API_URL=http://localhost:8080
npm run dev            # http://localhost:5173
```

---

## Variáveis de ambiente (produção)

| Variável | Descrição |
|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC do PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Segredo JWT (mínimo 32 caracteres) |
| `SERVER_URL` | URL pública do backend |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas (frontend) |
| `ADZUNA_APP_ID` | ID da API Adzuna |
| `ADZUNA_APP_KEY` | Chave da API Adzuna |
| `ADZUNA_COUNTRY` | País de busca (ex: `br`) |
| `IMPORT_TERMOS` | Termos de busca separados por vírgula |
| `ANTHROPIC_API_KEY` | Chave da API Claude (otimização de currículo) |

---

## CI/CD

Todo push para `main` executa automaticamente:
1. **CI** — build Maven + testes unitários com PostgreSQL de teste
2. **Deploy backend** — Railway detecta o push e faz redeploy via Dockerfile
3. **Deploy frontend** — GitHub Actions faz build React e publica no GitHub Pages

---

## Migrations Flyway

| Versão | Descrição |
|--------|-----------|
| V1 | Tabela `usuarios` |
| V2 | Tabela `curriculos` |
| V3 | Tabela `formacoes` |
| V4 | Tabela `experiencias` |
| V5 | Tabela `idiomas` |
| V6 | Tabela `certificacoes` |
| V7 | Tabela `habilidades_tecnicas` |
| V8 | Tabela `vagas` |
| V9 | Colunas de auditoria |
| V10 | Tabela `candidaturas` |
| V11 | Seed de vagas com descrição completa |

---

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

<div align="center">
Desenvolvido por <a href="https://github.com/denisMunerolli">Denis Munerolli</a>
</div>
