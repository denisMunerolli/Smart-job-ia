package com.smartjobai.infrastructure.client.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartjobai.core.entity.Vaga;
import com.smartjobai.infrastructure.client.VagaConnector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RemoteOkConnector implements VagaConnector {

    private static final String FONTE   = "remoteok";
    private static final String BASE_URL = "https://remoteok.com/api";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public List<Vaga> buscarVagas(String termo, String localizacao) {
        List<Vaga> vagas = new ArrayList<>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "SmartJobAI/1.0");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String url = BASE_URL + "?tag=" + termo.toLowerCase().replace(" ", "-");
            var response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            for (int i = 1; i < root.size(); i++) {
                JsonNode job = root.get(i);
                if (!job.has("id")) continue;

                String slug = job.path("slug").asText(null);
                String urlOrigem = slug != null
                        ? "https://remoteok.com/remote-jobs/" + slug
                        : "https://remoteok.com";

                String tags = job.path("tags").toString()
                        .replaceAll("[\\[\\]\"]", "").replace(",", ", ");

                String descricao = job.path("description").asText("");
                if (!tags.isBlank()) descricao += "\n\nTags: " + tags;

                Vaga vaga = Vaga.builder()
                        .idExterno("remoteok-" + job.path("id").asText())
                        .fonte(FONTE)
                        .titulo(job.path("position").asText(null))
                        .empresa(job.path("company").asText(null))
                        .localizacao("Remoto")
                        .descricao(descricao.isBlank() ? null : descricao)
                        .urlOrigem(urlOrigem)
                        .dataColeta(LocalDateTime.now())
                        .build();
                vagas.add(vaga);
            }
            log.info("[RemoteOK] {} vagas encontradas para '{}'", vagas.size(), termo);
        } catch (Exception e) {
            log.error("[RemoteOK] Erro ao buscar vagas: {}", e.getMessage());
        }
        return vagas;
    }

    @Override
    public Vaga detalharVaga(String idExterno) { return null; }

    @Override
    public void candidatar(Vaga vaga, String curriculoJson) {
        log.info("[RemoteOK] Candidatura para '{}'. Acesse: {}", vaga.getTitulo(), vaga.getUrlOrigem());
    }
}
