package com.smartjobai.core.service;

import com.smartjobai.core.entity.HabilidadeTecnica;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.HabilidadeTecnicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HabilidadeTecnicaService {

    private final HabilidadeTecnicaRepository repository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<HabilidadeTecnica> listarPorUsuario(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return repository.findByUsuarioId(usuario.getId());
    }

    @Transactional(readOnly = true)
    public HabilidadeTecnica buscarPorId(String email, Long id) {
        HabilidadeTecnica habilidade = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habilidade não encontrada: " + id));
        validarPropriedade(email, habilidade);
        return habilidade;
    }

    @Transactional
    public HabilidadeTecnica criar(String email, HabilidadeTecnica habilidade) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        habilidade.setUsuario(usuario);
        return repository.save(habilidade);
    }

    @Transactional
    public HabilidadeTecnica atualizar(String email, Long id, HabilidadeTecnica dados) {
        HabilidadeTecnica existente = buscarPorId(email, id);
        existente.setNome(dados.getNome());
        existente.setNivelProficiencia(dados.getNivelProficiencia());
        existente.setAnosExperiencia(dados.getAnosExperiencia());
        return repository.save(existente);
    }

    @Transactional
    public void remover(String email, Long id) {
        repository.delete(buscarPorId(email, id));
    }

    private void validarPropriedade(String email, HabilidadeTecnica habilidade) {
        if (!habilidade.getUsuario().getEmail().equalsIgnoreCase(email)) {
            throw new BusinessException("Você não tem permissão para acessar este recurso");
        }
    }
}
