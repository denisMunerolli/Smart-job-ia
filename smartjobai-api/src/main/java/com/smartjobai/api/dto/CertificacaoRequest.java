package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Certificacao;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CertificacaoRequest(
        @NotBlank String nome,
        @NotBlank String instituicaoEmissora,
        LocalDate dataEmissao,
        LocalDate dataExpiracao,
        String credencialUrl
) {
    public Certificacao toEntity() {
        Certificacao certificacao = new Certificacao();
        certificacao.setNome(nome);
        certificacao.setInstituicaoEmissora(instituicaoEmissora);
        certificacao.setDataEmissao(dataEmissao);
        certificacao.setDataExpiracao(dataExpiracao);
        certificacao.setCredencialUrl(credencialUrl);
        return certificacao;
    }
}
