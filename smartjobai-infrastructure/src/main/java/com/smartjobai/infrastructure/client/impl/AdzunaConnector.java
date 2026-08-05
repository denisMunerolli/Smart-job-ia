package com.smartjobai.infrastructure.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartjobai.core.entity.Vaga;
import com.smartjobai.infrastructure.client.VagaConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Integração real com a API pública do Adzuna.
 * Documentação: https://developer.adzuna.com/
 *
 * Variáveis de ambiente necessárias no Railway:
 *   ADZUNA_APP_ID  — App ID obtido em https://developer.adzuna.com/signup
 *   ADZUNA_APP_KEY — App Key gerada no painel
 *   ADZUNA_COUNTRY — país (br, us, gb, etc.) padrão: br
 *
 * Plano gratuito: 250 chamadas/mês.
 * Ativado automaticamente quando ADZUNA_APP_ID estiver definido.
 */
@Component
@ConditionalOnProperty(name = "adzuna.app-id")
@Slf4j
public class AdzunaConnector implements VagaConnector {

    private static final String FONTE = "adzuna";
    private static final String BASE  = "https://api.adzuna.com/v1/api/jobs";

    @Value("${adzuna.app-id}")
    private String appId;

    @Value("${adzuna.app-key}")
    private String appKey;

    @Value("${adzuna.country:br}")
    private String country;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AdzunaConnector(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<Vaga> buscarVagas(String termo, String localizacao) {
        List<Vaga> vagas = new ArrayList<>();
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(BASE + "/{country}/search/1")
                    .queryParam("app_id", appId)
                    .queryParam("app_key", appKey)
                    .queryParam("what", termo)
                    .queryParam("where", localizacao)
                    .queryParam("results_per_page", 50)
                    .queryParam("content-type", "application/json")
                    .buildAndExpand(country)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.path("results");

            for (JsonNode job : results) {
                Vaga vaga = Vaga.builder()
                        .idExterno(job.path("id").asText())
                        .fonte(FONTE)
                        .titulo(job.path("title").asText(null))
                        .empresa(job.path("company").path("display_name").asText(null))
                        .localizacao(job.path("location").path("display_name").asText(null))
                        .descricao(job.path("description").asText(null))
                        .dataColeta(LocalDateTime.now())
                        .build();
                vagas.add(vaga);
            }
            log.info("[Adzuna] {} vagas encontradas para '{}' em '{}'", vagas.size(), termo, localizacao);
        } catch (Exception e) {
            log.error("[Adzuna] Erro ao buscar vagas: {}", e.getMessage());
        }
        return vagas;
    }

    @Override
    public Vaga detalharVaga(String idExterno) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl(BASE + "/{country}/search/1")
                    .queryParam("app_id", appId)
                    .queryParam("app_key", appKey)
                    .queryParam("what", idExterno)
                    .queryParam("content-type", "application/json")
                    .buildAndExpand(country)
                    .toUriString();

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode job = root.path("results").get(0);
            if (job != null) {
                return Vaga.builder()
                        .idExterno(job.path("id").asText())
                        .fonte(FONTE)
                        .titulo(job.path("title").asText(null))
                        .empresa(job.path("company").path("display_name").asText(null))
                        .localizacao(job.path("location").path("display_name").asText(null))
                        .descricao(job.path("description").asText(null))
                        .dataColeta(LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            log.error("[Adzuna] Erro ao detalhar vaga {}: {}", idExterno, e.getMessage());
        }
        return null;
    }

    @Override
    public void candidatar(Vaga vaga, String curriculoJson) {
        // Adzuna é apenas fonte de descoberta — candidatura acontece no site original
        log.info("[Adzuna] Candidatura registrada internamente para a vaga '{}'. " +
                 "Acesse a vaga diretamente para candidatura externa.", vaga.getTitulo());
    }
}
