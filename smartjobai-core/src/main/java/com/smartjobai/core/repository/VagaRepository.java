package com.smartjobai.core.repository;

import com.smartjobai.core.entity.Vaga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {

    Optional<Vaga> findByIdExternoAndFonte(String idExterno, String fonte);

    boolean existsByIdExternoAndFonte(String idExterno, String fonte);

    @Query("SELECT v FROM Vaga v WHERE " +
           "(:titulo IS NULL OR LOWER(v.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) AND " +
           "(:empresa IS NULL OR LOWER(v.empresa) LIKE LOWER(CONCAT('%', :empresa, '%'))) AND " +
           "(:localizacao IS NULL OR LOWER(v.localizacao) LIKE LOWER(CONCAT('%', :localizacao, '%')))")
    Page<Vaga> buscarComFiltros(
            @Param("titulo") String titulo,
            @Param("empresa") String empresa,
            @Param("localizacao") String localizacao,
            Pageable pageable
    );
}
