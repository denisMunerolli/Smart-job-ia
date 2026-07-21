package com.smartjobai.api.controller;

import com.smartjobai.api.dto.ExperienciaRequest;
import com.smartjobai.api.dto.ExperienciaResponse;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.core.service.ExperienciaService;
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
@RequestMapping("/api/usuarios/me/experiencias")
@RequiredArgsConstructor
public class ExperienciaController {

    private final ExperienciaService experienciaService;

    @GetMapping
    public List<ExperienciaResponse> listar() {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return experienciaService.listarPorUsuario(email).stream().map(ExperienciaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ExperienciaResponse buscar(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return ExperienciaResponse.from(experienciaService.buscarPorId(email, id));
    }

    @PostMapping
    public ResponseEntity<ExperienciaResponse> criar(@Valid @RequestBody ExperienciaRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        var criada = experienciaService.criar(email, request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(ExperienciaResponse.from(criada));
    }

    @PutMapping("/{id}")
    public ExperienciaResponse atualizar(@PathVariable Long id, @Valid @RequestBody ExperienciaRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return ExperienciaResponse.from(experienciaService.atualizar(email, id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        experienciaService.remover(email, id);
        return ResponseEntity.noContent().build();
    }
}
