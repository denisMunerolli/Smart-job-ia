package com.smartjobai.core.service;

import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VagaService {

    private final VagaRepository repository;

    @Transactional(readOnly = true)
    public Page<Vaga> buscar(String titulo, String empresa, String localizacao, Pageable pageable) {
        return repository.buscarComFiltros(titulo, empresa, localizacao, pageable);
    }

    @Transactional(readOnly = true)
    public Vaga buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga nao encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public long contarTotal() {
        return repository.count();
    }

    /**
     * Retorna as vagas mais recentes para o calculo de recomendacoes.
     * O ranking por score e feito no MatchingService/PerfilController.
     */
    @Transactional(readOnly = true)
    public List<Vaga> listarParaRecomendacao(int limite) {
        return repository.buscarComFiltros(null, null, null, PageRequest.of(0, limite)).getContent();
    }
}
