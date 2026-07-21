package com.smartjobai.core.service;

import com.smartjobai.core.entity.Experiencia;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.ExperienciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExperienciaServiceUnitTest {

    @Mock
    private ExperienciaRepository repository;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private ExperienciaService experienciaService;

    private Usuario usuarioDono;

    @BeforeEach
    void setUp() {
        usuarioDono = new Usuario();
        usuarioDono.setId(1L);
        usuarioDono.setEmail("dono@email.com");
        usuarioDono.setNome("Dono");
    }

    @Test
    void deveListarExperienciasDoUsuarioAutenticado() {
        when(usuarioService.buscarPorEmail("dono@email.com")).thenReturn(usuarioDono);
        when(repository.findByUsuarioIdOrderByDataInicioDesc(1L))
                .thenReturn(List.of(new Experiencia()));

        List<Experiencia> resultado = experienciaService.listarPorUsuario("dono@email.com");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void deveAssociarExperienciaAoUsuarioAoCriar() {
        when(usuarioService.buscarPorEmail("dono@email.com")).thenReturn(usuarioDono);
        when(repository.save(any(Experiencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Experiencia nova = new Experiencia();
        nova.setEmpresa("Empresa Exemplo");
        nova.setCargo("Desenvolvedor Java");
        nova.setTecnologias(Set.of("Java", "Spring"));

        Experiencia criada = experienciaService.criar("dono@email.com", nova);

        assertThat(criada.getUsuario()).isEqualTo(usuarioDono);
        assertThat(criada.getTecnologias()).contains("Java", "Spring");
    }

    @Test
    void deveLancarResourceNotFoundQuandoExperienciaNaoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> experienciaService.buscarPorId("dono@email.com", 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deveLancarBusinessExceptionAoAcessarExperienciaDeOutroUsuario() {
        Experiencia experienciaDeOutroUsuario = new Experiencia();
        Usuario outroUsuario = new Usuario();
        outroUsuario.setEmail("outro@email.com");
        experienciaDeOutroUsuario.setUsuario(outroUsuario);

        when(repository.findById(5L)).thenReturn(Optional.of(experienciaDeOutroUsuario));

        assertThatThrownBy(() -> experienciaService.buscarPorId("dono@email.com", 5L))
                .isInstanceOf(BusinessException.class);
    }
}
