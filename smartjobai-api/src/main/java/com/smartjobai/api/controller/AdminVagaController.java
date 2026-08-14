package com.smartjobai.api.controller;

import com.smartjobai.batch.job.VagaImportJob;
import com.smartjobai.core.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/vagas")
@RequiredArgsConstructor
public class AdminVagaController {

    private final VagaImportJob vagaImportJob;
    private final VagaRepository vagaRepository;

    /** Dispara importação manual de vagas */
    @PostMapping("/importar")
    public ResponseEntity<String> importar() {
        vagaImportJob.importarVagas();
        return ResponseEntity.ok("Importacao de vagas iniciada com sucesso.");
    }

    /**
     * Limpa vagas sem descrição e reimporta.
     * Útil para remover o lixo do mock antigo.
     * DELETE /api/admin/vagas/sem-descricao
     */
    @DeleteMapping("/sem-descricao")
    @Transactional
    public ResponseEntity<Map<String, Object>> limparSemDescricao() {
        long antes = vagaRepository.count();
        vagaRepository.deleteAll(
            vagaRepository.findAll().stream()
                .filter(v -> v.getDescricao() == null || v.getDescricao().isBlank())
                .toList()
        );
        long removidas = antes - vagaRepository.count();
        vagaImportJob.importarVagas();
        return ResponseEntity.ok(Map.of(
            "removidas", removidas,
            "mensagem", "Vagas sem descrição removidas e reimportação iniciada."
        ));
    }
}
