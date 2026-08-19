package com.smartjobai.api.dto;

import com.smartjobai.ai.similarity.MultiDimensionalMatcher;
import java.util.List;

public record MatchingResult(
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

        // Habilidades técnicas faltantes
        List<String> hardSkillsFaltantes,
        List<String> termosContextoIgnorados,

        // Detalhes de experiência e educação
        ExperienciaDetalhe experienciaDetalhe,
        EducacaoDetalhe educacaoDetalhe,

        String descricao
) {
    public record ExperienciaDetalhe(
            int anosExigidos,
            int anosEncontrados,
            String nivelExigido,
            String nivelEncontrado,
            boolean satisfeito
    ) {}

    public record EducacaoDetalhe(
            String grauExigido,
            boolean grauEncontrado,
            String areaExigida,
            boolean areaEncontrada
    ) {}

    public static MatchingResult from(MultiDimensionalMatcher.MatchingDetalhado d) {
        ExperienciaDetalhe exp = new ExperienciaDetalhe(
                d.experienciaDetalhe().anosExigidos(),
                d.experienciaDetalhe().anosEncontrados(),
                d.experienciaDetalhe().nivelExigido(),
                d.experienciaDetalhe().nivelEncontrado(),
                d.experienciaDetalhe().satisfeito()
        );
        EducacaoDetalhe edu = new EducacaoDetalhe(
                d.educacaoDetalhe().grauExigido(),
                d.educacaoDetalhe().grauEncontrado(),
                d.educacaoDetalhe().areaExigida(),
                d.educacaoDetalhe().areaEncontrada()
        );
        return new MatchingResult(
                d.scoreGeral(), d.scoreGeral(), d.nivel(),
                d.hardSkills(), d.qualificacoesRequeridas(),
                d.experiencia(), d.educacao(),
                d.preferencias(), d.similaridadeTexto(),
                d.hardSkillsFaltantes(), d.skillsContexto(),
                exp, edu,
                gerarDescricao(d.scoreGeral(), d.experienciaDetalhe(), d.educacaoDetalhe())
        );
    }

    private static String gerarDescricao(
            int score,
            MultiDimensionalMatcher.ExperienciaComparacao exp,
            MultiDimensionalMatcher.EducacaoComparacao edu) {

        if (!exp.satisfeito() && exp.anosExigidos() > 0) {
            return String.format(
                "Compatibilidade %d%% — experiência insuficiente: vaga exige %d ano(s), CV indica %d.",
                score, exp.anosExigidos(), exp.anosEncontrados());
        }
        if (!edu.grauEncontrado() && !edu.grauExigido().isEmpty()) {
            return String.format(
                "Compatibilidade %d%% — grau acadêmico exigido (%s) não encontrado no currículo.",
                score, edu.grauExigido());
        }
        if (score >= 80) return "Excelente compatibilidade — perfil altamente alinhado com a vaga.";
        if (score >= 70) return "Boa compatibilidade — candidato forte para esta posição.";
        if (score >= 55) return "Compatibilidade moderada — a maioria das habilidades está presente.";
        if (score >= 40) return "Compatibilidade parcial — algumas habilidades técnicas precisam ser desenvolvidas.";
        return "Baixa compatibilidade — recomenda-se fortalecer as habilidades técnicas listadas.";
    }
}
