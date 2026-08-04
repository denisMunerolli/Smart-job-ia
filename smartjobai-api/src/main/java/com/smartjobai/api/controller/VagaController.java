package com.smartjobai.api.controller;

import com.smartjobai.api.dto.VagaResponse;
import com.smartjobai.core.service.VagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vagas")
@RequiredArgsConstructor
public class VagaController {

    private final VagaService vagaService;

    /**
     * Lista vagas com filtros opcionais e paginacao.
     * GET /api/vagas?titulo=java&empresa=tech&localizacao=remoto&page=0&size=20
     */
    @GetMapping
    public Page<VagaResponse> listar(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String empresa,
            @RequestParam(required = false) String localizacao,
            @PageableDefault(size = 20, sort = "dataCriacao") Pageable pageable
    ) {
        return vagaService.buscar(titulo, empresa, localizacao, pageable)
                .map(VagaResponse::from);
    }

    @GetMapping("/{id}")
    public VagaResponse buscar(@PathVariable Long id) {
        return VagaResponse.from(vagaService.buscarPorId(id));
    }
}
