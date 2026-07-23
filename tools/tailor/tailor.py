#!/usr/bin/env python3
"""
tailor.py - Compara seu currículo (JSON) com a descrição de uma vaga e gera:
  1) analise.md              - score de compatibilidade, palavras-chave que batem e que faltam
  2) curriculo_ajustado.md   - suas habilidades/experiências reordenadas por relevância p/ a vaga
  3) carta_apresentacao.md   - rascunho de carta, pra você revisar antes de enviar

IMPORTANTE: este script NÃO envia nada, NÃO acessa a internet e NÃO inventa
habilidades ou experiências. Ele só reordena e destaca o que já está no seu
currículo, e aponta lacunas honestamente para você decidir o que fazer.

Uso:
    python3 tailor.py --resume curriculo_exemplo.json --vaga vaga_exemplo.txt \
        --empresa "Empresa X" --cargo "Desenvolvedor Java Pleno" --saida saida/
"""

import argparse
import json
import math
import re
import sys
import unicodedata
from collections import Counter
from pathlib import Path

STOPWORDS_PT = {
    "a", "o", "as", "os", "de", "da", "do", "das", "dos", "em", "um", "uma",
    "uns", "umas", "para", "com", "por", "que", "e", "é", "ao", "aos", "à",
    "às", "na", "no", "nas", "nos", "se", "sua", "seu", "suas", "seus",
    "como", "mais", "menos", "muito", "muita", "muitos", "muitas", "ou",
    "mas", "também", "já", "não", "sim", "este", "esta", "isso", "isto",
    "essa", "esse", "será", "são", "foi", "ser", "estar", "tem", "têm",
    "ter", "tendo", "você", "nós", "eles", "elas", "ele", "ela", "lhe",
    "lhes", "até", "sobre", "entre", "após", "antes", "durante", "cada",
    "todo", "toda", "todos", "todas", "outro", "outra", "outros", "outras",
    "qual", "quais", "quando", "onde", "porque", "pois", "assim", "nossa",
    "nosso", "nossos", "nossas", "pelo", "pela", "pelos", "pelas", "num",
    "numa", "nesse", "nessa", "neste", "nesta", "isso", "aquele", "aquela",
}

RUIDO_VAGA_PT = {
    "buscamos", "procuramos", "requisitos", "requisito", "desejavel",
    "diferencial", "diferenciais", "oferecemos", "vaga", "vagas", "atuar",
    "atuacao", "construindo", "mantendo", "contribuir", "colaborativo",
    "ambiente", "plano", "carreira", "remoto", "presencial", "hibrido",
    "time", "equipe", "conhecimento", "conhecimentos", "familiaridade",
    "vivencia", "solida", "solido", "leitura", "documentacao", "junior",
    "pleno", "senior", "trabalho", "trabalhar",
}


def normalizar(texto: str) -> str:
    texto = texto.lower()
    texto = unicodedata.normalize("NFD", texto)
    texto = "".join(c for c in texto if unicodedata.category(c) != "Mn")
    texto = re.sub(r"[^a-z0-9\s+#./-]", " ", texto)
    texto = re.sub(r"\s+", " ", texto).strip()
    return texto


def tokenizar(texto: str) -> list:
    normalizado = normalizar(texto or "")
    tokens = []
    for bruto in normalizado.split(" "):
        t = bruto.strip(".,;:()[]\"'-")
        if t and t not in STOPWORDS_PT and t not in RUIDO_VAGA_PT and len(t) > 1:
            tokens.append(t)
    return tokens


def calcular_tf(tokens: list) -> dict:
    contagem = Counter(tokens)
    total = len(tokens) or 1
    return {termo: freq / total for termo, freq in contagem.items()}


def calcular_idf(documentos_tokens: list) -> dict:
    n_docs = len(documentos_tokens)
    contagem_doc = Counter()
    for tokens in documentos_tokens:
        for termo in set(tokens):
            contagem_doc[termo] += 1
    return {termo: math.log((n_docs + 1) / (df + 1)) + 1 for termo, df in contagem_doc.items()}


def vetor_tfidf(tokens: list, idf: dict) -> dict:
    tf = calcular_tf(tokens)
    return {termo: valor * idf.get(termo, 1.0) for termo, valor in tf.items()}


def similaridade_cosseno(v1: dict, v2: dict) -> float:
    termos = set(v1) | set(v2)
    dot = sum(v1.get(t, 0.0) * v2.get(t, 0.0) for t in termos)
    norm1 = math.sqrt(sum(v * v for v in v1.values()))
    norm2 = math.sqrt(sum(v * v for v in v2.values()))
    if norm1 == 0 or norm2 == 0:
        return 0.0
    return dot / (norm1 * norm2)


