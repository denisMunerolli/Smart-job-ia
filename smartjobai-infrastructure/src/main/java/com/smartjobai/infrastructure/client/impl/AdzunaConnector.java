package com.smartjobai.infrastructure.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartjobai.core.entity.Vaga;
import com.smartjobai.infrastructure.client.VagaConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@ConditionalOnProperty(name = "adzuna.app-id")
@RequiredArgsConstructor
@Slf4j
public class AdzunaConnector implements VagaConnector {

    private static final String FONTE = "adzuna";
    private static final String BASE_URL = "https://api.adzuna.com/v1/api/jobs";

    @Value("${adzuna.app-id}")
    private String appId;

    @Value("${adzuna.app-key}")
    private String appKey;

    @Value("${adzuna.country:br}")
    private String country;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<Vaga> buscarVagas(String termo, String localizacao) {
        List<Vaga> vagas = new ArrayList<>();
        try {
            String url = String.format(
                "%s/%s/search/1?app_id=%s&app_key=%s&what=%s&where=%s&results_per_page=20&content-type=application/json",
                BASE_URL, country, appId, appKey,
                termo.replace(" ", "%20"),
                localizacao.replace(" ", "%20")
            );

            String response = restTemplate.getForObject(url, String.class);
            JsonNode root   = objectMapper.readTree(response);
            JsonNode results = root.path("results");

            for (JsonNode job : results) {
                String redirectUrl = job.path("redirect_url").asText(null);
                String descricao   = job.path("description").asText(null);
                String empresa     = job.path("company").path("display_name").asText(null);
                String localizacaoVaga = job.path("location").path("display_name").asText(null);

                Vaga vaga = Vaga.builder()
                        .idExterno("adzuna-" + job.path("id").asText())
                        .fonte(FONTE)
                        .titulo(job.path("title").asText(null))
                        .empresa(empresa)
                        .descricao(descricao)
                        .localizacao(localizacaoVaga)
                        .urlOrigem(redirectUrl)
                        .dataColeta(LocalDateTime.now())
                        .build();
                vagas.add(vaga);
            }
            log.info("[Adzuna] {} vagas encontradas para '{}'", vagas.size(), termo);
        } catch (Exception e) {
            log.error("[Adzuna] Erro ao buscar vagas: {}", e.getMessage());
        }
        return vagas;
    }

    @Override
    public Vaga detalharVaga(String idExterno) { return null; }

    @Override
    public void candidatar(Vaga vaga, String curriculoJson) {
        log.info("[Adzuna] Candidatura registrada para '{}'. Acesse: {}", vaga.getTitulo(), vaga.getUrlOrigem());
    }
}
