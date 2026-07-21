package com.smartjobai.api.dto;

import com.smartjobai.core.entity.HabilidadeTecnica;
import com.smartjobai.core.entity.NivelProficiencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HabilidadeTecnicaRequest(
        @NotBlank String nome,
        @NotNull NivelProficiencia nivelProficiencia,
        Integer anosExperiencia
) {
    public HabilidadeTecnica toEntity() {
        HabilidadeTecnica habilidade = new HabilidadeTecnica();
        habilidade.setNome(nome);
        habilidade.setNivelProficiencia(nivelProficiencia);
        habilidade.setAnosExperiencia(anosExperiencia);
        return habilidade;
    }
}
