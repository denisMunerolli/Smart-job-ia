package com.smartjobai.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "candidaturas")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Candidatura extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaga_id", nullable = false)
    private Vaga vaga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculo_id")
    private Curriculo curriculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusCandidatura status = StatusCandidatura.PENDENTE;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
