package com.smartjobai.api.service;

import com.smartjobai.ai.similarity.TFIDFMatcher;
import com.smartjobai.core.entity.Curriculo;
import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.CurriculoRepository;
import com.smartjobai.core.repository.VagaRepository;
import com.smartjobai.core.service.UsuarioService;
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

    @Transactional(readOnly = true)
    public MatchingResultData matchTextoLivre(String textoVaga, String textoCurriculo) {
        validarTexto(textoVaga, "Texto da vaga");
        validarTexto(textoCurriculo, "Texto do curriculo");

        double score = tfidfMatcher.calcularSimilaridade(textoVaga, textoCurriculo);
        List<String> faltantes = tfidfMatcher.habilidadesFaltantes(textoVaga, textoCurriculo);
        return new MatchingResultData(score, faltantes);
    }

    @Transactional(readOnly = true)
    public MatchingResultData matchPorIds(String emailUsuario, Long vagaId, Long curriculoId) {
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga nao encontrada: " + vagaId));

        var usuario = usuarioService.buscarPorEmail(emailUsuario);
        Curriculo curriculo = curriculoRepository.findByIdAndUsuarioId(curriculoId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Curriculo nao encontrado: " + curriculoId));

        String textoVaga = montarTextoVaga(vaga);
        String textoCurriculo = curriculo.getConteudoJson() != null
                ? curriculo.getConteudoJson()
                : curriculo.getTitulo() != null ? curriculo.getTitulo() : "";

        double score = tfidfMatcher.calcularSimilaridade(textoVaga, textoCurriculo);
        List<String> faltantes = tfidfMatcher.habilidadesFaltantes(textoVaga, textoCurriculo);
        return new MatchingResultData(score, faltantes);
    }

    private String montarTextoVaga(Vaga vaga) {
        StringBuilder sb = new StringBuilder();
        if (vaga.getTitulo() != null)    sb.append(vaga.getTitulo()).append(" ");
        if (vaga.getDescricao() != null) sb.append(vaga.getDescricao()).append(" ");
        if (vaga.getEmpresa() != null)   sb.append(vaga.getEmpresa());
        return sb.toString().trim();
    }

    private void validarTexto(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new BusinessException(campo + " nao pode estar vazio.");
        }
    }

    public record MatchingResultData(double score, List<String> habilidadesFaltantes) {}
}
