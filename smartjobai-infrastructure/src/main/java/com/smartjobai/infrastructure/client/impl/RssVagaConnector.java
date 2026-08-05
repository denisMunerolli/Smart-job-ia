package com.smartjobai.infrastructure.client.impl;

import com.smartjobai.core.entity.Vaga;
import com.smartjobai.infrastructure.client.VagaConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Conector RSS para fontes que publicam vagas em formato Atom/RSS.
 * Padrão: usa o feed de vagas de TI do programathor.com.br (público, gratuito).
 *
 * Variável de ambiente:
 *   RSS_VAGAS_URL — URL do feed RSS (padrão: Programathor)
 *
 * Ativado quando RSS_VAGAS_URL estiver definido.
 */
@Component
@ConditionalOnProperty(name = "rss.vagas.url")
@Slf4j
public class RssVagaConnector implements VagaConnector {

    private static final String FONTE = "rss";

    private final String feedUrl;

    public RssVagaConnector(
            @org.springframework.beans.factory.annotation.Value("${rss.vagas.url}") String feedUrl) {
        this.feedUrl = feedUrl;
    }

    @Override
    public List<Vaga> buscarVagas(String termo, String localizacao) {
        List<Vaga> vagas = new ArrayList<>();
        try {
            URL url = new URL(feedUrl);
            try (InputStream is = url.openStream()) {
                Document doc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder().parse(is);
                doc.getDocumentElement().normalize();

                NodeList items = doc.getElementsByTagName("item");
                for (int i = 0; i < items.getLength(); i++) {
                    Element item = (Element) items.item(i);
                    String titulo    = getText(item, "title");
                    String descricao = getText(item, "description");
                    String link      = getText(item, "link");
                    String guid      = getText(item, "guid");

                    // Filtra pelo termo se informado
                    if (termo != null && !termo.isBlank()) {
                        String tituloLower = titulo != null ? titulo.toLowerCase() : "";
                        String descLower   = descricao != null ? descricao.toLowerCase() : "";
                        if (!tituloLower.contains(termo.toLowerCase()) &&
                            !descLower.contains(termo.toLowerCase())) {
                            continue;
                        }
                    }

                    Vaga vaga = Vaga.builder()
                            .idExterno(guid != null ? guid : link)
                            .fonte(FONTE)
                            .titulo(titulo)
                            .descricao(descricao)
                            .localizacao(localizacao)
                            .dataColeta(LocalDateTime.now())
                            .build();
                    vagas.add(vaga);
                }
            }
            log.info("[RSS] {} vagas encontradas para '{}'", vagas.size(), termo);
        } catch (Exception e) {
            log.error("[RSS] Erro ao processar feed {}: {}", feedUrl, e.getMessage());
        }
        return vagas;
    }

    @Override
    public Vaga detalharVaga(String idExterno) {
        return null; // RSS não suporta busca por ID
    }

    @Override
    public void candidatar(Vaga vaga, String curriculoJson) {
        log.info("[RSS] Candidatura registrada internamente para '{}'.", vaga.getTitulo());
    }

    private String getText(Element el, String tag) {
        NodeList nl = el.getElementsByTagName(tag);
        if (nl.getLength() > 0 && nl.item(0).getFirstChild() != null) {
            return nl.item(0).getFirstChild().getNodeValue();
        }
        return null;
    }
}
