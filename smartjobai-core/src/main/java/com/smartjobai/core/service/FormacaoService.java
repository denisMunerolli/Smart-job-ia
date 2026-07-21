package com.smartjobai.core.service;

import com.smartjobai.core.entity.Formacao;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.FormacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormacaoService {

    private final FormacaoRepository repository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<Formacao> listarPorUsuario(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return repository.findByUsuarioIdOrderByDataInicioDesc(usuario.getId());
    }

    @Transactional(readOnly = true)
    public Formacao buscarPorId(String email, Long id) {
        Formacao formacao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formação não encontrada: " + id));
        validarPropriedade(email, formacao);
        return formacao;
    }

    @Transactional
    public Formacao criar(String email, Formacao formacao) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        formacao.setUsuario(usuario);
        return repository.save(formacao);
    }

    @Transactional
    public Formacao atualizar(String email, Long id, Formacao dados) {
        Formacao existente = buscarPorId(email, id);
        existente.setInstituicao(dados.getInstituicao());
        existente.setCurso(dados.getCurso());
        existente.setNivel(dados.getNivel());
        existente.setDataInicio(dados.getDataInicio());
        existente.setDataFim(dados.getDataFim());
        existente.setEmAndamento(dados.isEmAndamento());
        existente.setDescricao(dados.getDescricao());
        return repository.save(existente);
    }

    @Transactional
    public void remover(String email, Long id) {
        repository.delete(buscarPorId(email, id));
    }

    private void validarPropriedade(String email, Formacao formacao) {
        if (!formacao.getUsuario().getEmail().equalsIgnoreCase(email)) {
            throw new BusinessException("Você não tem permissão para acessar este recurso");
        }
    }
}
