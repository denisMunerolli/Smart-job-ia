package com.smartjobai.core.service;

import com.smartjobai.core.entity.Experiencia;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.ExperienciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienciaService {

    private final ExperienciaRepository repository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<Experiencia> listarPorUsuario(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return repository.findByUsuarioIdOrderByDataInicioDesc(usuario.getId());
    }

    @Transactional(readOnly = true)
    public Experiencia buscarPorId(String email, Long id) {
        Experiencia experiencia = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Experiência não encontrada: " + id));
        validarPropriedade(email, experiencia);
        return experiencia;
    }

    @Transactional
    public Experiencia criar(String email, Experiencia experiencia) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        experiencia.setUsuario(usuario);
        return repository.save(experiencia);
    }

    @Transactional
    public Experiencia atualizar(String email, Long id, Experiencia dados) {
        Experiencia existente = buscarPorId(email, id);
        existente.setEmpresa(dados.getEmpresa());
        existente.setCargo(dados.getCargo());
        existente.setDataInicio(dados.getDataInicio());
        existente.setDataFim(dados.getDataFim());
        existente.setAtual(dados.isAtual());
        existente.setDescricao(dados.getDescricao());
        existente.setTecnologias(dados.getTecnologias());
        return repository.save(existente);
    }

    @Transactional
    public void remover(String email, Long id) {
        repository.delete(buscarPorId(email, id));
    }

    private void validarPropriedade(String email, Experiencia experiencia) {
        if (!experiencia.getUsuario().getEmail().equalsIgnoreCase(email)) {
            throw new BusinessException("Você não tem permissão para acessar este recurso");
        }
    }
}
