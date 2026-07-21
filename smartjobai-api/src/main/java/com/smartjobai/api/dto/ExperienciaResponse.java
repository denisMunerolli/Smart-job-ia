package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Experiencia;

import java.time.LocalDate;
import java.util.Set;

public record ExperienciaResponse(
        Long id,
        String empresa,
        String cargo,
        LocalDate dataInicio,
        LocalDate dataFim,
        boolean atual,
        String descricao,
        Set<String> tecnologias
) {
    public static ExperienciaResponse from(Experiencia experiencia) {
        return new ExperienciaResponse(
                experiencia.getId(),
                experiencia.getEmpresa(),
                experiencia.getCargo(),
                experiencia.getDataInicio(),
                experiencia.getDataFim(),
                experiencia.isAtual(),
                experiencia.getDescricao(),
                experiencia.getTecnologias()
        );
    }
}