def montar_texto_curriculo(dados: dict) -> str:
    partes = [dados.get("resumoProfissional", "")]
    for h in dados.get("habilidadesTecnicas", []):
        partes.append(h.get("nome", ""))
    for e in dados.get("experiencias", []):
        partes.append(e.get("cargo", ""))
        partes.append(e.get("empresa", ""))
        partes.append(e.get("descricao", ""))
        partes.extend(e.get("tecnologias", []))
    for f in dados.get("formacoes", []):
        partes.append(f.get("curso", ""))
        partes.append(f.get("instituicao", ""))
    return " ".join(p for p in partes if p)


def extrair_palavras_chave_vaga(texto_vaga: str, top_n: int = 25) -> list:
    tokens = tokenizar(texto_vaga)
    contagem = Counter(tokens)
    return [termo for termo, _ in contagem.most_common(top_n)]


def reordenar_habilidades(habilidades: list, palavras_chave: set) -> list:
    def bate(h):
        return 0 if set(tokenizar(h.get("nome", ""))) & palavras_chave else 1
    return sorted(habilidades, key=bate)


def reordenar_experiencias(experiencias: list, palavras_chave: set) -> list:
    def relevancia(e):
        termos_exp = set(tokenizar(" ".join(e.get("tecnologias", []) + [e.get("cargo", "")])))
        return -len(termos_exp & palavras_chave)
    return sorted(experiencias, key=relevancia)


def gerar_analise_md(score: float, batem: list, faltam: list) -> str:
    linhas = [
        "# Análise de compatibilidade com a vaga\n",
        f"**Score de similaridade (TF-IDF + cosseno):** {score * 100:.1f}%\n",
        "> Isto é uma estimativa por sobreposição de palavras-chave, não uma nota oficial de nenhum ATS.\n",
        "## Palavras-chave da vaga que já aparecem no seu currículo\n",
    ]
    linhas.append(", ".join(sorted(batem)) if batem else "_nenhuma correspondência direta encontrada_")
    linhas.append("\n## Palavras-chave da vaga que NÃO aparecem no seu currículo\n")
    linhas.append(", ".join(sorted(faltam)) if faltam else "_seu currículo já cobre praticamente tudo_")
    linhas.append(
        "\n> Para cada item da lista acima: só adicione ao currículo se for verdade. "
        "Se você tem a habilidade mas não registrou, é aqui que vale atualizar o cadastro. "
        "Se não tem, não insira — isso é o tipo de coisa que aparece na entrevista."
    )
    return "\n".join(linhas)


def gerar_curriculo_md(dados: dict, habilidades_ordenadas: list, experiencias_ordenadas: list) -> str:
    linhas = [f"# {dados.get('nome', '')}\n"]
    contato = " | ".join(filter(None, [dados.get("email"), dados.get("linkedinUrl"), dados.get("githubUrl")]))
    if contato:
        linhas.append(f"{contato}\n")
    if dados.get("resumoProfissional"):
        linhas.append("## Resumo\n" + dados["resumoProfissional"] + "\n")

    linhas.append("## Habilidades técnicas (reordenadas por relevância para a vaga)\n")
    for h in habilidades_ordenadas:
        anos = f" — {h['anosExperiencia']} ano(s)" if h.get("anosExperiencia") else ""
        linhas.append(f"- {h.get('nome', '')} ({h.get('nivelProficiencia', '')}){anos}")

    linhas.append("\n## Experiência profissional (reordenada por relevância para a vaga)\n")
    for e in experiencias_ordenadas:
        periodo = f"{e.get('dataInicio', '')} – {'atual' if e.get('atual') else e.get('dataFim', '')}"
        linhas.append(f"### {e.get('cargo', '')} — {e.get('empresa', '')} ({periodo})")
        if e.get("descricao"):
            linhas.append(e["descricao"])
        if e.get("tecnologias"):
            linhas.append("**Tecnologias:** " + ", ".join(e["tecnologias"]))
        linhas.append("")

    if dados.get("formacoes"):
        linhas.append("## Formação\n")
        for f in dados["formacoes"]:
            linhas.append(f"- {f.get('curso', '')} — {f.get('instituicao', '')} ({f.get('nivel', '')})")

    if dados.get("idiomas"):
        linhas.append("\n## Idiomas\n")
        for i in dados["idiomas"]:
            linhas.append(f"- {i.get('nome', '')}: {i.get('nivel', '')}")

    if dados.get("certificacoes"):
        linhas.append("\n## Certificações\n")
        for c in dados["certificacoes"]:
            linhas.append(f"- {c.get('nome', '')} — {c.get('instituicaoEmissora', '')}")

    return "\n".join(linhas)


