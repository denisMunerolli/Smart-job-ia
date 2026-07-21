package com.smartjobai.api.dto;

import com.smartjobai.core.entity.Idioma;
import com.smartjobai.core.entity.NivelIdioma;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IdiomaRequest(
        @NotBlank String nome,
        @NotNull NivelIdioma nivel
) {
    public Idioma toEntity() {
        Idioma idioma = new Idioma();
        idioma.setNome(nome);
        idioma.setNivel(nivel);
        return idioma;
    }
}
