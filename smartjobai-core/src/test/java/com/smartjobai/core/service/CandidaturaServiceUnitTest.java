package com.smartjobai.core.service;

import com.smartjobai.core.entity.Candidatura;
import com.smartjobai.core.entity.StatusCandidatura;
import com.smartjobai.core.entity.Usuario;
import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.CandidaturaRepository;
import com.smartjobai.core.repository.CurriculoRepository;
import com.smartjobai.core.repository.VagaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidaturaServiceUnitTest {

    @Mock private CandidaturaRepository candidaturaRepository;
    @Mock private VagaRepository vagaRepository;
    @Mock private CurriculoRepository curriculoRepository;
    @Mock private UsuarioService usuarioService;

    @InjectMocks private CandidaturaService service;

    private Usuario usuario;
    private Vaga vaga;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setEmail("denis@test.com");

        vaga = new Vaga();
        vaga.setTitulo("Dev Java");
    }

    @Test
    void candidatar_vagaExistente_deveCriarCandidatura() {
        when(usuarioService.buscarPorEmail("denis@test.com")).thenReturn(usuario);
        when(candidaturaRepository.existsByUsuarioIdAndVagaId(any(), any())).thenReturn(false);
        when(vagaRepository.findById(1L)).thenReturn(Optional.of(vaga));
        when(candidaturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Candidatura resultado = service.candidatar("denis@test.com", 1L, null, "Tenho interesse");

        assertThat(resultado.getStatus()).isEqualTo(StatusCandidatura.PENDENTE);
        assertThat(resultado.getObservacao()).isEqualTo("Tenho interesse");
        assertThat(resultado.getVaga()).isEqualTo(vaga);
    }

    @Test
    void candidatar_jaExistente_deveLancarBusinessException() {
        when(usuarioService.buscarPorEmail("denis@test.com")).thenReturn(usuario);
        when(candidaturaRepository.existsByUsuarioIdAndVagaId(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.candidatar("denis@test.com", 1L, null, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ja se candidatou");
    }

    @Test
    void candidatar_vagaInexistente_deveLancarResourceNotFoundException() {
        when(usuarioService.buscarPorEmail("denis@test.com")).thenReturn(usuario);
        when(candidaturaRepository.existsByUsuarioIdAndVagaId(any(), any())).thenReturn(false);
        when(vagaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.candidatar("denis@test.com", 99L, null, null))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void atualizarStatus_deveAlterarStatusEObservacao() {
        Candidatura candidatura = new Candidatura();
        candidatura.setStatus(StatusCandidatura.PENDENTE);

        when(usuarioService.buscarPorEmail("denis@test.com")).thenReturn(usuario);
        when(candidaturaRepository.findByIdAndUsuarioId(any(), any())).thenReturn(Optional.of(candidatura));
        when(candidaturaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Candidatura resultado = service.atualizarStatus("denis@test.com", 1L, StatusCandidatura.ENVIADA, "Enviado pelo portal");

        assertThat(resultado.getStatus()).isEqualTo(StatusCandidatura.ENVIADA);
        assertThat(resultado.getObservacao()).isEqualTo("Enviado pelo portal");
    }

    @Test
    void remover_candidaturaNaoPendente_deveLancarBusinessException() {
        Candidatura candidatura = new Candidatura();
        candidatura.setStatus(StatusCandidatura.ENVIADA);

        when(usuarioService.buscarPorEmail("denis@test.com")).thenReturn(usuario);
        when(candidaturaRepository.findByIdAndUsuarioId(any(), any())).thenReturn(Optional.of(candidatura));

        assertThatThrownBy(() -> service.remover("denis@test.com", 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("pendentes");
    }
}
