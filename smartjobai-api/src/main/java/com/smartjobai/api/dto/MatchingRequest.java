package com.smartjobai.api.dto;

/**
 * Duas formas de usar:
 *
 * 1. Texto livre: preencha textoVaga e textoCurriculo diretamente.
 * 2. Por IDs: preencha vagaId e curriculoId — o serviço busca os textos no banco.
 *
 * Se todos os campos forem preenchidos, os textos livres têm precedência.
 */
public record MatchingRequest(
        String textoVaga,
        String textoCurriculo,
        Long vagaId,
        Long curriculoId
) {
}
