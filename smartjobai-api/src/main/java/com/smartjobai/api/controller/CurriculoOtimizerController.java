package com.smartjobai.api.controller;

import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.api.service.CurriculoOtimizerService;
import com.smartjobai.api.service.CurriculoOtimizerService.OtimizacaoResult;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/curriculos/otimizar")
@RequiredArgsConstructor
public class CurriculoOtimizerController {

    private final CurriculoOtimizerService otimizerService;

    /**
     * POST /api/curriculos/otimizar/ids
     * Otimiza o currículo do usuário para uma vaga específica usando IDs.
     */
    @PostMapping("/ids")
    public OtimizacaoResult otimizarPorIds(@RequestBody OtimizarPorIdsRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return otimizerService.otimizarParaVaga(email, request.vagaId(), request.curriculoId());
    }

    /**
     * POST /api/curriculos/otimizar/texto
     * Otimiza por texto livre — útil para vagas externas coladas pelo usuário.
     */
    @PostMapping("/texto")
    public OtimizacaoResult otimizarPorTexto(@RequestBody OtimizarPorTextoRequest request) {
        return otimizerService.otimizarTextoLivre(request.textoVaga(), request.textoCurriculo());
    }

    public record OtimizarPorIdsRequest(Long vagaId, Long curriculoId) {}

    public record OtimizarPorTextoRequest(
            @NotBlank String textoVaga,
            @NotBlank String textoCurriculo
    ) {}
}
