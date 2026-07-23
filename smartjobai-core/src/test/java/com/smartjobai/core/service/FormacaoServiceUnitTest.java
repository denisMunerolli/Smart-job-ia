package com.smartjobai.core.service;

import com.smartjobai.core.entity.Formacao;
import com.smartjobai.core.entity.NivelFormacao;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.FormacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FormacaoServiceUnitTest {

    @Mock
    private FormacaoRepository repository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private FormacaoService formacaoService;

    private Usuario usuarioDono;

    @BeforeEach
    void setUp() {
        usuarioDono = new Usuario();
        usuarioDono.setId(1L);
        usuarioDono.setEmail("dono@email.com");
        usuarioDono.setNome("Dono");
    }

    @Test
    void deveListarFormacoesDoUsuarioAutenticado() {
        when(usuarioService.buscarPorEmail("dono@email.com")).thenReturn(usuarioDono);
        when(repository.findByUsuarioIdOrderByDataInicioDesc(1L))
                .thenReturn(List.of(new Formacao(), new Formacao()));

        List<Formacao> resultado = formacaoService.listarPorUsuario("dono@email.com");

        assertThat(resultado).hasSize(2);
    }

    @Test
    void deveAssociarFormacaoAoUsuarioAoCriar() {
        when(usuarioService.buscarPorEmail("dono@email.com")).thenReturn(usuarioDono);
        when(repository.save(any(Formacao.class))).thenAnswer(inv -> inv.getArgument(0));

        Formacao nova = new Formacao();
        nova.setInstituicao("Universidade Exemplo");
        nova.setCurso("ADS");
        nova.setNivel(NivelFormacao.TECNICO);

        Formacao criada = formacaoService.criar("dono@email.com", nova);

        assertThat(criada.getUsuario()).isEqualTo(usuarioDono);
    }

    @Test
    void deveLancarResourceNotFoundQuandoFormacaoNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> formacaoService.buscarPorId("dono@email.com", 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveLancarBusinessExceptionAoAcessarFormacaoDeOutroUsuario() {
        Formacao formacaoDeOutroUsuario = new Formacao();
        Usuario outroUsuario = new Usuario();
        outroUsuario.setEmail("outro@email.com");
        formacaoDeOutroUsuario.setUsuario(outroUsuario);

        when(repository.findById(5L)).thenReturn(Optional.of(formacaoDeOutroUsuario));

        assertThatThrownBy(() -> formacaoService.buscarPorId("dono@email.com", 5L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void deveRemoverApenasSeForDonoDoRecurso() {
        Formacao formacaoDoDono = new Formacao();
        formacaoDoDono.setUsuario(usuarioDono);
        when(repository.findById(7L)).thenReturn(Optional.of(formacaoDoDono));

        formacaoService.remover("dono@email.com", 7L);

        org.mockito.Mockito.verify(repository).delete(formacaoDoDono);
    }
}
