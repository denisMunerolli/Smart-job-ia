package com.smartjobai.core.repository;

import com.smartjobai.core.entity.Idioma;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdiomaRepository extends JpaRepository<Idioma, Long> {
    List<Idioma> findByUsuarioId(Long usuarioId);
}
