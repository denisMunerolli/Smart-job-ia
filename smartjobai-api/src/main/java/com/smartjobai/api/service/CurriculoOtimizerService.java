package com.smartjobai.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartjobai.core.entity.Curriculo;
import com.smartjobai.core.entity.Vaga;
import com.smartjobai.core.exception.BusinessException;
import com.smartjobai.core.exception.ResourceNotFoundException;
import com.smartjobai.core.repository.CurriculoRepository;
import com.smartjobai.core.repository.VagaRepository;
import com.smartjobai.core.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Serviço que usa a API do Claude (Anthropic) para otimizar
 * automaticamente o currículo do usuário para uma vaga específica.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurriculoOtimizerService {

    private static final String ANTHROPIC_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-sonnet-4-6";

    @Value("${anthropic.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final UsuarioService usuarioService;
    private final CurriculoRepository curriculoRepository;
    private final VagaRepository vagaRepository;

    public record OtimizacaoResult(
            String curriculoOtimizado,
            List<String> mudancasFeitas,
            List<String> habilidadesDestacadas,
            int scoreEstimado
    ) {}

    @Transactional(readOnly = true)
    public OtimizacaoResult otimizarParaVaga(String emailUsuario, Long vagaId, Long curriculoId) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(
                "API key do Claude não configurada. Adicione ANTHROPIC_API_KEY nas variáveis do Railway.");
        }

        var usuario = usuarioService.buscarPorEmail(emailUsuario);

        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new ResourceNotFoundException("Vaga não encontrada: " + vagaId));

        Curriculo curriculo = curriculoRepository.findByIdAndUsuarioId(curriculoId, usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Currículo não encontrado: " + curriculoId));

        String conteudoCurriculo = curriculo.getConteudoJson() != null
                ? curriculo.getConteudoJson()
                : curriculo.getTitulo();

        String prompt = montarPrompt(vaga, conteudoCurriculo);
        String respostaIA = chamarClaudeApi(prompt);
        return parsearResposta(respostaIA);
    }

    @Transactional(readOnly = true)
    public OtimizacaoResult otimizarTextoLivre(String textoVaga, String textoCurriculo) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new BusinessException(
                "API key do Claude não configurada. Adicione ANTHROPIC_API_KEY nas variáveis do Railway.");
        }
        String prompt = montarPromptTextoLivre(textoVaga, textoCurriculo);
        String respostaIA = chamarClaudeApi(prompt);
        return parsearResposta(respostaIA);
    }

    private String montarPrompt(Vaga vaga, String curriculo) {
        return String.format("""
            Você é um especialista em recursos humanos e otimização de currículos.
            
            VAGA:
            Título: %s
            Empresa: %s
            Descrição: %s
            
            CURRÍCULO ATUAL:
            %s
            
            TAREFA:
            Otimize o currículo para essa vaga específica. Destaque habilidades relevantes,
            ajuste a linguagem para combinar com a cultura da empresa e inclua palavras-chave
            da descrição da vaga.
            
            Responda APENAS em JSON válido com este formato exato:
            {
              "curriculoOtimizado": "texto completo do currículo otimizado",
              "mudancasFeitas": ["mudança 1", "mudança 2", "mudança 3"],
              "habilidadesDestacadas": ["skill 1", "skill 2", "skill 3"],
              "scoreEstimado": 85
            }
            """,
            vaga.getTitulo(), vaga.getEmpresa(), vaga.getDescricao(), curriculo
        );
    }

    private String montarPromptTextoLivre(String textoVaga, String textoCurriculo) {
        return String.format("""
            Você é um especialista em recursos humanos e otimização de currículos.
            
            DESCRIÇÃO DA VAGA:
            %s
            
            CURRÍCULO ATUAL:
            %s
            
            TAREFA:
            Otimize o currículo para essa vaga. Destaque habilidades relevantes,
            ajuste a linguagem e inclua palavras-chave da descrição da vaga.
            
            Responda APENAS em JSON válido com este formato exato:
            {
              "curriculoOtimizado": "texto completo do currículo otimizado",
              "mudancasFeitas": ["mudança 1", "mudança 2", "mudança 3"],
              "habilidadesDestacadas": ["skill 1", "skill 2", "skill 3"],
              "scoreEstimado": 85
            }
            """,
            textoVaga, textoCurriculo
        );
    }

    private String chamarClaudeApi(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");

            Map<String, Object> body = Map.of(
                "model", MODEL,
                "max_tokens", 2000,
                "messages", List.of(Map.of("role", "user", "content", prompt))
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(ANTHROPIC_API_URL, request, String.class);
            JsonNode root = objectMapper.readTree(response);
            return root.path("content").get(0).path("text").asText();
        } catch (Exception e) {
            log.error("Erro ao chamar Claude API: {}", e.getMessage());
            throw new BusinessException("Erro ao processar com IA: " + e.getMessage());
        }
    }

    private OtimizacaoResult parsearResposta(String respostaIA) {
        try {
            // Remove markdown se presente
            String json = respostaIA
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            JsonNode node = objectMapper.readTree(json);
            String curriculoOtimizado = node.path("curriculoOtimizado").asText();
            List<String> mudancas = objectMapper.convertValue(
                    node.path("mudancasFeitas"), objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class));
            List<String> habilidades = objectMapper.convertValue(
                    node.path("habilidadesDestacadas"), objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, String.class));
            int score = node.path("scoreEstimado").asInt(70);

            return new OtimizacaoResult(curriculoOtimizado, mudancas, habilidades, score);
        } catch (Exception e) {
            log.error("Erro ao parsear resposta da IA: {}", e.getMessage());
            throw new BusinessException("Erro ao interpretar resposta da IA.");
        }
    }
}
