package com.smartjobai.ai.similarity;

import com.smartjobai.ai.classifier.SkillClassifier;
import com.smartjobai.ai.classifier.SkillClassifier.TermCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Motor de matching multidimensional v2.
 *
 * Melhorias sobre v1:
 * - Experiência: compara anos exigidos pela vaga × anos encontrados no CV
 * - Educação: verifica se o grau exigido está presente no CV
 * - SkillClassifier com 9 categorias (vs 4 anteriores)
 * - Termos de contexto (JOB_CONDITION, LANGUAGE, EXPERIENCE) não poluem o score técnico
 *
 * Pesos:
 *   Hard Skills         25%
 *   Qualificações       25%
 *   Experiência         15%  ← agora compara vaga × CV
 *   Educação            15%  ← agora compara vaga × CV
 *   Preferências        10%
 *   Similaridade TF-IDF 10%
 */
@Component
@RequiredArgsConstructor
public class MultiDimensionalMatcher {

    private final TFIDFMatcher tfidfMatcher;
    private final SkillClassifier classifier;

    // Padrões para extração de anos de experiência
    private static final Pattern ANOS_PATTERN = Pattern.compile(
        "(\\d+)\\+?\\s*(?:year|years|ano|anos)(?:\\s+of)?(?:\\s+experience|\\s+experiencia)?",
        Pattern.CASE_INSENSITIVE
    );

