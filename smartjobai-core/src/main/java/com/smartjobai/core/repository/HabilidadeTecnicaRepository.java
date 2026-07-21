package com.smartjobai.core.repository;

import com.smartjobai.core.entity.HabilidadeTecnica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabilidadeTecnicaRepository extends JpaRepository<HabilidadeTecnica, Long> {
    List<HabilidadeTecnica> findByUsuarioId(Long usuarioId);
    List<HabilidadeTecnica> findByUsuarioIdAndNomeContainingIgnoreCase(Long usuarioId, String nome);
}
