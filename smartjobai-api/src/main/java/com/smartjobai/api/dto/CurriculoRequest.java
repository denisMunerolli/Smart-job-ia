package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Curriculo;
import jakarta.validation.constraints.NotBlank;

public record CurriculoRequest(
        @NotBlank String titulo,
        String conteudoJson
) {
    public Curriculo toEntity() {
        Curriculo curriculo = new Curriculo();
        curriculo.setTitulo(titulo);
        curriculo.setConteudoJson(conteudoJson);
        return curriculo;
    }
}
