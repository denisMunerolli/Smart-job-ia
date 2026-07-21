package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Formacao;
import com.smartjobai.core.entity.NivelFormacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FormacaoRequest(
        @NotBlank String instituicao,
        @NotBlank String curso,
        @NotNull NivelFormacao nivel,
        LocalDate dataInicio,
        LocalDate dataFim,
        boolean emAndamento,
        String descricao
) {
    public Formacao toEntity() {
        Formacao formacao = new Formacao();
        formacao.setInstituicao(instituicao);
        formacao.setCurso(curso);
        formacao.setNivel(nivel);
        formacao.setDataInicio(dataInicio);
        formacao.setDataFim(dataFim);
        formacao.setEmAndamento(emAndamento);
        formacao.setDescricao(descricao);
        return formacao;
    }
}
