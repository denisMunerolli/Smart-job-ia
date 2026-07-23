package com.smartjobai.api.controller;

import com.smartjobai.api.dto.IdiomaRequest;
import com.smartjobai.api.dto.IdiomaResponse;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.core.service.IdiomaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/me/idiomas")
@RequiredArgsConstructor
public class IdiomaController {

    private final IdiomaService idiomaService;

    @GetMapping
    public List<IdiomaResponse> listar() {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return idiomaService.listarPorUsuario(email).stream().map(IdiomaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public IdiomaResponse buscar(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return IdiomaResponse.from(idiomaService.buscarPorId(email, id));
    }

    @PostMapping
    public ResponseEntity<IdiomaResponse> criar(@Valid @RequestBody IdiomaRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        var criado = idiomaService.criar(email, request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(IdiomaResponse.from(criado));
    }

    @PutMapping("/{id}")
    public IdiomaResponse atualizar(@PathVariable Long id, @Valid @RequestBody IdiomaRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return IdiomaResponse.from(idiomaService.atualizar(email, id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        idiomaService.remover(email, id);
        return ResponseEntity.noContent().build();
    }
}
