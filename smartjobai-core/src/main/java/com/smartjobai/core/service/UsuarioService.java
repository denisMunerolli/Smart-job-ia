package com.smartjobai.core.service;

import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario cadastrar(Usuario usuario) {
        if (repository.existsByEmail(usuario.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return repository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + email));
    }

    /**
     * Carrega o usuário com todas as coleções de cadastro inicializadas,
     * para uso no endpoint GET /api/usuarios/me (perfil completo).
     */
    @Transactional(readOnly = true)
    public Usuario buscarPerfilCompleto(String email) {
        Usuario usuario = buscarPorEmail(email);
        usuario.getFormacoes().size();
        usuario.getExperiencias().size();
        usuario.getIdiomas().size();
        usuario.getCertificacoes().size();
        usuario.getHabilidadesTecnicas().size();
        return usuario;
    }

    @Transactional
    public Usuario atualizarPerfil(String email, String nome, String linkedinUrl, String githubUrl, String portfolioUrl) {
        Usuario usuario = buscarPorEmail(email);
        if (nome != null && !nome.isBlank()) {
            usuario.setNome(nome);
        }
        usuario.setLinkedinUrl(linkedinUrl);
        usuario.setGithubUrl(githubUrl);
        usuario.setPortfolioUrl(portfolioUrl);
        return repository.save(usuario);
    }
}
