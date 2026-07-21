package com.smartjobai.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "formacoes", indexes = {
        @Index(name = "idx_formacao_usuario", columnList = "usuario_id")
})
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Formacao extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String instituicao;

    @Column(nullable = false)
    private String curso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NivelFormacao nivel;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private boolean emAndamento;

    @Column(length = 2000)
    private String descricao;
}
