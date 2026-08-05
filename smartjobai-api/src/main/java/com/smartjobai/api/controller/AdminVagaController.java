package com.smartjobai.api.controller;

import com.smartjobai.batch.job.VagaImportJob;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints administrativos para gerenciamento de vagas.
 * Requer role ADMIN (configurado no SecurityConfig).
 *
 * POST /api/admin/vagas/importar — dispara o job de importação manualmente.
 */
@RestController
@RequestMapping("/api/admin/vagas")
@RequiredArgsConstructor
public class AdminVagaController {

    private final VagaImportJob vagaImportJob;

    @PostMapping("/importar")
    public ResponseEntity<String> importar() {
        vagaImportJob.importarVagas();
        return ResponseEntity.ok("Importacao de vagas iniciada com sucesso.");
    }
}
