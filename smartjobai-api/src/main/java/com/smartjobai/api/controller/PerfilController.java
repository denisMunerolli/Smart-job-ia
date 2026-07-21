package com.smartjobai.api.controller;

import com.smartjobai.api.dto.UsuarioResponse;
import com.smartjobai.api.security.SecurityUtils;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios/me")
@RequiredArgsConstructor
public class PerfilController {

    private final UsuarioService usuarioService;

    @GetMapping
    public UsuarioResponse perfilCompleto() {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        Usuario usuario = usuarioService.buscarPerfilCompleto(email);
        return UsuarioResponse.perfilCompleto(usuario);
    }

    @PutMapping
    public UsuarioResponse atualizarPerfil(@Valid @RequestBody AtualizarPerfilRequest request) {
        String email = SecurityUtils.getUsuarioAutenticadoEmail();
        Usuario usuario = usuarioService.atualizarPerfil(
                email, request.nome(), request.linkedinUrl(), request.githubUrl(), request.portfolioUrl());
        return UsuarioResponse.resumo(usuario);
    }

    public record AtualizarPerfilRequest(
            @NotBlank String nome,
            String linkedinUrl,
            String githubUrl,
            String portfolioUrl
    ) {
    }
}
