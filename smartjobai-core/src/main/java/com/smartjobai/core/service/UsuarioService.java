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
    public Usuario atualizarPerfil(String email, String nome, String linkedinUrl,
                                    String githubUrl, String portfolioUrl) {
        Usuario usuario = buscarPorEmail(email);
        if (nome != null && !nome.isBlank()) usuario.setNome(nome);
        usuario.setLinkedinUrl(linkedinUrl);
        usuario.setGithubUrl(githubUrl);
        usuario.setPortfolioUrl(portfolioUrl);
        return repository.save(usuario);
    }

    /**
     * Altera a senha do usuário.
     * Requer a senha atual para confirmar identidade.
     */
    @Transactional
    public void alterarSenha(String email, String senhaAtual, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 8) {
            throw new BusinessException("A nova senha deve ter pelo menos 8 caracteres.");
        }
        Usuario usuario = buscarPorEmail(email);
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new BusinessException("Senha atual incorreta.");
        }
        if (passwordEncoder.matches(novaSenha, usuario.getSenha())) {
            throw new BusinessException("A nova senha deve ser diferente da senha atual.");
        }
        usuario.setSenha(passwordEncoder.encode(novaSenha));
        repository.save(usuario);
    }

    /**
     * Exclui permanentemente a conta e todos os dados associados.
     * Requer confirmação da senha.
     */
    @Transactional
    public void deletarConta(String email, String senhaConfirmacao) {
        Usuario usuario = buscarPorEmail(email);
        if (!passwordEncoder.matches(senhaConfirmacao, usuario.getSenha())) {
            throw new BusinessException("Senha incorreta. A conta não foi excluída.");
        }
        repository.delete(usuario);
    }
}
