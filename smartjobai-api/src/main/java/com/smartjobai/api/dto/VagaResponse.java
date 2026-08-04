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
        LocalDateTime dataCriacao
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
                vaga.getDataCriacao()
        );
    }
}
