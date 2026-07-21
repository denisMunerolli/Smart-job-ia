package com.smartjobai.core.repository;

import com.smartjobai.core.entity.Experiencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {
    List<Experiencia> findByUsuarioId(Long usuarioId);
    List<Experiencia> findByUsuarioIdOrderByDataInicioDesc(Long usuarioId);
}
