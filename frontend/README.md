# SmartJobAI — Frontend

React + Vite + Tailwind CSS

## Desenvolvimento local

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

O frontend roda em `http://localhost:5173` e faz proxy para o backend em `http://localhost:8080`.

## Build para produção

```bash
npm run build
# Arquivos gerados em: frontend/dist/
```

## Deploy (Vercel / Netlify)

1. Aponte para a pasta `frontend/`
2. Defina a variável de ambiente:
   ```
   VITE_API_URL=https://smartjobai-api-production.up.railway.app
   ```
3. Build command: `npm run build`
4. Output directory: `dist`

## Páginas

| Rota               | Página             |
|--------------------|--------------------|
| `/login`           | Login              |
| `/register`        | Cadastro           |
| `/`                | Dashboard          |
| `/vagas`           | Busca de vagas     |
| `/vagas/:id`       | Detalhe da vaga    |
| `/candidaturas`    | Minhas candidaturas|
| `/curriculos`      | Meus currículos    |
| `/matching`        | Matching IA        |
| `/perfil`          | Meu perfil         |
