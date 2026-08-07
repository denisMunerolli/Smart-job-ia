package com.smartjobai.api.service;

import com.smartjobai.ai.similarity.TFIDFMatcher;
import com.smartjobai.core.entity.Curriculo;
import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.CurriculoRepository;
import com.smartjobai.core.repository.VagaRepository;
import com.smartjobai.core.service.UsuarioService;
import com.smartjobai.core.service.VagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final TFIDFMatcher tfidfMatcher;
    private final CurriculoRepository curriculoRepository;
    private final VagaRepository vagaRepository;
    private final VagaService vagaService;
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

    /**
     * Calcula o score de uma vaga especifica contra o curriculo ativo do usuario.
     */
    @Transactional(readOnly = true)
    public double scoreVaga(Long vagaId, String emailUsuario) {
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga nao encontrada: " + vagaId));
        var usuario = usuarioService.buscarPorEmail(emailUsuario);
        Curriculo curriculo = curriculoRepository.findByUsuarioIdAndAtivoTrue(usuario.getId())
                .orElse(null);
        if (curriculo == null) return 0.0;

        String textoVaga = montarTextoVaga(vaga);
        String textoCurriculo = curriculo.getConteudoJson() != null
                ? curriculo.getConteudoJson()
                : curriculo.getTitulo() != null ? curriculo.getTitulo() : "";
        return tfidfMatcher.calcularSimilaridade(textoVaga, textoCurriculo);
    }

    /**
     * Retorna top N vagas com maior score para o curriculo ativo do usuario.
     */
    @Transactional(readOnly = true)
    public List<VagaScoreData> recomendarVagas(String emailUsuario, int limite) {
        var usuario = usuarioService.buscarPorEmail(emailUsuario);
        Curriculo curriculo = curriculoRepository.findByUsuarioIdAndAtivoTrue(usuario.getId())
                .orElse(null);

        List<Vaga> vagas = vagaService.listarParaRecomendacao(200);

        if (curriculo == null || vagas.isEmpty()) {
            return vagas.stream()
                    .limit(limite)
                    .map(v -> new VagaScoreData(v, 0.0))
                    .toList();
        }

        String textoCurriculo = curriculo.getConteudoJson() != null
                ? curriculo.getConteudoJson()
                : curriculo.getTitulo() != null ? curriculo.getTitulo() : "";

        return vagas.stream()
                .map(v -> new VagaScoreData(v, tfidfMatcher.calcularSimilaridade(montarTextoVaga(v), textoCurriculo)))
                .sorted(Comparator.comparingDouble(VagaScoreData::score).reversed())
                .limit(limite)
                .toList();
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
    public record VagaScoreData(Vaga vaga, double score) {}
}
