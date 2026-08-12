package com.smartjobai.api.dto;

import java.util.List;

public record MatchingResult(
        // Score geral ponderado
        int score,
        int scorePercentual,
        String nivel,

        // Breakdown por dimensão
        int hardSkills,
        int qualificacoesRequeridas,
        int experiencia,
        int educacao,
        int preferencias,
        int similaridadeTexto,

        // Habilidades técnicas faltantes (sem palavras de contexto)
        List<String> hardSkillsFaltantes,

        // Palavras de contexto identificadas (para transparência)
        List<String> termosContextoIgnorados,

        // Compatibilidade descritiva
        String descricao
) {
    public static MatchingResult from(
            com.smartjobai.ai.similarity.MultiDimensionalMatcher.MatchingDetalhado d) {

        String descricao = gerarDescricao(d.scoreGeral(), d.hardSkills(), d.hardSkillsFaltantes());

        return new MatchingResult(
                d.scoreGeral(),
                d.scoreGeral(),
                d.nivel(),
                d.hardSkills(),
                d.qualificacoesRequeridas(),
                d.experiencia(),
                d.educacao(),
                d.preferencias(),
                d.similaridadeTexto(),
                d.hardSkillsFaltantes(),
                d.skillsContexto(),
                descricao
        );
    }

    private static String gerarDescricao(int score, int hardSkills, List<String> faltantes) {
        if (score >= 80) return "Excelente compatibilidade — perfil altamente alinhado com a vaga.";
        if (score >= 70) return "Boa compatibilidade — candidato forte para esta posição.";
        if (score >= 55) return "Compatibilidade moderada — a maioria das habilidades está presente.";
        if (score >= 40) return "Compatibilidade parcial — algumas habilidades técnicas precisam ser desenvolvidas.";
        return "Baixa compatibilidade — recomenda-se fortalecer as habilidades técnicas listadas.";
    }
}
