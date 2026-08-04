package com.smartjobai.core.service;

import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.VagaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VagaServiceUnitTest {

    @Mock private VagaRepository repository;

    @InjectMocks private VagaService service;

    @Test
    void buscar_semFiltros_deveRetornarPaginaDeVagas() {
        Vaga vaga = new Vaga();
        vaga.setTitulo("Dev Java");

        when(repository.buscarComFiltros(any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(vaga)));

        var resultado = service.buscar(null, null, null, Pageable.unpaged());

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getTitulo()).isEqualTo("Dev Java");
    }

    @Test
    void buscarPorId_existente_deveRetornarVaga() {
        Vaga vaga = new Vaga();
        vaga.setTitulo("Dev Java");

        when(repository.findById(1L)).thenReturn(Optional.of(vaga));

        Vaga resultado = service.buscarPorId(1L);
        assertThat(resultado.getTitulo()).isEqualTo("Dev Java");
    }

    @Test
    void buscarPorId_inexistente_deveLancarResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
