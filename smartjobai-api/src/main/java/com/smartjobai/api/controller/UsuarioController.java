package com.smartjobai.api.controller;

import com.smartjobai.api.dto.UsuarioStatsResponse;
import com.smartjobai.api.dto.VagaRecomendadaResponse;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.api.service.MatchingService;
import com.smartjobai.core.entity.StatusCandidatura;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.repository.CandidaturaRepository;
import com.smartjobai.core.repository.CurriculoRepository;
import com.smartjobai.core.service.UsuarioService;
import com.smartjobai.core.service.VagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios/me")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final CandidaturaRepository candidaturaRepository;
    private final CurriculoRepository curriculoRepository;
    private final VagaService vagaService;
    private final MatchingService matchingService;

    @GetMapping
    public Usuario buscar() {
        return usuarioService.buscarPerfilCompleto(SecurityUtils.getUsuarioAutenticadoEmail());
    }

    @PutMapping
    public Usuario atualizar(@RequestBody Usuario dados) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return usuarioService.atualizarPerfil(
                email,
                dados.getNome(),
                dados.getLinkedinUrl(),
                dados.getGithubUrl(),
                dados.getPortfolioUrl()
        );
    }

    /**
     * GET /api/usuarios/me/stats
     */
    @GetMapping("/stats")
    public UsuarioStatsResponse stats() {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        Usuario usuario = usuarioService.buscarPorEmail(email);
        Long uid = usuario.getId();

        long totalVagas        = vagaService.contarTotal();
        long totalCandidaturas = candidaturaRepository.countByUsuarioId(uid);
        long totalCurriculos   = curriculoRepository.countByUsuarioId(uid);

        Map<StatusCandidatura, Long> porStatus = new EnumMap<>(StatusCandidatura.class);
        candidaturaRepository.countGroupByStatus(uid)
                .forEach(row -> porStatus.put((StatusCandidatura) row[0], (Long) row[1]));

        return new UsuarioStatsResponse(
                totalVagas,
                totalCandidaturas,
                totalCurriculos,
                0.0,
                porStatus
        );
    }

    /**
     * GET /api/usuarios/me/recomendacoes?limite=10
     * Rota dedicada para evitar conflito com VagaController
     */
    @GetMapping("/recomendacoes")
    public List<VagaRecomendadaResponse> recomendacoes(
            @RequestParam(defaultValue = "10") int limite) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return matchingService.recomendarVagas(email, Math.min(limite, 50))
                .stream()
                .map(d -> VagaRecomendadaResponse.from(d.vaga(), d.score()))
                .toList();
    }
}
