package com.smartjobai.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "certificacoes", indexes = {
        @Index(name = "idx_certificacao_usuario", columnList = "usuario_id")
})
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Certificacao extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String instituicaoEmissora;

    private LocalDate dataEmissao;

    private LocalDate dataExpiracao;

    private String credencialUrl;
}
