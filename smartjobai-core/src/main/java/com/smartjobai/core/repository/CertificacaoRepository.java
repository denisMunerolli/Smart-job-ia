package com.smartjobai.core.repository;

import com.smartjobai.core.entity.Certificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificacaoRepository extends JpaRepository<Certificacao, Long> {
    List<Certificacao> findByUsuarioId(Long usuarioId);
}
