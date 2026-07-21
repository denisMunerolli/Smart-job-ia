package com.smartjobai.api.controller;

import com.smartjobai.api.dto.HabilidadeTecnicaRequest;
import com.smartjobai.api.dto.HabilidadeTecnicaResponse;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.core.service.HabilidadeTecnicaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/me/habilidades")
@RequiredArgsConstructor
public class HabilidadeTecnicaController {

    private final HabilidadeTecnicaService habilidadeTecnicaService;

    @GetMapping
    public List<HabilidadeTecnicaResponse> listar() {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return habilidadeTecnicaService.listarPorUsuario(email).stream().map(HabilidadeTecnicaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public HabilidadeTecnicaResponse buscar(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return HabilidadeTecnicaResponse.from(habilidadeTecnicaService.buscarPorId(email, id));
    }

    @PostMapping
    public ResponseEntity<HabilidadeTecnicaResponse> criar(@Valid @RequestBody HabilidadeTecnicaRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        var criada = habilidadeTecnicaService.criar(email, request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(HabilidadeTecnicaResponse.from(criada));
    }

    @PutMapping("/{id}")
    public HabilidadeTecnicaResponse atualizar(@PathVariable Long id, @Valid @RequestBody HabilidadeTecnicaRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return HabilidadeTecnicaResponse.from(habilidadeTecnicaService.atualizar(email, id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        habilidadeTecnicaService.remover(email, id);
        return ResponseEntity.noContent().build();
    }
}
