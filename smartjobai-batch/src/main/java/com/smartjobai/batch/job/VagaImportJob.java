package com.smartjobai.batch.job;

import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.repository.VagaRepository;
import com.smartjobai.infrastructure.client.VagaConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Job agendado para importar vagas de todas as fontes ativas.
 *
 * Variáveis de ambiente opcionais:
 *   IMPORT_TERMOS     — termos de busca separados por virgula (padrão: Java,Python,React)
 *   IMPORT_LOCALIZACAO — localização padrão (padrão: Brasil)
 *
 * Cron: todos os dias às 6h e 18h (UTC).
 * Para rodar manualmente: POST /api/admin/vagas/importar
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class VagaImportJob {

    private final List<VagaConnector> connectors;
    private final VagaRepository vagaRepository;

    @Value("${import.termos:Java,Python,React,Spring Boot,Node.js}")
    private String termos;

    @Value("${import.localizacao:Brasil}")
    private String localizacao;

    @Scheduled(cron = "0 0 6,18 * * ?")
    @Transactional
    public void importarVagas() {
        log.info("=== VagaImportJob iniciado — {} conector(es) ativo(s) ===", connectors.size());
        String[] termoArray = termos.split(",");

        for (VagaConnector connector : connectors) {
            int totalNovas = 0;
            for (String termo : termoArray) {
                List<Vaga> vagas = connector.buscarVagas(termo.trim(), localizacao);
                for (Vaga vaga : vagas) {
                    if (vaga.getIdExterno() != null &&
                        !vagaRepository.existsByIdExternoAndFonte(vaga.getIdExterno(), vaga.getFonte())) {
                        vagaRepository.save(vaga);
                        totalNovas++;
                    }
                }
            }
            log.info("[{}] {} vagas novas importadas", connector.getClass().getSimpleName(), totalNovas);
        }
        log.info("=== VagaImportJob concluido ===");
    }
}
