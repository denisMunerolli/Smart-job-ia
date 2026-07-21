package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record UsuarioResponse(
        Long id,
        String email,
        String nome,
        String linkedinUrl,
        String githubUrl,
        String portfolioUrl,
        LocalDateTime dataCriacao,
        List<FormacaoResponse> formacoes,
        List<ExperienciaResponse> experiencias,
        List<IdiomaResponse> idiomas,
        List<CertificacaoResponse> certificacoes,
        List<HabilidadeTecnicaResponse> habilidadesTecnicas
) {
    public static UsuarioResponse resumo(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getLinkedinUrl(),
                usuario.getGithubUrl(),
                usuario.getPortfolioUrl(),
                usuario.getDataCriacao(),
                null, null, null, null, null
        );
    }

    public static UsuarioResponse perfilCompleto(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getLinkedinUrl(),
                usuario.getGithubUrl(),
                usuario.getPortfolioUrl(),
                usuario.getDataCriacao(),
                usuario.getFormacoes().stream().map(FormacaoResponse::from).collect(Collectors.toList()),
                usuario.getExperiencias().stream().map(ExperienciaResponse::from).collect(Collectors.toList()),
                usuario.getIdiomas().stream().map(IdiomaResponse::from).collect(Collectors.toList()),
                usuario.getCertificacoes().stream().map(CertificacaoResponse::from).collect(Collectors.toList()),
                usuario.getHabilidadesTecnicas().stream().map(HabilidadeTecnicaResponse::from).collect(Collectors.toList())
        );
    }
}
