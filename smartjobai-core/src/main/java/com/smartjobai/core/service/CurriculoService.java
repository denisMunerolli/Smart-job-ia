package com.smartjobai.core.service;

import com.smartjobai.core.entity.Curriculo;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.CurriculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurriculoService {

    private final CurriculoRepository repository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<Curriculo> listarPorUsuario(String email) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return repository.findByUsuarioIdOrderByVersaoDesc(usuario.getId());
    }

    @Transactional(readOnly = true)
    public Curriculo buscarPorId(String email, Long id) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        return repository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Currículo não encontrado: " + id));
    }

    /**
     * Cria um novo currículo.
     * A versão é calculada automaticamente: max(versões existentes) + 1,
     * ou 1 se for o primeiro currículo do usuário.
     */
    @Transactional
    public Curriculo criar(String email, Curriculo curriculo) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        curriculo.setUsuario(usuario);

        List<Curriculo> existentes = repository.findByUsuarioIdOrderByVersaoDesc(usuario.getId());
        int proximaVersao = existentes.stream()
                .mapToInt(Curriculo::getVersao)
                .max()
                .orElse(0) + 1;
        curriculo.setVersao(proximaVersao);
        curriculo.setAtivo(true);

        return repository.save(curriculo);
    }

    /**
     * Atualiza título e conteúdo do currículo.
     * Não altera versão nem status ativo — use os endpoints específicos para isso.
     */
    @Transactional
    public Curriculo atualizar(String email, Long id, Curriculo dados) {
        Curriculo existente = buscarPorId(email, id);
        existente.setTitulo(dados.getTitulo());
        existente.setConteudoJson(dados.getConteudoJson());
        return repository.save(existente);
    }

    /**
     * Ativa um currículo e desativa todos os outros do usuário.
     * Garante que só haja um currículo ativo por vez.
     */
    @Transactional
    public Curriculo ativar(String email, Long id) {
        Usuario usuario = usuarioService.buscarPorEmail(email);
        Curriculo alvo = repository.findByIdAndUsuarioId(id, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Currículo não encontrado: " + id));

        repository.findByUsuarioIdOrderByVersaoDesc(usuario.getId())
                .forEach(c -> c.setAtivo(false));

        alvo.setAtivo(true);
        return repository.save(alvo);
    }

    @Transactional
    public void remover(String email, Long id) {
        Curriculo curriculo = buscarPorId(email, id);
        if (curriculo.isAtivo()) {
            throw new BusinessException("Não é possível excluir o currículo ativo. Ative outro currículo antes de excluir este.");
        }
        repository.delete(curriculo);
    }
}
