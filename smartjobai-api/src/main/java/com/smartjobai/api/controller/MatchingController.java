package com.smartjobai.api.controller;

import com.smartjobai.api.dto.MatchingRequest;
import com.smartjobai.api.dto.MatchingResult;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.service.MatchingService;
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

    /**
     * Compara currículo x vaga e retorna score TF-IDF + habilidades faltantes.
     *
     * Modo 1 — texto livre:
     *   { "textoVaga": "...", "textoCurriculo": "..." }
     *
     * Modo 2 — por IDs (vaga e currículo devem existir no banco):
     *   { "vagaId": 1, "curriculoId": 2 }
     */
    @PostMapping
    public MatchingResult comparar(@RequestBody MatchingRequest request) {

        MatchingService.MatchingResultData data;

        boolean textoLivre = request.textoVaga() != null && !request.textoVaga().isBlank()
                && request.textoCurriculo() != null && !request.textoCurriculo().isBlank();

        boolean porIds = request.vagaId() != null && request.curriculoId() != null;

        if (textoLivre) {
            data = matchingService.matchTextoLivre(request.textoVaga(), request.textoCurriculo());
        } else if (porIds) {
            String email = SecurityUtils.getUsuarioAutenticadoEmail();
            data = matchingService.matchPorIds(email, request.vagaId(), request.curriculoId());
        } else {
            throw new BusinessException(
                    "Informe (textoVaga + textoCurriculo) ou (vagaId + curriculoId).");
        }

        return MatchingResult.of(data.score(), data.habilidadesFaltantes());
    }
}
