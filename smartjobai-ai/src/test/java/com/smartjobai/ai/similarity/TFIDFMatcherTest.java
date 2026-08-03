package com.smartjobai.ai.similarity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TFIDFMatcherTest {

    private final TFIDFMatcher matcher = new TFIDFMatcher();

    @Test
    void textosIdenticos_devemRetornarSimilaridade1() {
        String texto = "desenvolvedor java spring boot microsservicos";
        double score = matcher.calcularSimilaridade(texto, texto);
        assertThat(score).isEqualTo(1.0);
    }

    @Test
    void textosSemRelacao_devemRetornarSimilaridadeBaixa() {
        double score = matcher.calcularSimilaridade(
                "cozinheiro chefe gastronomia restaurante",
                "desenvolvedor java backend spring boot"
        );
        assertThat(score).isLessThan(0.1);
    }

    @Test
    void textosComAlgumaRelacao_devemRetornarSimilaridadeMedia() {
        double score = matcher.calcularSimilaridade(
                "desenvolvedor java spring boot aws docker kubernetes",
                "engenheiro software java spring microsservicos"
        );
        assertThat(score).isGreaterThan(0.3);
    }

    @Test
    void habilidadesFaltantes_deveRetornarTermesDaVagaAusentesNoCurriculo() {
        String vaga = "desenvolvedor java spring boot kubernetes docker aws";
        String curriculo = "desenvolvedor java spring boot";

        List<String> faltantes = matcher.habilidadesFaltantes(vaga, curriculo);

        assertThat(faltantes).contains("kubernetes", "docker", "aws");
        assertThat(faltantes).doesNotContain("java", "spring", "boot");
    }

    @Test
    void habilidadesFaltantes_textosIdenticos_deveRetornarListaVazia() {
        String texto = "desenvolvedor java spring boot";
        List<String> faltantes = matcher.habilidadesFaltantes(texto, texto);
        assertThat(faltantes).isEmpty();
    }

    @Test
    void calcularSimilaridade_textosNulos_naoDeveLancarExcecao() {
        // textos nulos são tratados como vazios — retorna 0
        double score = matcher.calcularSimilaridade(null, null);
        assertThat(score).isEqualTo(0.0);
    }
}
