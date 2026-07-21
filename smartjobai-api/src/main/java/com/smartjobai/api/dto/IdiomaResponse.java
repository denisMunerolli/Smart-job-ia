package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Idioma;
import com.smartjobai.core.entity.NivelIdioma;

public record IdiomaResponse(
        Long id,
        String nome,
        NivelIdioma nivel
) {
    public static IdiomaResponse from(Idioma idioma) {
        return new IdiomaResponse(idioma.getId(), idioma.getNome(), idioma.getNivel());
    }
}
