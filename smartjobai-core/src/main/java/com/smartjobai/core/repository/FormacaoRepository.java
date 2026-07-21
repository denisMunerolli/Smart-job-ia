package com.smartjobai.core.repository;

import com.smartjobai.core.entity.Formacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormacaoRepository extends JpaRepository<Formacao, Long> {
    List<Formacao> findByUsuarioId(Long usuarioId);
    List<Formacao> findByUsuarioIdOrderByDataInicioDesc(Long usuarioId);
}
