package com.smartjobai.ai.similarity;

import com.smartjobai.commons.util.TextUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TFIDFMatcher {

    private static final Set<String> STOPWORDS = Set.of(
            "de", "da", "do", "em", "com", "para", "por", "que", "se", "ou",
            "um", "uma", "os", "as", "na", "no", "ao", "dos", "das", "is",
            "the", "and", "or", "in", "of", "to", "a", "an", "at", "on"
    );

    public double calcularSimilaridade(String textoVaga, String textoCurriculo) {
        List<String> tokensVaga = tokenizar(textoVaga);
        List<String> tokensCurriculo = tokenizar(textoCurriculo);

        if (tokensVaga.isEmpty() || tokensCurriculo.isEmpty()) return 0.0;

        Set<String> vocabulario = new HashSet<>(tokensVaga);
        vocabulario.addAll(tokensCurriculo);

        Map<String, Double> tfidfVaga = calcularTFIDF(tokensVaga, vocabulario);
        Map<String, Double> tfidfCurriculo = calcularTFIDF(tokensCurriculo, vocabulario);

        return cossenoSimilaridade(tfidfVaga, tfidfCurriculo);
    }

    public List<String> habilidadesFaltantes(String textoVaga, String textoCurriculo) {
        List<String> tokensVaga = tokenizar(textoVaga);
        List<String> tokensCurriculo = tokenizar(textoCurriculo);

        Set<String> presencaCurriculo = new HashSet<>(tokensCurriculo);

        Map<String, Integer> freqVaga = new HashMap<>();
        for (String token : tokensVaga) {
            freqVaga.merge(token, 1, Integer::sum);
        }

        return freqVaga.entrySet().stream()
                .filter(e -> !presencaCurriculo.contains(e.getKey()))
                .filter(e -> e.getKey().length() > 3)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(15)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<String> tokenizar(String texto) {
        if (texto == null || texto.isBlank()) return Collections.emptyList();

        String semAcentos = TextUtils.removeAccents(texto);
        if (semAcentos == null) return Collections.emptyList();

        String limpo = TextUtils.cleanText(semAcentos.toLowerCase());
        if (limpo == null || limpo.isBlank()) return Collections.emptyList();
        return Arrays.stream(limpo.split("[\s\p{P}]+"))
                .filter(t -> !t.isEmpty() && t.length() > 1 && !STOPWORDS.contains(t))
                .collect(Collectors.toList());
    }

    private Map<String, Double> calcularTFIDF(List<String> tokens, Set<String> vocabulario) {
        Map<String, Integer> freq = new HashMap<>();
        for (String t : tokens) freq.merge(t, 1, Integer::sum);
        double total = Math.max(tokens.size(), 1);
        Map<String, Double> tfidf = new HashMap<>();
        for (String termo : vocabulario) {
            tfidf.put(termo, freq.getOrDefault(termo, 0) / total);
        }
        return tfidf;
    }

    private double cossenoSimilaridade(Map<String, Double> v1, Map<String, Double> v2) {
        Set<String> allKeys = new HashSet<>(v1.keySet());
        allKeys.addAll(v2.keySet());
        double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (String key : allKeys) {
            double a = v1.getOrDefault(key, 0.0);
            double b = v2.getOrDefault(key, 0.0);
            dot += a * b;
            norm1 += a * a;
            norm2 += b * b;
        }
        if (norm1 == 0 || norm2 == 0) return 0;
        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
