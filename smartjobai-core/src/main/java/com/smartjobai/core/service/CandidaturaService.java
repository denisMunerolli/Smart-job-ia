package com.smartjobai.core.service;

import com.smartjobai.core.entity.Candidatura;
import com.smartjobai.core.entity.Curriculo;
import com.smartjobai.core.entity.StatusCandidatura;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.CandidaturaRepository;
import com.smartjobai.core.repository.CurriculoRepository;
import com.smartjobai.core.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final VagaRepository vagaRepository;
    private final CurriculoRepository curriculoRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public Page<Candidatura> listar(String email, Pageable pageable) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return candidaturaRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuario.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public List<Candidatura> listarPorStatus(String email, StatusCandidatura status) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return candidaturaRepository.findByUsuarioIdAndStatusOrderByDataCriacaoDesc(usuario.getId(), status);
    }

    @Transactional(readOnly = true)
    public Candidatura buscarPorId(String email, Long id) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return candidaturaRepository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Candidatura nao encontrada: " + id));
    }

    @Transactional
    public Candidatura candidatar(String email, Long vagaId, Long curriculoId, String observacao) {
        Usuario usuario = usuarioService.buscarPorEmail(email);

        if (candidaturaRepository.existsByUsuarioIdAndVagaId(usuario.getId(), vagaId)) {
            throw new BusinessException("Voce ja se candidatou a esta vaga.");
        }

        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga nao encontrada: " + vagaId));

        Candidatura candidatura = new Candidatura();
        candidatura.setUsuario(usuario);
        candidatura.setVaga(vaga);
        candidatura.setStatus(StatusCandidatura.PENDENTE);
        candidatura.setObservacao(observacao);

        if (curriculoId != null) {
            Curriculo curriculo = curriculoRepository.findByIdAndUsuarioId(curriculoId, usuario.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Curriculo nao encontrado: " + curriculoId));
            candidatura.setCurriculo(curriculo);
        }

        return candidaturaRepository.save(candidatura);
    }

    @Transactional
    public Candidatura atualizarStatus(String email, Long id, StatusCandidatura novoStatus, String observacao) {
        Candidatura candidatura = buscarPorId(email, id);
        candidatura.setStatus(novoStatus);
        if (observacao != null && !observacao.isBlank()) {
            candidatura.setObservacao(observacao);
        }
        return candidaturaRepository.save(candidatura);
    }

    @Transactional
    public void remover(String email, Long id) {
        Candidatura candidatura = buscarPorId(email, id);
        if (candidatura.getStatus() != StatusCandidatura.PENDENTE) {
            throw new BusinessException("Apenas candidaturas pendentes podem ser removidas.");
        }
        candidaturaRepository.delete(candidatura);
    }
}
