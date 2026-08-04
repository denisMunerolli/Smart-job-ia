package com.smartjobai.api.controller;

import com.smartjobai.api.dto.CandidaturaRequest;
import com.smartjobai.api.dto.CandidaturaResponse;
import com.smartjobai.api.dto.CandidaturaStatusRequest;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.core.entity.StatusCandidatura;
import com.smartjobai.core.service.CandidaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/me/candidaturas")
@RequiredArgsConstructor
public class CandidaturaController {

    private final CandidaturaService candidaturaService;

    @GetMapping
    public Page<CandidaturaResponse> listar(
            @PageableDefault(size = 20, sort = "dataCriacao") Pageable pageable
    ) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return candidaturaService.listar(email, pageable).map(CandidaturaResponse::from);
    }

    @GetMapping("/status/{status}")
    public List<CandidaturaResponse> listarPorStatus(@PathVariable StatusCandidatura status) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return candidaturaService.listarPorStatus(email, status)
                .stream().map(CandidaturaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CandidaturaResponse buscar(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return CandidaturaResponse.from(candidaturaService.buscarPorId(email, id));
    }

    @PostMapping
    public ResponseEntity<CandidaturaResponse> candidatar(@Valid @RequestBody CandidaturaRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        var candidatura = candidaturaService.candidatar(
                email, request.vagaId(), request.curriculoId(), request.observacao());
        return ResponseEntity.status(HttpStatus.CREATED).body(CandidaturaResponse.from(candidatura));
    }

    /**
     * Atualiza o status e/ou observacao de uma candidatura.
     * Util para registrar progressao: PENDENTE -> ENVIADA -> EM_ANALISE -> ENTREVISTA -> ...
     */
    @PutMapping("/{id}/status")
    public CandidaturaResponse atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody CandidaturaStatusRequest request
    ) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return CandidaturaResponse.from(
                candidaturaService.atualizarStatus(email, id, request.status(), request.observacao()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        candidaturaService.remover(email, id);
        return ResponseEntity.noContent().build();
    }
}
