package com.smartjobai.core.service;

import com.smartjobai.core.entity.Curriculo;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.CurriculoRepository;
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
class CurriculoServiceUnitTest {

    @Mock private CurriculoRepository repository;
    @Mock private UsuarioService usuarioService;

    @InjectMocks private CurriculoService service;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("denis@test.com");
        // ID precisa ser definido para os mocks de findByIdAndUsuarioId funcionarem
    }

    @Test
    void criar_primeiroCurriculo_deveSerVersao1() {
        when(usuarioService.buscarPorEmail("denis@test.com")).thenReturn(usuario);
        when(repository.findByUsuarioIdOrderByVersaoDesc(any())).thenReturn(List.of());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Curriculo novo = new Curriculo();
        novo.setTitulo("Curriculo Java");

        Curriculo resultado = service.criar("denis@test.com", novo);

        assertThat(resultado.getVersao()).isEqualTo(1);
        assertThat(resultado.isAtivo()).isTrue();
    }

    @Test
    void criar_quandoJaExisteVersao2_deveSerVersao3() {
        Curriculo existente = new Curriculo();
        existente.setVersao(2);

        when(usuarioService.buscarPorEmail("denis@test.com")).thenReturn(usuario);
        when(repository.findByUsuarioIdOrderByVersaoDesc(any())).thenReturn(List.of(existente));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Curriculo resultado = service.criar("denis@test.com", new Curriculo());

        assertThat(resultado.getVersao()).isEqualTo(3);
    }

    @Test
    void ativar_deveDesativarTodosEAtivarAlvo() {
        Curriculo c1 = new Curriculo(); c1.setAtivo(true);
        Curriculo c2 = new Curriculo(); c2.setAtivo(false);

        when(usuarioService.buscarPorEmail("denis@test.com")).thenReturn(usuario);
        when(repository.findByIdAndUsuarioId(any(), any())).thenReturn(Optional.of(c2));
        when(repository.findByUsuarioIdOrderByVersaoDesc(any())).thenReturn(List.of(c1, c2));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Curriculo resultado = service.ativar("denis@test.com", 2L);

        assertThat(resultado.isAtivo()).isTrue();
        assertThat(c1.isAtivo()).isFalse();
    }

    @Test
    void remover_curriculoAtivo_deveLancarBusinessException() {
        Curriculo ativo = new Curriculo();
        ativo.setAtivo(true);

        when(usuarioService.buscarPorEmail("denis@test.com")).thenReturn(usuario);
        when(repository.findByIdAndUsuarioId(any(), any())).thenReturn(Optional.of(ativo));

        assertThatThrownBy(() -> service.remover("denis@test.com", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ativo");
    }

    @Test
    void buscarPorId_naoEncontrado_deveLancarResourceNotFoundException() {
        when(usuarioService.buscarPorEmail("denis@test.com")).thenReturn(usuario);
        when(repository.findByIdAndUsuarioId(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId("denis@test.com", 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
