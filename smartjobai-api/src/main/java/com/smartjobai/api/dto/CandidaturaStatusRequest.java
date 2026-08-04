package com.smartjobai.api.dto;

import com.smartjobai.core.entity.StatusCandidatura;
import jakarta.validation.constraints.NotNull;

public record CandidaturaStatusRequest(
        @NotNull StatusCandidatura status,
        String observacao
) {
}
