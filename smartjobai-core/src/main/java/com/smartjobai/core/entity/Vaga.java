package com.smartjobai.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * FIX em relação ao esqueleto original: @Builder sozinho, sem
 * @NoArgsConstructor, suprime o construtor padrão sem argumentos — e o
 * Hibernate precisa desse construtor para instanciar a entidade via
 * reflection. Sem isso, qualquer SELECT nesta tabela lançaria
 * InstantiationException em tempo de execução.
 */
@Entity
@Table(name = "vagas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_externo")
    private String idExterno; // ID na plataforma de origem

    private String fonte; // "linkedin", "indeed", etc.

    private String titulo;

    private String empresa;

    @Lob
    private String descricao;

    private String localizacao;

    @Column(name = "data_coleta")
    private LocalDateTime dataColeta;
}
