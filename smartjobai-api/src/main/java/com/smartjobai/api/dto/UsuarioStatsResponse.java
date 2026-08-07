package com.smartjobai.api.dto;

import com.smartjobai.core.entity.StatusCandidatura;
import java.util.Map;

public record UsuarioStatsResponse(
        long totalVagas,
        long totalCandidaturas,
        long totalCurriculos,
        double scoremedioMatching,
        Map<StatusCandidatura, Long> candidaturasPorStatus
) {}
