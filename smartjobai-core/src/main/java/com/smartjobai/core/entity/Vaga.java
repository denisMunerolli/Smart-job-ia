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

    /** Fonte da vaga: adzuna, remoteok, mock, rss */
    private String fonte;

    private String titulo;
    private String empresa;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String localizacao;

    @Column(name = "data_coleta")
    private LocalDateTime dataColeta;

    /** URL original da vaga no site de origem */
    @Column(name = "url_origem")
    private String urlOrigem;

    /** Nome legível da fonte para exibição */
    public String getFonteLabel() {
        if (fonte == null) return "Desconhecida";
        return switch (fonte.toLowerCase()) {
            case "adzuna"   -> "Adzuna";
            case "remoteok" -> "RemoteOK";
            case "rss"      -> "RSS Feed";
            case "mock"     -> "Demonstração";
            default         -> fonte;
        };
    }
}
