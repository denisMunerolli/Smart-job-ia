# SmartJobAI 🚀

Plataforma inteligente de matching entre candidatos e vagas de emprego, usando **TF-IDF + similaridade de cosseno** para calcular compatibilidade currículo × vaga em tempo real.

**Produção:** https://smartjobai-api-production.up.railway.app

---

## 🏗️ Arquitetura

```
smartjobai/
├── smartjobai-commons        # Utilitários (TextUtils, stopwords PT/EN)
├── smartjobai-core           # Entidades JPA, repositórios, serviços de domínio
├── smartjobai-infrastructure # Conectores externos: Adzuna API, RSS feeds
├── smartjobai-ai             # Algoritmo TF-IDF + cosseno + habilidadesFaltantes()
├── smartjobai-batch          # Job de importação automática (6h e 18h UTC)
├── smartjobai-api            # Controllers REST, JWT, Swagger, app principal
└── frontend/                 # React 18 + Vite 5 + Tailwind CSS
```

**Stack:** Java 21 · Spring Boot 3 · PostgreSQL 15 · Flyway · JWT (jjwt) · React 18 · Vite 5 · Tailwind CSS  
**Hosting:** Railway (backend + banco) · Vercel (frontend) · GitHub Actions (CI/CD)

---

## ✨ Funcionalidades

### Backend
- ✅ Autenticação JWT (login, register, token Bearer)
- ✅ Gestão de perfil, formações, experiências, idiomas
- ✅ Currículos com versionamento automático e ativação exclusiva
- ✅ Busca de vagas paginada com filtros (título, empresa, localização)
- ✅ Workflow de candidaturas com 7 status (PENDENTE → APROVADA)
- ✅ Matching TF-IDF por texto livre ou por IDs (currículo × vaga)
- ✅ **Vagas recomendadas** — top 10 vagas rankeadas por score do currículo ativo
- ✅ **Estatísticas do usuário** — totais e candidaturas agrupadas por status
- ✅ Importação automática via Adzuna API e RSS feeds (cron 6h/18h)
- ✅ Importação manual via `/api/admin/vagas/importar`

### Frontend
- ✅ **Dashboard** com cards reais, gráfico de candidaturas (donut) e vagas recomendadas com score
- ✅ Listagem de vagas paginada com filtros
- ✅ Detalhe da vaga com cálculo de matching e botão de candidatura
- ✅ **Candidaturas** com funil visual de progresso, filtro por status e paginação
- ✅ Gerenciamento de currículos (CRUD + ativação)
- ✅ Página de Matching IA (modo texto livre ou por IDs)
- ✅ Edição de perfil

---

## 🔌 Endpoints principais

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/api/auth/login` | Login → JWT |
| POST | `/api/auth/register` | Cadastro |
| GET | `/api/usuarios/me` | Perfil do usuário |
| GET | `/api/usuarios/me/stats` | Estatísticas (totais + status) |
| GET | `/api/usuarios/me/vagas/recomendadas` | Top vagas por score TF-IDF |
| GET | `/api/vagas` | Listar vagas (filtros + paginação) |
| GET | `/api/vagas/{id}` | Detalhe da vaga |
| POST | `/api/matching` | Calcular matching (texto livre ou IDs) |
| GET | `/api/usuarios/me/candidaturas` | Listar candidaturas (paginado) |
| POST | `/api/usuarios/me/candidaturas` | Candidatar-se |
| PUT | `/api/usuarios/me/candidaturas/{id}/status` | Atualizar status |
| GET | `/api/usuarios/me/curriculos` | Listar currículos |
| POST | `/api/usuarios/me/curriculos` | Criar currículo |
| PUT | `/api/usuarios/me/curriculos/{id}/ativar` | Ativar currículo |
| POST | `/api/admin/vagas/importar` | Importar vagas manualmente |

Documentação interativa: `{base_url}/swagger-ui.html`

---

## 🚀 Rodando localmente

### Backend

```bash
# 1. Clonar
git clone https://github.com/denisMunerolli/Smart-job-ia.git
cd Smart-job-ia

# 2. Subir PostgreSQL via Docker
docker run -d --name pg -e POSTGRES_DB=smartjobai \
  -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:15

# 3. Compilar todos os módulos
mvn clean install -DskipTests

# 4. Configurar variáveis e rodar
cd smartjobai-api
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/smartjobai
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export JWT_SECRET=$(openssl rand -hex 32)
mvn spring-boot:run
```

Acesse: http://localhost:8080/swagger-ui.html

### Frontend

```bash
cd frontend
npm install
cp .env.example .env          # VITE_API_URL=http://localhost:8080
npm run dev                   # http://localhost:5173
```

---

## ⚙️ Variáveis de ambiente (Railway)

| Variável | Descrição |
|----------|-----------|
| `SPRING_DATASOURCE_URL` | URL do PostgreSQL Railway |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco |
| `JWT_SECRET` | Segredo JWT (≥ 32 chars — use `openssl rand -hex 32`) |
| `SERVER_URL` | URL pública do backend |
| `CORS_ALLOWED_ORIGINS` | URL do frontend (Vercel) |
| `ADZUNA_APP_ID` | ID da API Adzuna (developer.adzuna.com) |
| `ADZUNA_APP_KEY` | Key da API Adzuna |
| `ADZUNA_COUNTRY` | País de busca (ex: `br`) |
| `RSS_VAGAS_URL` | URL de feed RSS (opcional) |
| `IMPORT_TERMOS` | Termos de busca separados por vírgula |
| `IMPORT_LOCALIZACAO` | Localização padrão para importação |

---

## 🧪 Testes

```bash
# Unitários (rápido, sem Docker)
mvn test -pl smartjobai-core,smartjobai-ai

# Todos (requer Docker para Testcontainers)
mvn verify
```

Cobertura: CandidaturaService, VagaService, CurriculoService, TFIDFMatcher, UsuarioService (Testcontainers).

---

## 🔄 CI/CD

Todo push para `main` dispara o pipeline GitHub Actions:
1. Sobe PostgreSQL de teste
2. Compila todos os módulos (`mvn clean verify`)
3. Railway faz redeploy automático em caso de sucesso

---

## 📊 Migrations Flyway

| Versão | Descrição |
|--------|-----------|
| V1 | Tabela `usuarios` |
| V2 | Tabela `formacoes` |
| V3 | Tabela `experiencias` |
| V4 | Tabela `idiomas` |
| V5 | Tabela `certificacoes` |
| V6 | Tabela `curriculos` |
| V7 | Tabela `vagas` |
| V8 | Colunas de auditoria (`created_at`, `updated_at`) |
| V9 | No-op (compatibilidade) |
| V10 | Tabela `candidaturas` |

---

*Atualizado automaticamente — SmartJobAI v1.0*
