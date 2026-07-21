package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Formacao;
import com.smartjobai.core.entity.NivelFormacao;

import java.time.LocalDate;

public record FormacaoResponse(
        Long id,
        String instituicao,
        String curso,
        NivelFormacao nivel,
        LocalDate dataInicio,
        LocalDate dataFim,
        boolean emAndamento,
        String descricao
) {
    public static FormacaoResponse from(Formacao formacao) {
        return new FormacaoResponse(
                formacao.getId(),
                formacao.getInstituicao(),
                formacao.getCurso(),
                formacao.getNivel(),
                formacao.getDataInicio(),
                formacao.getDataFim(),
                formacao.isEmAndamento(),
                formacao.getDescricao()
        );
    }
}
