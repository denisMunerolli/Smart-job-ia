package com.smartjobai.api.dto;

import com.smartjobai.core.entity.HabilidadeTecnica;
import com.smartjobai.core.entity.NivelProficiencia;

public record HabilidadeTecnicaResponse(
        Long id,
        String nome,
        NivelProficiencia nivelProficiencia,
        Integer anosExperiencia
) {
    public static HabilidadeTecnicaResponse from(HabilidadeTecnica habilidade) {
        return new HabilidadeTecnicaResponse(
                habilidade.getId(),
                habilidade.getNome(),
                habilidade.getNivelProficiencia(),
                habilidade.getAnosExperiencia()
        );
    }
}
