package com.smartjobai.core.repository;

import com.smartjobai.core.entity.Curriculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculoRepository extends JpaRepository<Curriculo, Long> {
    List<Curriculo> findByUsuarioId(Long usuarioId);
    List<Curriculo> findByUsuarioIdAndAtivoTrue(Long usuarioId);
}
