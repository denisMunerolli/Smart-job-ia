package com.smartjobai.core.repository;

import com.smartjobai.core.entity.Candidatura;
import com.smartjobai.core.entity.StatusCandidatura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    Page<Candidatura> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId, Pageable pageable);

    List<Candidatura> findByUsuarioIdAndStatusOrderByDataCriacaoDesc(Long usuarioId, StatusCandidatura status);

    Optional<Candidatura> findByIdAndUsuarioId(Long id, Long usuarioId);

    boolean existsByUsuarioIdAndVagaId(Long usuarioId, Long vagaId);
}
