package com.smartjobai.api.controller;

import com.smartjobai.ai.similarity.MultiDimensionalMatcher;
import com.smartjobai.api.dto.MatchingResult;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.api.service.MatchingService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping
    public MatchingResult calcular(@RequestBody MatchingRequest request) {
        MultiDimensionalMatcher.MatchingDetalhado resultado;

        if (request.vagaId() != null && request.curriculoId() != null) {
            String email = SecurityUtils.getUsuarioAutenticadoEmail();
            resultado = matchingService.matchPorIds(email, request.vagaId(), request.curriculoId());
        } else {
            resultado = matchingService.matchTextoLivre(request.textoVaga(), request.textoCurriculo());
        }

        return MatchingResult.from(resultado);
    }

    public record MatchingRequest(
            Long vagaId,
            Long curriculoId,
            String textoVaga,
            String textoCurriculo
    ) {}
}
