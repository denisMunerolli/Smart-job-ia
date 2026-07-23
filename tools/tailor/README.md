# Comparador de currículo x vaga (local, sem envio automático)

Script Python sem dependências externas — só a biblioteca padrão.

**O que ele NÃO faz, de propósito:** não busca vagas na internet, não envia
nada sozinho, não inventa habilidade/experiência que não esteja no JSON de
entrada.

## Como usar

1. Copie `curriculo_exemplo.json` para `meu_curriculo.json` e preencha com
   seus dados reais (mesma estrutura das entidades da Fase 2 do backend —
   `habilidadesTecnicas`, `experiencias`, `formacoes`, `idiomas`,
   `certificacoes`).
2. Cole a descrição da vaga num `.txt`.
3. Rode:
   ```bash
   python3 tailor.py --resume meu_curriculo.json --vaga vaga.txt \
       --empresa "Nome da Empresa" --cargo "Título da vaga" --saida saida/
   ```
4. Revise os 3 arquivos gerados (`analise.md`, `curriculo_ajustado.md`,
   `carta_apresentacao.md`) antes de enviar qualquer coisa.

## Testando com o exemplo incluído

```bash
python3 tailor.py --resume curriculo_exemplo.json --vaga vaga_exemplo.txt \
    --empresa "Empresa X" --cargo "Desenvolvedor Java Pleno" --saida saida_exemplo/
```

## Limitações

- O score é uma estimativa por sobreposição de palavras (TF-IDF + cosseno),
  não uma nota oficial de nenhum ATS/Gupy.
- Comparação por palavra isolada (unigrama) — não reconhece frases como
  unidade ("banco de dados relacional").