    // Padrões para nível de senioridade
    private static final Pattern NIVEL_PATTERN = Pattern.compile(
        "\\b(junior|jr|entry.level|mid.level|pleno|senior|sr|lead|principal|staff|architect)\\b",
        Pattern.CASE_INSENSITIVE
    );

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
            List<String> todasHabilidadesFaltantes,
            ExperienciaComparacao experienciaDetalhe,
            EducacaoComparacao educacaoDetalhe
    ) {}

    public record ExperienciaComparacao(
            int anosExigidos,
            int anosEncontrados,
            String nivelExigido,
            String nivelEncontrado,
            boolean satisfeito
    ) {}

    public record EducacaoComparacao(
            String grauExigido,
            boolean grauEncontrado,
            String areaExigida,
            boolean areaEncontrada
    ) {}

    public MatchingDetalhado calcular(String textoVaga, String textoCurriculo) {
        Set<String> termosVaga = tokenizar(textoVaga);
        Set<String> termosCv   = tokenizar(textoCurriculo);

        // Hard skills
        Set<String> hardSkillsVaga = filtrarCategoria(termosVaga, TermCategory.HARD_SKILL);
        Set<String> hardSkillsCv   = filtrarCategoria(termosCv,   TermCategory.HARD_SKILL);
        List<String> hardSkillsFaltantes = hardSkillsVaga.stream()
                .filter(t -> !hardSkillsCv.contains(t))
                .sorted()
                .collect(Collectors.toList());

        // Termos de contexto identificados
        List<String> termosContexto = termosVaga.stream()
                .filter(t -> classifier.isJobCondition(t) || classifier.isLanguage(t))
                .sorted()
                .collect(Collectors.toList());

        // Habilidades gerais faltantes (sem contexto, sem experiência, sem educação)
        List<String> todasFaltantes = tfidfMatcher.habilidadesFaltantes(textoVaga, textoCurriculo)
                .stream()
                .filter(t -> !classifier.isJobCondition(t))
                .filter(t -> !classifier.isExperience(t))
                .filter(t -> !classifier.isEducation(t))
                .filter(t -> !classifier.isLanguage(t))
                .filter(t -> t.length() >= 3)
                .collect(Collectors.toList());

        // ── 1. Hard Skills (25%) ──────────────────────────────────────────
        int scoreHardSkills = hardSkillsVaga.isEmpty() ? 75 :
                (int) Math.round(
                    (double)(hardSkillsVaga.size() - hardSkillsFaltantes.size())
                    / hardSkillsVaga.size() * 100
                );

        // ── 2. Qualificações requeridas (25%) ─────────────────────────────
        int scoreQualificacoes = calcularQualificacoes(termosVaga, termosCv);

        // ── 3. Experiência (15%) — compara vaga × CV ─────────────────────
        ExperienciaComparacao expDetalhe = compararExperiencia(textoVaga, textoCurriculo);
        int scoreExperiencia = calcularScoreExperiencia(expDetalhe);

        // ── 4. Educação (15%) — compara vaga × CV ────────────────────────
        EducacaoComparacao eduDetalhe = compararEducacao(textoVaga, textoCurriculo);
        int scoreEducacao = calcularScoreEducacao(eduDetalhe);

        // ── 5. Preferências (10%) ─────────────────────────────────────────
        int scorePreferencias = calcularPreferencias(termosVaga, termosCv);

        // ── 6. TF-IDF (10%) ───────────────────────────────────────────────
        double tfidf = tfidfMatcher.calcularSimilaridade(textoVaga, textoCurriculo);
        int scoreTfidf = (int) Math.round(tfidf * 100);

        // ── Score final ponderado ─────────────────────────────────────────
        int scoreGeral = (int) Math.round(
                scoreHardSkills     * 0.25 +
                scoreQualificacoes  * 0.25 +
                scoreExperiencia    * 0.15 +
                scoreEducacao       * 0.15 +
                scorePreferencias   * 0.10 +
                scoreTfidf          * 0.10
        );
        scoreGeral = Math.min(100, Math.max(0, scoreGeral));
        String nivel = scoreGeral >= 70 ? "ALTO" : scoreGeral >= 40 ? "MEDIO" : "BAIXO";

        return new MatchingDetalhado(
                scoreGeral, scoreHardSkills, scoreQualificacoes,
                scoreExperiencia, scoreEducacao, scorePreferencias, scoreTfidf,
                nivel, hardSkillsFaltantes, termosContexto, todasFaltantes,
                expDetalhe, eduDetalhe
        );
    }

    // ── Experiência: compara anos exigidos × anos no CV ───────────────────

    private ExperienciaComparacao compararExperiencia(String vaga, String cv) {
        int anosExigidos   = extrairAnos(vaga);
        int anosEncontrados = extrairAnos(cv);
        String nivelVaga = extrairNivel(vaga);
        String nivelCv   = extrairNivel(cv);

        // Mapeia nível para anos mínimos esperados
        if (anosExigidos == 0) {
            anosExigidos = nivelParaAnos(nivelVaga);
        }

        boolean satisfeito = anosExigidos == 0 || anosEncontrados >= anosExigidos;
        return new ExperienciaComparacao(
                anosExigidos, anosEncontrados, nivelVaga, nivelCv, satisfeito);
    }

    private int calcularScoreExperiencia(ExperienciaComparacao exp) {
        if (exp.anosExigidos() == 0) return 70; // vaga não especifica → neutro

        if (exp.anosEncontrados() >= exp.anosExigidos()) return 100;

        // Penalidade proporcional à diferença
        double ratio = (double) exp.anosEncontrados() / exp.anosExigidos();
        return (int) Math.round(ratio * 80); // max 80 se não atinge o exigido
    }

    private int extrairAnos(String texto) {
        if (texto == null || texto.isBlank()) return 0;
        Matcher m = ANOS_PATTERN.matcher(texto);
        int maxAnos = 0;
        while (m.find()) {
            int anos = Integer.parseInt(m.group(1));
            if (anos > maxAnos) maxAnos = anos;
        }
        return maxAnos;
    }

    private String extrairNivel(String texto) {
        if (texto == null || texto.isBlank()) return "";
        Matcher m = NIVEL_PATTERN.matcher(texto.toLowerCase());
        return m.find() ? m.group(1).toLowerCase() : "";
    }

    private int nivelParaAnos(String nivel) {
        return switch (nivel.toLowerCase()) {
            case "junior", "jr", "entry-level", "entry" -> 1;
            case "mid-level", "pleno", "mid"             -> 3;
            case "senior", "sr"                          -> 5;
            case "lead", "principal", "staff"            -> 8;
            case "architect"                             -> 10;
            default                                      -> 0;
        };
    }

    // ── Educação: compara grau exigido × grau no CV ───────────────────────

    private EducacaoComparacao compararEducacao(String vaga, String cv) {
        String vagaLow = vaga.toLowerCase();
        String cvLow   = cv.toLowerCase();

        String grauExigido = extrairGrau(vagaLow);
        boolean grauEncontrado = grauExigido.isEmpty() || cvLow.contains(grauExigido)
                || temGrauEquivalente(cvLow, grauExigido);

        String areaExigida = extrairAreaEducacao(vagaLow);
        boolean areaEncontrada = areaExigida.isEmpty()
                || verificarAreaCompativel(cvLow, areaExigida);

        return new EducacaoComparacao(grauExigido, grauEncontrado, areaExigida, areaEncontrada);
    }

    private int calcularScoreEducacao(EducacaoComparacao edu) {
        if (edu.grauExigido().isEmpty()) return 75; // vaga não exige grau → neutro

        int score = 0;
        if (edu.grauEncontrado()) score += 70;
        if (edu.areaEncontrada()) score += 30;
        return score;
    }

    private String extrairGrau(String vaga) {
        if (vaga.contains("phd") || vaga.contains("doctorate") || vaga.contains("doutorado"))
            return "phd";
        if (vaga.contains("master") || vaga.contains("mestrado") || vaga.contains("mba"))
            return "master";
        if (vaga.contains("bachelor") || vaga.contains("bacharelado") || vaga.contains("graduacao")
                || vaga.contains("degree") || vaga.contains("graduation"))
            return "bachelor";
        return "";
    }

    private boolean temGrauEquivalente(String cv, String grauExigido) {
        return switch (grauExigido) {
            case "bachelor" -> cv.contains("bachelor") || cv.contains("bacharelado")
                    || cv.contains("graduacao") || cv.contains("graduation")
                    || cv.contains("software engineering") || cv.contains("computer science")
                    || cv.contains("engenharia de software") || cv.contains("faculdade")
                    || cv.contains("university") || cv.contains("universidade");
            case "master"   -> cv.contains("master") || cv.contains("mestrado") || cv.contains("mba");
            case "phd"      -> cv.contains("phd") || cv.contains("doctorate") || cv.contains("doutorado");
            default         -> false;
        };
    }

    private String extrairAreaEducacao(String vaga) {
        if (vaga.contains("computer science") || vaga.contains("ciencia da computacao"))
            return "computer science";
        if (vaga.contains("software engineering") || vaga.contains("engenharia de software"))
            return "software engineering";
        if (vaga.contains("information systems") || vaga.contains("sistemas de informacao"))
            return "information systems";
        if (vaga.contains("mathematics") || vaga.contains("statistics"))
            return "mathematics";
        return "";
    }

    private boolean verificarAreaCompativel(String cv, String areaExigida) {
        // Áreas compatíveis para desenvolvimento de software
        Set<String> areasCompativeis = new HashSet<>(Arrays.asList(
                "computer science", "ciencia da computacao",
                "software engineering", "engenharia de software",
                "information systems", "sistemas de informacao",
                "information technology", "tecnologia da informacao",
                "computer engineering", "engenharia da computacao",
                "mathematics", "matematica", "statistics"
        ));
        return areasCompativeis.stream().anyMatch(cv::contains);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private Set<String> filtrarCategoria(Set<String> termos, TermCategory categoria) {
        return termos.stream()
                .filter(t -> classifier.classify(t) == categoria)
                .collect(Collectors.toSet());
    }

    private Set<String> tokenizar(String texto) {
        if (texto == null || texto.isBlank()) return new HashSet<>();
        return Arrays.stream(
                texto.toLowerCase()
                     .replaceAll("[^a-zA-Z0-9\\s\\-+#]", " ")
                     .split("\\s+"))
                .filter(t -> t.length() >= 2)
                .collect(Collectors.toSet());
    }

    private int calcularQualificacoes(Set<String> termosVaga, Set<String> termosCv) {
        // Conta apenas termos técnicos — exclui condições, experiência, educação e idiomas
        long totalTecnicos = termosVaga.stream()
                .filter(t -> {
                    TermCategory cat = classifier.classify(t);
                    return cat == TermCategory.HARD_SKILL || cat == TermCategory.GENERAL;
                })
                .count();
        if (totalTecnicos == 0) return 75;
        long presentes = termosVaga.stream()
                .filter(t -> {
                    TermCategory cat = classifier.classify(t);
                    return cat == TermCategory.HARD_SKILL || cat == TermCategory.GENERAL;
                })
                .filter(termosCv::contains)
                .count();
        return (int) Math.round((double) presentes / totalTecnicos * 100);
    }

    private int calcularPreferencias(Set<String> termosVaga, Set<String> termosCv) {
        Set<String> preferencias = termosVaga.stream()
                .filter(t -> classifier.classify(t) == TermCategory.GENERAL && t.length() > 4)
                .collect(Collectors.toSet());
        if (preferencias.isEmpty()) return 70;
        long presentes = preferencias.stream().filter(termosCv::contains).count();
        return (int) Math.round((double) presentes / preferencias.size() * 100);
    }
}
