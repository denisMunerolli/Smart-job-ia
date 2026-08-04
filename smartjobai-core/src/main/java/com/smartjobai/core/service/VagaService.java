package com.smartjobai.core.service;

import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
