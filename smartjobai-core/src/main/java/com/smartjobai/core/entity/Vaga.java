package com.smartjobai.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "vagas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Vaga extends EntidadeBase {

    @Column(name = "id_externo")
    private String idExterno;

    private String fonte;

    private String titulo;

    private String empresa;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String localizacao;

    @Column(name = "data_coleta")
    private LocalDateTime dataColeta;
}
