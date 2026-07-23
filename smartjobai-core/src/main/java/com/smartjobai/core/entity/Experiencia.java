package com.smartjobai.core.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "experiencias", indexes = {
        @Index(name = "idx_experiencia_usuario", columnList = "usuario_id"),
        @Index(name = "idx_experiencia_cargo", columnList = "cargo")
})
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Experiencia extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String empresa;

    @Column(nullable = false)
    private String cargo;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private boolean atual;

    @Column(length = 3000)
    private String descricao;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "experiencia_tecnologias", joinColumns = @JoinColumn(name = "experiencia_id"),
            indexes = @Index(name = "idx_exp_tecnologia", columnList = "tecnologia"))
    @Column(name = "tecnologia")
    private Set<String> tecnologias = new HashSet<>();
}
