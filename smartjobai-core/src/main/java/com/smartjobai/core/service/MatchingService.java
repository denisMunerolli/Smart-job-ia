package com.smartjobai.core.service;

import com.smartjobai.ai.similarity.TFIDFMatcher;
import com.smartjobai.core.entity.Curriculo;
import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.CurriculoRepository;
import com.smartjobai.core.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final TFIDFMatcher tfidfMatcher;
    private final CurriculoRepository curriculoRepository;
    private final VagaRepository vagaRepository;
    private final UsuarioService usuarioService;

    /**
     * Matching por texto livre — útil para colar a descrição de uma vaga
     * e o texto do currículo diretamente, sem salvar nada no banco.
     */
    @Transactional(readOnly = true)
    public MatchingResultData matchTextoLivre(String textoVaga, String textoCurriculo) {
        validarTexto(textoVaga, "Texto da vaga");
        validarTexto(textoCurriculo, "Texto do currículo");

        double score = tfidfMatcher.calcularSimilaridade(textoVaga, textoCurriculo);
        List<String> faltantes = tfidfMatcher.habilidadesFaltantes(textoVaga, textoCurriculo);
        return new MatchingResultData(score, faltantes);
    }

    /**
     * Matching via IDs — busca a vaga e o currículo no banco,
     * valida que o currículo pertence ao usuário logado e calcula o score.
     */
    @Transactional(readOnly = true)
    public MatchingResultData matchPorIds(String emailUsuario, Long vagaId, Long curriculoId) {
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada: " + vagaId));

        var usuario = usuarioService.buscarPorEmail(emailUsuario);
        Curriculo curriculo = curriculoRepository.findByIdAndUsuarioId(curriculoId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Currículo não encontrado: " + curriculoId));

        String textoVaga = montarTextoVaga(vaga);
        String textoCurriculo = curriculo.getConteudoJson() != null
                ? curriculo.getConteudoJson()
                : curriculo.getTitulo();

        double score = tfidfMatcher.calcularSimilaridade(textoVaga, textoCurriculo);
        List<String> faltantes = tfidfMatcher.habilidadesFaltantes(textoVaga, textoCurriculo);
        return new MatchingResultData(score, faltantes);
    }

    // -----------------------------------------------------------------------

    private String montarTextoVaga(Vaga vaga) {
        StringBuilder sb = new StringBuilder();
        if (vaga.getTitulo() != null) sb.append(vaga.getTitulo()).append(" ");
        if (vaga.getDescricao() != null) sb.append(vaga.getDescricao()).append(" ");
        if (vaga.getEmpresa() != null) sb.append(vaga.getEmpresa());
        return sb.toString().trim();
    }

    private void validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new BusinessException(campo + " não pode estar vazio.");
        }
    }

    /**
     * Estrutura interna para transportar score + faltantes sem
     * acoplar o core ao DTO da camada api.
     */
    public record MatchingResultData(double score, List<String> habilidadesFaltantes) {
    }
}
