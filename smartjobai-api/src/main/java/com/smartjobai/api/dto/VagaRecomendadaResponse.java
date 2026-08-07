package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Vaga;

public record VagaRecomendadaResponse(
        Long id,
        String titulo,
        String empresa,
        String localizacao,
        String fonte,
        double score,
        int scorePercentual,
        String nivel
) {
    public static VagaRecomendadaResponse from(Vaga vaga, double score) {
        int pct = (int) Math.round(score * 100);
        String nivel = pct >= 70 ? "ALTO" : pct >= 40 ? "MEDIO" : "BAIXO";
        return new VagaRecomendadaResponse(
                vaga.getId(),
                vaga.getTitulo(),
                vaga.getEmpresa(),
                vaga.getLocalizacao(),
                vaga.getFonte(),
                score,
                pct,
                nivel
        );
    }
}
