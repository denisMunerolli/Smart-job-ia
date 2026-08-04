package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Candidatura;
import com.smartjobai.core.entity.StatusCandidatura;

import java.time.LocalDateTime;

public record CandidaturaResponse(
        Long id,
        Long vagaId,
        String vagaTitulo,
        String vagaEmpresa,
        Long curriculoId,
        String curriculoTitulo,
        StatusCandidatura status,
        String observacao,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
    public static CandidaturaResponse from(Candidatura c) {
        return new CandidaturaResponse(
                c.getId(),
                c.getVaga().getId(),
                c.getVaga().getTitulo(),
                c.getVaga().getEmpresa(),
                c.getCurriculo() != null ? c.getCurriculo().getId() : null,
                c.getCurriculo() != null ? c.getCurriculo().getTitulo() : null,
                c.getStatus(),
                c.getObservacao(),
                c.getDataCriacao(),
                c.getDataAtualizacao()
        );
    }
}
