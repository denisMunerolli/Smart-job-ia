package com.smartjobai.ai.similarity;

import com.smartjobai.ai.classifier.SkillClassifier;
import com.smartjobai.ai.classifier.SkillClassifier.TermCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Motor de matching multidimensional.
 *
 * Score final = 25% hard skills + 25% qualificações + 15% experiência
 *             + 15% educação + 10% preferred + 10% TF-IDF
 *
 * Isso representa com muito mais precisão a compatibilidade real
 * entre candidato e vaga do que TF-IDF puro.
 */
@Component
@RequiredArgsConstructor
public class MultiDimensionalMatcher {

    private final TFIDFMatcher tfidfMatcher;
    private final SkillClassifier classifier;

    public record MatchingDetalhado(
            int scoreGeral,
            int hardSkills,
            int qualificacoesRequeridas,
            int experiencia,
            int educacao,
            int preferencias,
            int similaridadeTexto,
            String nivel,
            List<String> hardSkillsFaltantes,
            List<String> skillsContexto,
            List<String> todasHabilidadesFaltantes
    ) {}

    public MatchingDetalhado calcular(String textoVaga, String textoCurriculo) {
        // Tokenizar ambos os textos
        Set<String> termosVaga      = tokenizar(textoVaga);
        Set<String> termosCurriculo = tokenizar(textoCurriculo);

        // Separar por categoria
        Set<String> hardSkillsVaga = filtrarCategoria(termosVaga, TermCategory.HARD_SKILL);
        Set<String> hardSkillsCv   = filtrarCategoria(termosCurriculo, TermCategory.HARD_SKILL);

        // Hard skills faltantes (só tecnologias reais)
        List<String> hardSkillsFaltantes = hardSkillsVaga.stream()
                .filter(t -> !hardSkillsCv.contains(t))
                .sorted()
                .collect(Collectors.toList());

        // Palavras de contexto identificadas (para mostrar ao usuário)
        List<String> termosContexto = termosVaga.stream()
                .filter(t -> classifier.isJobCondition(t))
                .sorted()
                .collect(Collectors.toList());

        // Habilidades faltantes reais (sem contexto)
        List<String> todasFaltantes = tfidfMatcher.habilidadesFaltantes(textoVaga, textoCurriculo)
                .stream()
                .filter(t -> !classifier.isJobCondition(t))
                .filter(t -> t.length() >= 3)
                .collect(Collectors.toList());

        // 1. Hard Skills (25%) — % de hard skills da vaga presentes no CV
        int scoreHardSkills = hardSkillsVaga.isEmpty() ? 80 :
                (int) Math.round((double)(hardSkillsVaga.size() - hardSkillsFaltantes.size())
                        / hardSkillsVaga.size() * 100);

        // 2. Qualificações requeridas (25%) — baseado em hard skills + termos técnicos
        int scoreQualificacoes = calcularQualificacoes(termosVaga, termosCurriculo);

        // 3. Experiência (15%) — detecta menção de projetos, anos, experiência
        int scoreExperiencia = calcularExperiencia(textoCurriculo);

        // 4. Educação (15%) — detecta menção de graduação, curso, etc.
        int scoreEducacao = calcularEducacao(textoCurriculo);

        // 5. Preferências (10%) — soft skills e tecnologias opcionais
        int scorePreferencias = calcularPreferencias(termosVaga, termosCurriculo);

        // 6. TF-IDF (10%) — similaridade textual bruta
        double tfidf = tfidfMatcher.calcularSimilaridade(textoVaga, textoCurriculo);
        int scoreTfidf = (int) Math.round(tfidf * 100);

        // Score final ponderado
        int scoreGeral = (int) Math.round(
                scoreHardSkills     * 0.25 +
                scoreQualificacoes  * 0.25 +
                scoreExperiencia    * 0.15 +
                scoreEducacao       * 0.15 +
                scorePreferencias   * 0.10 +
                scoreTfidf          * 0.10
        );

        // Limitar entre 0 e 100
        scoreGeral = Math.min(100, Math.max(0, scoreGeral));

        String nivel = scoreGeral >= 70 ? "ALTO" : scoreGeral >= 40 ? "MEDIO" : "BAIXO";

        return new MatchingDetalhado(
                scoreGeral,
                scoreHardSkills,
                scoreQualificacoes,
                scoreExperiencia,
                scoreEducacao,
                scorePreferencias,
                scoreTfidf,
                nivel,
                hardSkillsFaltantes,
                termosContexto,
                todasFaltantes
        );
    }

    private Set<String> filtrarCategoria(Set<String> termos, TermCategory categoria) {
        return termos.stream()
                .filter(t -> classifier.classify(t) == categoria)
                .collect(Collectors.toSet());
    }

    private Set<String> tokenizar(String texto) {
        if (texto == null || texto.isBlank()) return new HashSet<>();
        return Arrays.stream(texto.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s\\-+#]", " ")
                .split("\\s+"))
                .filter(t -> t.length() >= 2)
                .collect(Collectors.toSet());
    }

    private int calcularQualificacoes(Set<String> termosVaga, Set<String> termosCv) {
        long totalTecnicos = termosVaga.stream()
                .filter(t -> !classifier.isJobCondition(t))
                .count();
        if (totalTecnicos == 0) return 75;
        long presentes = termosVaga.stream()
                .filter(t -> !classifier.isJobCondition(t))
                .filter(termosCv::contains)
                .count();
        return (int) Math.round((double) presentes / totalTecnicos * 100);
    }

    private int calcularExperiencia(String curriculo) {
        String cv = curriculo.toLowerCase();
        int score = 40; // base
        if (cv.contains("project") || cv.contains("projeto")) score += 15;
        if (cv.contains("production") || cv.contains("produção")) score += 20;
        if (cv.contains("developed") || cv.contains("desenvolveu") || cv.contains("built")) score += 10;
        if (cv.contains("year") || cv.contains("ano")) score += 10;
        if (cv.contains("team") || cv.contains("equipe")) score += 5;
        return Math.min(100, score);
    }

    private int calcularEducacao(String curriculo) {
        String cv = curriculo.toLowerCase();
        int score = 50; // base
        if (cv.contains("bachelor") || cv.contains("bacharelado") || cv.contains("graduação")) score += 30;
        if (cv.contains("software engineering") || cv.contains("computer science") ||
            cv.contains("engenharia de software") || cv.contains("ciência da computação")) score += 20;
        if (cv.contains("master") || cv.contains("mestrado") || cv.contains("phd")) score += 20;
        return Math.min(100, score);
    }

    private int calcularPreferencias(Set<String> termosVaga, Set<String> termosCv) {
        // Termos que não são hard skills nem condições — soft skills / ferramentas gerais
        Set<String> preferencias = termosVaga.stream()
                .filter(t -> classifier.classify(t) == TermCategory.GENERAL)
                .filter(t -> t.length() > 4)
                .collect(Collectors.toSet());
        if (preferencias.isEmpty()) return 70;
        long presentes = preferencias.stream().filter(termosCv::contains).count();
        return (int) Math.round((double) presentes / preferencias.size() * 100);
    }
}
