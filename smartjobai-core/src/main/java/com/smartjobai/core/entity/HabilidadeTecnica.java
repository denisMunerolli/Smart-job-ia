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

@Entity
@Table(name = "habilidades_tecnicas", indexes = {
        @Index(name = "idx_habilidade_usuario", columnList = "usuario_id"),
        @Index(name = "idx_habilidade_nome", columnList = "nome")
})
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class HabilidadeTecnica extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NivelProficiencia nivelProficiencia;

    private Integer anosExperiencia;
}
