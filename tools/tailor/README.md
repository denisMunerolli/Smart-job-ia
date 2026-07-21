# SmartJobAI — Comparador de currículo x vaga (local, sem envio automático)

Script local em Python (sem dependências externas — só a biblioteca padrão)
que compara seu currículo com a descrição de uma vaga, aponta o que bate e o
que falta, e gera rascunhos de currículo reordenado e carta de apresentação.

**O que ele NÃO faz, de propósito:**
- não busca vagas na internet
- não envia nada (email, formulário, candidatura) sozinho
- não inventa habilidade ou experiência que não esteja no seu JSON de entrada

## Requisitos

Python 3.9 ou mais recente. Nenhuma instalação de pacote é necessária.

## Como usar

1. **Copie `curriculo_exemplo.json`** para `meu_curriculo.json` e preencha com
   seus dados reais. A estrutura é a mesma das entidades da Fase 2 do
   SmartJobAI (`habilidadesTecnicas`, `experiencias`, `formacoes`, `idiomas`,
   `certificacoes`) — se um dia você conectar isto ao backend Java, dá pra
   gerar esse JSON direto do endpoint `GET /api/usuarios/me`.

2. **Copie a descrição da vaga** (do site da empresa, Gupy, LinkedIn — o que
   for) e cole num arquivo de texto, por exemplo `vaga.txt`.

3. **Rode o script:**
   ```bash
   python3 tailor.py --resume meu_curriculo.json --vaga vaga.txt \
       --empresa "Nome da Empresa" --cargo "Título da vaga" --saida saida/
   ```

4. **Veja os 3 arquivos gerados em `saida/`:**
   - `analise.md` — score de compatibilidade e palavras-chave que batem/faltam
   - `curriculo_ajustado.md` — suas habilidades e experiências reais,
     reordenadas para destacar o que é mais relevante para *essa* vaga
   - `carta_apresentacao.md` — rascunho de carta com trechos para você
     completar (marcados entre colchetes)

5. **Revise tudo antes de enviar.** Principalmente a lista de "palavras-chave
   que faltam": só adicione ao currículo o que for verdade. Se for algo que
   você domina mas esqueceu de cadastrar, é a hora de atualizar. Se não for
   verdade, não force — isso costuma aparecer na entrevista técnica.

## Testando com o exemplo incluído

```bash
python3 tailor.py --resume curriculo_exemplo.json --vaga vaga_exemplo.txt \
    --empresa "Empresa X" --cargo "Desenvolvedor Java Pleno" --saida saida_exemplo/
```

## Limitações conhecidas

- O "score" é uma estimativa por sobreposição de palavras (TF-IDF +
  similaridade de cosseno, o mesmo princípio do `TFIDFMatcher` do módulo
  `smartjobai-ai`), não uma nota oficial de nenhum ATS/Gupy — cada
  plataforma tem seu próprio algoritmo de triagem, que não é público.
- Comparação é por palavra isolada (unigrama), então não reconhece frases
  como "banco de dados relacional" como uma unidade — é uma limitação
  aceitável para um script simples, mas fique atento a falsos negativos.
- O nome/empresa da vaga não é extraído automaticamente do texto — passe via
  `--empresa`/`--cargo` para a carta sair mais completa.
