package com.smartjobai.core.service;

import com.smartjobai.core.entity.Certificacao;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.CertificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificacaoService {

    private final CertificacaoRepository repository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<Certificacao> listarPorUsuario(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return repository.findByUsuarioId(usuario.getId());
    }

    @Transactional(readOnly = true)
    public Certificacao buscarPorId(String email, Long id) {
        Certificacao certificacao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certificação não encontrada: " + id));
        validarPropriedade(email, certificacao);
        return certificacao;
    }

    @Transactional
    public Certificacao criar(String email, Certificacao certificacao) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        certificacao.setUsuario(usuario);
        return repository.save(certificacao);
    }

    @Transactional
    public Certificacao atualizar(String email, Long id, Certificacao dados) {
        Certificacao existente = buscarPorId(email, id);
        existente.setNome(dados.getNome());
        existente.setInstituicaoEmissora(dados.getInstituicaoEmissora());
        existente.setDataEmissao(dados.getDataEmissao());
        existente.setDataExpiracao(dados.getDataExpiracao());
        existente.setCredencialUrl(dados.getCredencialUrl());
        return repository.save(existente);
    }

    @Transactional
    public void remover(String email, Long id) {
        repository.delete(buscarPorId(email, id));
    }

    private void validarPropriedade(String email, Certificacao certificacao) {
        if (!certificacao.getUsuario().getEmail().equalsIgnoreCase(email)) {
            throw new BusinessException("Você não tem permissão para acessar este recurso");
        }
    }
}
