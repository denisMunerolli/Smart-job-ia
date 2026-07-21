package com.smartjobai.core.service;

import com.smartjobai.core.entity.Idioma;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.IdiomaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IdiomaService {

    private final IdiomaRepository repository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<Idioma> listarPorUsuario(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return repository.findByUsuarioId(usuario.getId());
    }

    @Transactional(readOnly = true)
    public Idioma buscarPorId(String email, Long id) {
        Idioma idioma = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idioma não encontrado: " + id));
        validarPropriedade(email, idioma);
        return idioma;
    }

    @Transactional
    public Idioma criar(String email, Idioma idioma) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        idioma.setUsuario(usuario);
        return repository.save(idioma);
    }

    @Transactional
    public Idioma atualizar(String email, Long id, Idioma dados) {
        Idioma existente = buscarPorId(email, id);
        existente.setNome(dados.getNome());
        existente.setNivel(dados.getNivel());
        return repository.save(existente);
    }

    @Transactional
    public void remover(String email, Long id) {
        repository.delete(buscarPorId(email, id));
    }

    private void validarPropriedade(String email, Idioma idioma) {
        if (!idioma.getUsuario().getEmail().equalsIgnoreCase(email)) {
            throw new BusinessException("Você não tem permissão para acessar este recurso");
        }
    }
}
