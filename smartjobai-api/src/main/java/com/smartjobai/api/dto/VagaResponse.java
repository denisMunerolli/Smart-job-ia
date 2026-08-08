package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Vaga;

import java.time.LocalDateTime;

public record VagaResponse(
        Long id,
        String titulo,
        String empresa,
        String localizacao,
        String descricao,
        String fonte,
        LocalDateTime dataColeta,
        LocalDateTime dataCriacao,
        Double score,
        Integer scorePercentual,
        String nivel
) {
    public static VagaResponse from(Vaga vaga) {
        return new VagaResponse(
                vaga.getId(),
                vaga.getTitulo(),
                vaga.getEmpresa(),
                vaga.getLocalizacao(),
                vaga.getDescricao(),
                vaga.getFonte(),
                vaga.getDataColeta(),
                vaga.getDataCriacao(),
                null, null, null
        );
    }

    public static VagaResponse from(Vaga vaga, double score) {
        int pct = (int) Math.round(score * 100);
        String nivel = pct >= 70 ? "ALTO" : pct >= 40 ? "MEDIO" : "BAIXO";
        return new VagaResponse(
                vaga.getId(),
                vaga.getTitulo(),
                vaga.getEmpresa(),
                vaga.getLocalizacao(),
                vaga.getDescricao(),
                vaga.getFonte(),
                vaga.getDataColeta(),
                vaga.getDataCriacao(),
                score,
                pct,
                nivel
        );
    }
}
