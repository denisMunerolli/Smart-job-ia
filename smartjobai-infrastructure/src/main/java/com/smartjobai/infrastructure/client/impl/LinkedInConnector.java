package com.smartjobai.infrastructure.client.impl;

import com.smartjobai.core.entity.Vaga;
import com.smartjobai.infrastructure.client.VagaConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/*
 * Implementação SIMULADA de propósito: integração real com o LinkedIn
 * exigiria a API oficial (credenciais aprovadas pela plataforma) — scraping
 * não autorizado viola os termos de uso.
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
        log.info("[MOCK] Candidatura preparada para a vaga {} — revisão manual necessária antes do envio.",
                vaga.getTitulo());
    }
}
