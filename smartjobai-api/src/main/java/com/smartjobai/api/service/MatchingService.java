package com.smartjobai.api.service;

import com.smartjobai.ai.similarity.MultiDimensionalMatcher;
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
    private final MultiDimensionalMatcher multiMatcher;
    private final CurriculoRepository curriculoRepository;
    private final VagaRepository vagaRepository;
    private final VagaService vagaService;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public MultiDimensionalMatcher.MatchingDetalhado matchTextoLivre(
            String textoVaga, String textoCurriculo) {
        validar(textoVaga, "Texto da vaga");
        validar(textoCurriculo, "Texto do currículo");
        return multiMatcher.calcular(textoVaga, textoCurriculo);
    }

    @Transactional(readOnly = true)
    public MultiDimensionalMatcher.MatchingDetalhado matchPorIds(
            String emailUsuario, Long vagaId, Long curriculoId) {
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada: " + vagaId));
        var usuario = usuarioService.buscarPorEmail(emailUsuario);
        Curriculo curriculo = curriculoRepository.findByIdAndUsuarioId(curriculoId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Currículo não encontrado: " + curriculoId));

        return multiMatcher.calcular(montarTextoVaga(vaga), textoCV(curriculo));
    }

    @Transactional(readOnly = true)
    public double scoreVaga(Long vagaId, String emailUsuario) {
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada: " + vagaId));
        var usuario = usuarioService.buscarPorEmail(emailUsuario);
        Curriculo curriculo = curriculoRepository.findByUsuarioIdAndAtivoTrue(usuario.getId())
                .orElse(null);
        if (curriculo == null) return 0.0;
        var resultado = multiMatcher.calcular(montarTextoVaga(vaga), textoCV(curriculo));
        return resultado.scoreGeral() / 100.0;
    }

    @Transactional(readOnly = true)
    public List<VagaScoreData> recomendarVagas(String emailUsuario, int limite) {
        var usuario = usuarioService.buscarPorEmail(emailUsuario);
        Curriculo curriculo = curriculoRepository.findByUsuarioIdAndAtivoTrue(usuario.getId())
                .orElse(null);
        List<Vaga> vagas = vagaService.listarParaRecomendacao(200);

        if (curriculo == null || vagas.isEmpty()) {
            return vagas.stream().limit(limite).map(v -> new VagaScoreData(v, 0.0)).toList();
        }

        String cvTexto = textoCV(curriculo);
        return vagas.stream()
                .map(v -> new VagaScoreData(v,
                        multiMatcher.calcular(montarTextoVaga(v), cvTexto).scoreGeral() / 100.0))
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

    private String textoCV(Curriculo c) {
        if (c.getConteudoJson() != null) return c.getConteudoJson();
        return c.getTitulo() != null ? c.getTitulo() : "";
    }

    private void validar(String texto, String campo) {
        if (texto == null || texto.isBlank())
            throw new BusinessException(campo + " não pode estar vazio.");
    }

    public record VagaScoreData(Vaga vaga, double score) {}
}
