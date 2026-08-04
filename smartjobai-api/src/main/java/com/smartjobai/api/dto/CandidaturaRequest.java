package com.smartjobai.api.dto;

import jakarta.validation.constraints.NotNull;

public record CandidaturaRequest(
        @NotNull Long vagaId,
        Long curriculoId,
        String observacao
) {
}
