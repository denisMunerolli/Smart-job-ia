"""
Testes do tailor.py — só biblioteca padrão, roda direto pelo terminal:

    python3 -m unittest test_tailor.py -v

Sem instalar nada, sem Docker, sem IDE.
"""

import subprocess
import sys
import unittest
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))

from tailor import (  # noqa: E402
    normalizar,
    tokenizar,
    similaridade_cosseno,
    reordenar_habilidades,
    reordenar_experiencias,
)


class TestNormalizacaoETokenizacao(unittest.TestCase):

    def test_normalizar_remove_acentos_e_minusculas(self):
        self.assertEqual(normalizar("Programação Ágil"), "programacao agil")

    def test_tokenizar_remove_pontuacao_de_borda(self):
        # regressão do bug "boot." (ponto de fim de frase grudado no token)
        tokens = tokenizar("Trabalhamos com Spring Boot.")
        self.assertIn("boot", tokens)
        self.assertNotIn("boot.", tokens)

    def test_tokenizar_remove_ruido_de_anuncio_de_vaga(self):
        tokens = tokenizar("Buscamos um desenvolvedor com os seguintes requisitos")
        self.assertNotIn("buscamos", tokens)
        self.assertNotIn("requisitos", tokens)

    def test_tokenizar_preserva_termos_tecnicos_compostos(self):
        tokens = tokenizar("Experiência com Node.js e C#")
        self.assertIn("node.js", tokens)
        self.assertIn("c#", tokens)

    def test_tokenizar_ignora_string_vazia(self):
        self.assertEqual(tokenizar(""), [])
        self.assertEqual(tokenizar(None), [])


class TestSimilaridadeCosseno(unittest.TestCase):

    def test_vetores_identicos_dao_similaridade_maxima(self):
        v = {"java": 1.0, "spring": 0.5}
        self.assertAlmostEqual(similaridade_cosseno(v, v), 1.0, places=6)

    def test_vetores_disjuntos_dao_zero(self):
        self.assertEqual(similaridade_cosseno({"java": 1.0}, {"python": 1.0}), 0.0)

    def test_vetor_vazio_nao_quebra(self):
        self.assertEqual(similaridade_cosseno({}, {"java": 1.0}), 0.0)
        self.assertEqual(similaridade_cosseno({}, {}), 0.0)


class TestReordenacao(unittest.TestCase):

    def test_habilidade_que_bate_com_a_vaga_vem_primeiro(self):
        habilidades = [{"nome": "Excel"}, {"nome": "Java"}, {"nome": "Word"}]
        resultado = reordenar_habilidades(habilidades, {"java"})
        self.assertEqual(resultado[0]["nome"], "Java")

    def test_experiencia_com_mais_tecnologias_em_comum_vem_primeiro(self):
        experiencias = [
            {"cargo": "Estágio Administrativo", "tecnologias": ["Excel"]},
            {"cargo": "Dev Java", "tecnologias": ["Java", "Spring", "MySQL"]},
        ]
        resultado = reordenar_experiencias(experiencias, {"java", "spring", "mysql"})
        self.assertEqual(resultado[0]["cargo"], "Dev Java")


class TestExecucaoDeParaCompleta(unittest.TestCase):
    """Smoke test: chama o script pela linha de comando, do jeito que você usaria de verdade."""

    def test_script_roda_e_gera_os_tres_arquivos_esperados(self):
        base = Path(__file__).resolve().parent
        saida = base / "_saida_teste_automatico"

        resultado = subprocess.run(
            [
                sys.executable, str(base / "tailor.py"),
                "--resume", str(base / "curriculo_exemplo.json"),
                "--vaga", str(base / "vaga_exemplo.txt"),
                "--saida", str(saida),
            ],
            capture_output=True, text=True,
        )

        self.assertEqual(resultado.returncode, 0, msg=resultado.stderr)
        self.assertTrue((saida / "analise.md").exists())
        self.assertTrue((saida / "curriculo_ajustado.md").exists())
        self.assertTrue((saida / "carta_apresentacao.md").exists())

        # limpeza dos arquivos gerados pelo próprio teste
        for arquivo in saida.glob("*"):
            arquivo.unlink()
        saida.rmdir()


if __name__ == "__main__":
    unittest.main()
