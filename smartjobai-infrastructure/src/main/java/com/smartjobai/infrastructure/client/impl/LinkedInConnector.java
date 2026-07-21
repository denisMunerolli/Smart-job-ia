package com.smartjobai.infrastructure.client.impl;

import com.smartjobai.core.entity.Vaga;
import com.smartjobai.infrastructure.client.VagaConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Implementação SIMULADA, como definido desde a Fase 1 do roadmap: para uma
 * integração real com o LinkedIn seria necessário usar a API oficial (exige
 * credenciais aprovadas pela plataforma) — scraping não autorizado viola os
 * termos de uso do LinkedIn. Isto fica como mock até que essa integração
 * oficial seja implementada.
 */
@Component
@Slf4j
public class LinkedInConnector implements VagaConnector {

    private static final String FONTE = "linkedin";

    @Override
    public List<Vaga> buscarVagas(String termo, String localizacao) {
        log.info("[MOCK] Buscando vagas no LinkedIn para: {} em {}", termo, localizacao);
        return List.of(
                Vaga.builder()
                        .idExterno("123")
                        .fonte(FONTE)
                        .titulo("Desenvolvedor Java")
                        .empresa("TechCorp")
                        .localizacao(localizacao)
                        .dataColeta(LocalDateTime.now())
                        .build(),
                Vaga.builder()
                        .idExterno("456")
                        .fonte(FONTE)
                        .titulo("Engenheiro de Software")
                        .empresa("InovaTI")
                        .localizacao(localizacao)
                        .dataColeta(LocalDateTime.now())
                        .build()
        );
    }

    @Override
    public Vaga detalharVaga(String idExterno) {
        return Vaga.builder()
                .idExterno(idExterno)
                .fonte(FONTE)
                .descricao("Vaga para Java com Spring...")
                .dataColeta(LocalDateTime.now())
                .build();
    }

    @Override
    public void candidatar(Vaga vaga, String curriculoJson) {
        // Candidatura automática não é implementada de propósito — a maioria
        // das plataformas proíbe isso em seus termos de uso. Este método
        // existe na interface para um fluxo futuro onde o usuário revisa e
        // confirma manualmente antes do envio.
        log.info("[MOCK] Candidatura preparada para a vaga {} — revisão manual necessária antes do envio.",
                vaga.getTitulo());
    }
}
