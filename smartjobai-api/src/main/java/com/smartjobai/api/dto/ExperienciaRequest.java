package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Experiencia;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public record ExperienciaRequest(
        @NotBlank String empresa,
        @NotBlank String cargo,
        LocalDate dataInicio,
        LocalDate dataFim,
        boolean atual,
        String descricao,
        Set<String> tecnologias
) {
    public Experiencia toEntity() {
        Experiencia experiencia = new Experiencia();
        experiencia.setEmpresa(empresa);
        experiencia.setCargo(cargo);
        experiencia.setDataInicio(dataInicio);
        experiencia.setDataFim(dataFim);
        experiencia.setAtual(atual);
        experiencia.setDescricao(descricao);
        experiencia.setTecnologias(tecnologias != null ? new HashSet<>(tecnologias) : new HashSet<>());
        return experiencia;
    }
}
