package com.smartjobai.api.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErroResponse(
        int status,
        String mensagem,
        LocalDateTime timestamp,
        Map<String, String> erros
) {
}
