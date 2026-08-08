package com.smartjobai.core.repository;

import com.smartjobai.core.entity.Curriculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CurriculoRepository extends JpaRepository<Curriculo, Long> {

    List<Curriculo> findByUsuarioIdOrderByVersaoDesc(Long usuarioId);

    Optional<Curriculo> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<Curriculo> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    boolean existsByUsuarioIdAndAtivoTrue(Long usuarioId);

    long countByUsuarioId(Long usuarioId);
}
