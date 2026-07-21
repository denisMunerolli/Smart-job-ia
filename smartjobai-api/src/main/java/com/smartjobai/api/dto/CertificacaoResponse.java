package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Certificacao;

import java.time.LocalDate;

public record CertificacaoResponse(
        Long id,
        String nome,
        String instituicaoEmissora,
        LocalDate dataEmissao,
        LocalDate dataExpiracao,
        String credencialUrl
) {
    public static CertificacaoResponse from(Certificacao certificacao) {
        return new CertificacaoResponse(
                certificacao.getId(),
                certificacao.getNome(),
                certificacao.getInstituicaoEmissora(),
                certificacao.getDataEmissao(),
                certificacao.getDataExpiracao(),
                certificacao.getCredencialUrl()
        );
    }
}
