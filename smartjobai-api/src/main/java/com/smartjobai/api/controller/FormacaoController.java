package com.smartjobai.api.controller;

import com.smartjobai.api.dto.FormacaoRequest;
import com.smartjobai.api.dto.FormacaoResponse;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.core.service.FormacaoService;
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
@RequestMapping("/api/usuarios/me/formacoes")
@RequiredArgsConstructor
public class FormacaoController {

    private final FormacaoService formacaoService;

    @GetMapping
    public List<FormacaoResponse> listar() {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return formacaoService.listarPorUsuario(email).stream().map(FormacaoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public FormacaoResponse buscar(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return FormacaoResponse.from(formacaoService.buscarPorId(email, id));
    }

    @PostMapping
    public ResponseEntity<FormacaoResponse> criar(@Valid @RequestBody FormacaoRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        var criada = formacaoService.criar(email, request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(FormacaoResponse.from(criada));
    }

    @PutMapping("/{id}")
    public FormacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody FormacaoRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return FormacaoResponse.from(formacaoService.atualizar(email, id, request.toEntity()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        formacaoService.remover(email, id);
        return ResponseEntity.noContent().build();
    }
}
