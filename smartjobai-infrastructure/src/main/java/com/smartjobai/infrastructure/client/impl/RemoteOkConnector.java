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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Integração com RemoteOK - API pública gratuita de vagas remotas.
 * Documentação: https://remoteok.com/api
 * Sem limite de chamadas, sem autenticação necessária.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RemoteOkConnector implements VagaConnector {

    private static final String FONTE = "remoteok";
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
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            // Primeiro elemento é metadata, pular
            for (int i = 1; i < root.size(); i++) {
                JsonNode job = root.get(i);
                if (!job.has("id")) continue;

                String titulo = job.path("position").asText(null);
                String empresa = job.path("company").asText(null);
                String descricao = job.path("description").asText(null);
                String tags = job.path("tags").toString();

                Vaga vaga = Vaga.builder()
                        .idExterno("remoteok-" + job.path("id").asText())
                        .fonte(FONTE)
                        .titulo(titulo)
                        .empresa(empresa)
                        .localizacao("Remoto")
                        .descricao(descricao != null ? descricao + " Tags: " + tags : tags)
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
    public Vaga detalharVaga(String idExterno) {
        return null;
    }

    @Override
    public void candidatar(Vaga vaga, String curriculoJson) {
        log.info("[RemoteOK] Candidatura registrada para '{}'. Acesse remoteok.com para candidatura.", vaga.getTitulo());
    }
}
