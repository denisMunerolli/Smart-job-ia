package com.smartjobai.batch.job;

import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.repository.VagaRepository;
import com.smartjobai.infrastructure.client.VagaConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VagaImportJob {

    private final List<VagaConnector> connectors;
    private final VagaRepository vagaRepository;

    @Scheduled(cron = "0 0 6 * * ?")
    public void importarVagas() {
        for (VagaConnector connector : connectors) {
            List<Vaga> vagas = connector.buscarVagas("Java", "Brasil");
            int novas = 0;
            for (Vaga vaga : vagas) {
                if (!vagaRepository.existsByIdExternoAndFonte(vaga.getIdExterno(), vaga.getFonte())) {
                    vagaRepository.save(vaga);
                    novas++;
                }
            }
            log.info("Importadas {} vagas novas de {} (total retornado: {})",
                    novas, connector.getClass().getSimpleName(), vagas.size());
        }
    }
}
