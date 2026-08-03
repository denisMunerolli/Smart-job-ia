package com.smartjobai.api.controller;

import com.smartjobai.api.dto.CurriculoRequest;
import com.smartjobai.api.dto.CurriculoResponse;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.core.service.CurriculoService;
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
@RequestMapping("/api/usuarios/me/curriculos")
@RequiredArgsConstructor
public class CurriculoController {

    private final CurriculoService curriculoService;

    @GetMapping
    public List<CurriculoResponse> listar() {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return curriculoService.listarPorUsuario(email).stream()
                .map(CurriculoResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CurriculoResponse buscar(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return CurriculoResponse.from(curriculoService.buscarPorId(email, id));
    }

    @PostMapping
    public ResponseEntity<CurriculoResponse> criar(@Valid @RequestBody CurriculoRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        var criado = curriculoService.criar(email, request.toEntity());
        return ResponseEntity.status(HttpStatus.CREATED).body(CurriculoResponse.from(criado));
    }

    @PutMapping("/{id}")
    public CurriculoResponse atualizar(@PathVariable Long id, @Valid @RequestBody CurriculoRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return CurriculoResponse.from(curriculoService.atualizar(email, id, request.toEntity()));
    }

    /**
     * Ativa um currículo específico e desativa automaticamente os demais.
     * Útil quando o usuário mantém múltiplas versões e quer trocar a ativa.
     */
    @PutMapping("/{id}/ativar")
    public CurriculoResponse ativar(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        return CurriculoResponse.from(curriculoService.ativar(email, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        curriculoService.remover(email, id);
        return ResponseEntity.noContent().build();
    }
}
