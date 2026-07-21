package com.smartjobai.core.repository;

import com.smartjobai.core.entity.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    Optional<Vaga> findByIdExternoAndFonte(String idExterno, String fonte);
    boolean existsByIdExternoAndFonte(String idExterno, String fonte);
}
