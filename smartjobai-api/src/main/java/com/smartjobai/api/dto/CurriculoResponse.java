package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Curriculo;

import java.time.LocalDateTime;

public record CurriculoResponse(
        Long id,
        String titulo,
        Integer versao,
        boolean ativo,
        String conteudoJson,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
    public static CurriculoResponse from(Curriculo curriculo) {
        return new CurriculoResponse(
                curriculo.getId(),
                curriculo.getTitulo(),
                curriculo.getVersao(),
                curriculo.isAtivo(),
                curriculo.getConteudoJson(),
                curriculo.getDataCriacao(),
                curriculo.getDataAtualizacao()
        );
    }
}
