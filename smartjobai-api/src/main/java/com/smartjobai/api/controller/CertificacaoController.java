package com.smartjobai.api.controller;

import com.smartjobai.api.dto.CertificacaoRequest;
import com.smartjobai.api.dto.CertificacaoResponse;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.core.service.CertificacaoService;
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
@RequestMapping("/api/usuarios/me/certificacoes")
@RequiredArgsConstructor
public class CertificacaoController {

    private final CertificacaoService certificacaoService;

    @GetMapping
    public List<CertificacaoResponse> listar() {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return certificacaoService.listarPorUsuario(email).stream().map(CertificacaoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CertificacaoResponse buscar(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return CertificacaoResponse.from(certificacaoService.buscarPorId(email, id));
    }

    @PostMapping
    public ResponseEntity<CertificacaoResponse> criar(@Valid @RequestBody CertificacaoRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        var criada = certificacaoService.criar(email, request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(CertificacaoResponse.from(criada));
    }

    @PutMapping("/{id}")
    public CertificacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody CertificacaoRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return CertificacaoResponse.from(certificacaoService.atualizar(email, id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        certificacaoService.remover(email, id);
        return ResponseEntity.noContent().build();
    }
}