def gerar_carta_md(dados: dict, empresa: str, cargo: str, batem: list, experiencias_ordenadas: list) -> str:
    nome = dados.get("nome", "")
    top_exp = experiencias_ordenadas[0] if experiencias_ordenadas else None
    destaques = ", ".join(sorted(batem)[:6]) if batem else "suas principais tecnologias"

    corpo = [
        "# Rascunho de carta de apresentação — REVISE ANTES DE ENVIAR\n",
        f"Prezados(as) recrutadores(as) da {empresa or '[EMPRESA]'},\n",
        f"Me chamo {nome} e tenho interesse na vaga de {cargo or '[CARGO]'}. "
        f"Ao ler a descrição, identifiquei forte aderência com minha experiência em {destaques}.\n",
    ]

    if top_exp:
        corpo.append(
            f"Na {top_exp.get('empresa', '')}, atuei como {top_exp.get('cargo', '')} "
            f"e trabalhei diretamente com {', '.join(top_exp.get('tecnologias', [])[:5]) or 'as tecnologias listadas no currículo'}. "
            "[Revise esta frase e adicione um resultado concreto, com número se possível — "
            "ex: prazo reduzido, bug crítico resolvido, sistema entregue.]\n"
        )

    corpo.append(
        "Fico à disposição para conversar sobre como minha experiência pode contribuir com o time. "
        "Segue meu currículo em anexo.\n"
    )
    corpo.append(f"Atenciosamente,\n{nome}")
    return "\n".join(corpo)


def main():
    parser = argparse.ArgumentParser(description="Compara currículo com vaga e gera rascunhos para revisão manual.")
    parser.add_argument("--resume", required=True, help="Caminho do JSON com os dados do currículo")
    parser.add_argument("--vaga", required=True, help="Caminho do .txt com a descrição da vaga")
    parser.add_argument("--empresa", default="", help="Nome da empresa (opcional, usado na carta)")
    parser.add_argument("--cargo", default="", help="Nome do cargo (opcional, usado na carta)")
    parser.add_argument("--top-keywords", type=int, default=25, help="Quantas palavras-chave extrair da vaga")
    parser.add_argument("--saida", default="saida", help="Diretório de saída dos arquivos gerados")
    args = parser.parse_args()

    resume_path = Path(args.resume)
    vaga_path = Path(args.vaga)
    if not resume_path.exists():
        sys.exit(f"Arquivo de currículo não encontrado: {resume_path}")
    if not vaga_path.exists():
        sys.exit(f"Arquivo de vaga não encontrado: {vaga_path}")

    dados = json.loads(resume_path.read_text(encoding="utf-8"))
    texto_vaga = vaga_path.read_text(encoding="utf-8")

    texto_curriculo = montar_texto_curriculo(dados)
    tokens_curriculo = tokenizar(texto_curriculo)
    tokens_vaga = tokenizar(texto_vaga)

    idf = calcular_idf([tokens_curriculo, tokens_vaga])
    score = similaridade_cosseno(vetor_tfidf(tokens_curriculo, idf), vetor_tfidf(tokens_vaga, idf))

    palavras_chave_vaga = extrair_palavras_chave_vaga(texto_vaga, args.top_keywords)
    tokens_curriculo_set = set(tokens_curriculo)
    batem = [p for p in palavras_chave_vaga if p in tokens_curriculo_set]
    faltam = [p for p in palavras_chave_vaga if p not in tokens_curriculo_set]

    palavras_chave_set = set(palavras_chave_vaga)
    habilidades_ordenadas = reordenar_habilidades(dados.get("habilidadesTecnicas", []), palavras_chave_set)
    experiencias_ordenadas = reordenar_experiencias(dados.get("experiencias", []), palavras_chave_set)

    saida_dir = Path(args.saida)
    saida_dir.mkdir(parents=True, exist_ok=True)

    (saida_dir / "analise.md").write_text(gerar_analise_md(score, batem, faltam), encoding="utf-8")
    (saida_dir / "curriculo_ajustado.md").write_text(
        gerar_curriculo_md(dados, habilidades_ordenadas, experiencias_ordenadas), encoding="utf-8"
    )
    (saida_dir / "carta_apresentacao.md").write_text(
        gerar_carta_md(dados, args.empresa, args.cargo, batem, experiencias_ordenadas), encoding="utf-8"
    )

    print(f"Score de compatibilidade: {score * 100:.1f}%")
    print(f"Palavras-chave que batem ({len(batem)}): {', '.join(sorted(batem)) or '-'}")
    print(f"Palavras-chave que faltam ({len(faltam)}): {', '.join(sorted(faltam)) or '-'}")
    print(f"\nArquivos gerados em: {saida_dir.resolve()}")
    print("  - analise.md")
    print("  - curriculo_ajustado.md")
    print("  - carta_apresentacao.md")
    print("\nRevise os três arquivos antes de enviar qualquer coisa.")


if __name__ == "__main__":
    main()
