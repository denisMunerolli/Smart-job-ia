package com.smartjobai.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "curriculos")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Curriculo extends EntidadeBase {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String titulo;

    private Integer versao = 1;

    private boolean ativo = true;

    @Column(name = "conteudo_json", columnDefinition = "TEXT")
    private String conteudoJson;
}
