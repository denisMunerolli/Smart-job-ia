package com.smartjobai.api.dto;

import java.util.List;

/**
 * Resultado do matching TF-IDF entre currículo e vaga.
 *
 * score: 0.0 a 1.0 (similaridade por cosseno)
 * scorePercentual: score * 100, arredondado para exibição
 * habilidadesFaltantes: termos relevantes na vaga ausentes no currículo
 * nivel: classificação textual do score (BAIXO / MÉDIO / ALTO)
 */
public record MatchingResult(
        double score,
        int scorePercentual,
        String nivel,
        List<String> habilidadesFaltantes
) {
    public static MatchingResult of(double score, List<String> habilidadesFaltantes) {
        int percentual = (int) Math.round(score * 100);
        String nivel = percentual >= 70 ? "ALTO" : percentual >= 40 ? "MÉDIO" : "BAIXO";
        return new MatchingResult(score, percentual, nivel, habilidadesFaltantes);
    }
}
